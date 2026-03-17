package lucis.lux.hff.interactions;


import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.protocol.InteractionState;
import com.hypixel.hytale.protocol.InteractionType;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.entity.InteractionContext;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.modules.interaction.interaction.CooldownHandler;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.RootInteraction;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.SimpleInstantInteraction;
import com.hypixel.hytale.server.core.universe.Universe;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import lucis.lux.hff.HFF;
import lucis.lux.hff.data.FirearmState;
import lucis.lux.hff.data.FirearmStats;
import lucis.lux.hff.data.registry.Registries;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

import java.util.UUID;

/**
 * The {@code CheckCooldownInteraction} class is a {@link SimpleInstantInteraction} responsible for checking
 * whether a firearm is currently on cooldown. This interaction is triggered before a firearm is fired to ensure
 * that the player cannot shoot faster than the firearm's specified rate of fire.
 *
 * <p>This interaction performs the following steps:</p>
 * <ul>
 *     <li>Checks if the firearm is registered at hte {@link }. If not, the interaction fails.</li>
 *     <li>Retrieves the UUID associated with the firearm from its metadata.</li>
 *     <li>Checks if the firearm is on cooldown using the {@link CooldownHandler}.</li>
 *     <li>If the firearm is not on cooldown, it proceeds to the next interaction, typically {@code hff:shoot_firearm}.</li>
 * </ul>
 *
 * <p>This interaction is designed to work with the HFF (Hytale Firearm Framework) plugin and is
 * usually used in conjunction with the {@link ShootFirearmInteraction} class.</p>
 *
 * @see ShootFirearmInteraction
 */
public class CheckCooldownInteraction extends SimpleInstantInteraction {

    /**
     * The {@link BuilderCodec} for serializing and deserializing the {@code CheckCooldownInteraction}.S
     */
    public static final BuilderCodec<CheckCooldownInteraction> CODEC = BuilderCodec.builder(CheckCooldownInteraction.class, CheckCooldownInteraction::new, SimpleInstantInteraction.CODEC).build();


    /**
     * Executes the cooldown check logic for the firearm.
     *
     * <p>This method performs the following steps:</p>
     * <ol>
     *     <li>Checks if the firearm is registered at the {@link }. If not, the interaction fails.</li>
     *     <li>Retrieves the UUID associated with the firearm from its metadata.</li>
     *     <li>Checks if the firearm is on cooldown using the {@link CooldownHandler}.</li>
     *     <li>If the firearm is not on cooldown, the interaction proceeds to the next step, typically {@code hff:shoot_firearm}.</li>
     * </ol>
     *
     * @param interactionType    The type of interaction.
     * @param interactionContext The context of the interaction, including the player and the held item.
     * @param cooldownHandler    The cooldown handler used to check if the firearm is in cooldown.
     */
    @Override
    protected void firstRun(@NonNullDecl InteractionType interactionType, @NonNullDecl InteractionContext interactionContext, @NonNullDecl CooldownHandler cooldownHandler) {
        CommandBuffer<EntityStore> commandBuffer = interactionContext.getCommandBuffer();
        Ref<EntityStore> ref = interactionContext.getEntity();
        Player player = commandBuffer.getComponent(ref, Player.getComponentType());

        if (HFF.get().getConfigData().isDebugMode()) {
            player.sendMessage(Message.raw("At CheckCooldownInteraction"));
        }

        // Retrieve the firearm's statistics
        FirearmStats stats = Registries.FIREARM_STATS.get(interactionContext.getHeldItem().getItemId());

        if (stats == null) {
            if (HFF.get().getConfigData().isDebugMode()) {
                player.sendMessage(Message.raw("Did not find any stats for this item"));
            }
            interactionContext.getState().state = InteractionState.Failed;
            return;
        }


        // Retrieve the UUID associated with the firearm
        UUID weaponUuid = interactionContext.getHeldItem().getFromMetadataOrNull("HFF_STATE", Codec.UUID_BINARY);
        if (weaponUuid != null) {
            // Check if the firearm is on cooldown
            if (cooldownHandler.isOnCooldown(new RootInteraction(), weaponUuid.toString(), stats.getCooldown(), new float[]{stats.getCooldown()}, false)) {
                if (HFF.get().getConfigData().isDebugMode()) {
                    player.sendMessage(Message.raw("Max Cooldown: " + cooldownHandler.getCooldown(weaponUuid.toString()).getCooldown() + " TPS: " + Universe.get().getDefaultWorld().getTps()));
                }
                interactionContext.getState().state = InteractionState.Failed;
            } else if (HFF.get().getConfigData().isDebugMode()) {
                player.sendMessage(Message.raw("Not on cooldown"));
            }
        } else {
            // Create a new UUID and state for the weapon if it doesn't have one
            ItemStack item = interactionContext.getHeldItem();
            weaponUuid = UUID.randomUUID();
            interactionContext.setHeldItem(item.withMetadata("HFF_STATE", Codec.UUID_BINARY, weaponUuid));
            player.getInventory().getHotbar().replaceItemStackInSlot(interactionContext.getHeldItemSlot(), item, interactionContext.getHeldItem());
            Registries.FIREARM_STATES.register(weaponUuid, new FirearmState());
            if (HFF.get().getConfigData().isDebugMode()) {
                player.sendMessage(Message.raw("Created a new state for the weapon"));
            }
        }

        // Proceed to the next interaction if the firearm is not on cooldown
        // this.next = "hff:shoot_firearm";
    }
}