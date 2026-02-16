package lucis.lux.hff.interactions;


import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.event.IEventDispatcher;
import com.hypixel.hytale.protocol.InteractionState;
import com.hypixel.hytale.protocol.InteractionType;
import com.hypixel.hytale.server.core.HytaleServer;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.entity.InteractionContext;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.modules.interaction.interaction.CooldownHandler;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.RootInteraction;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.SimpleInstantInteraction;
import com.hypixel.hytale.server.core.universe.Universe;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import lucis.lux.hff.HFF;
import lucis.lux.hff.components.FirearmStatsComponent;
import lucis.lux.hff.interactions.events.OnCheckTimeout;
import lucis.lux.hff.util.ComponentRefResult;
import lucis.lux.hff.util.ConfigManager;
import lucis.lux.hff.util.EnsureEntity;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

import java.util.UUID;

/**
 * The {@code CheckCooldownInteraction} class is responsible for checking whether a firearm is currently on cooldown.
 * This interaction is triggered before a firearm is fired to ensure that the player cannot shoot faster than the
 * firearm's specified rate of fire.
 *
 * <p>This interaction performs the following steps:</p>
 * <ul>
 *     <li>Ensures that the firearm's {@link FirearmStatsComponent} is present and valid.</li>
 *     <li>Dispatches an {@link OnCheckTimeout} event to notify other systems about the cooldown check.</li>
 *     <li>Checks if the firearm is on cooldown using the {@link CooldownHandler}.</li>
 *     <li>If the firearm is not on cooldown, it proceeds to the next interaction, typically {@code hff:shoot_firearm}.</li>
 * </ul>
 *
 * <p>This interaction is designed to work with the HFF (Hytale Firearm Framework) plugin and is
 * usually used in conjunction with the {@link ShootFirearmInteraction} class.</p>
 *
 * @see FirearmStatsComponent
 * @see OnCheckTimeout
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
     *     <li>Retrieves the player and the firearm's {@link FirearmStatsComponent}.</li>
     *     <li>Dispatches an {@link OnCheckTimeout} event to notify other systems.</li>
     *     <li>Checks if the firearm is newly created or disabled. If so, the interaction fails.</li>
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

        // Ensure the firearm's component is present and valid
        ComponentRefResult<FirearmStatsComponent> result = EnsureEntity.get(interactionContext, FirearmStatsComponent.class, HFF.get().getFirearmStatsComponentType(), interactionContext.getHeldItem().getItemId());

        // Dispatch the OnCheckTimeout event
        IEventDispatcher<OnCheckTimeout, OnCheckTimeout> dispatcher = HytaleServer.get().getEventBus().dispatchFor(OnCheckTimeout.class);

        if (dispatcher.hasListener()) {
            OnCheckTimeout event = new OnCheckTimeout("data");
            dispatcher.dispatch(event);
        }

        // If the component was newly created or is disabled, fail the interaction
        if (result.newlyCreated() || result.disabled()) {
            interactionContext.getState().state = InteractionState.Failed;
            return;
        }

        FirearmStatsComponent stats = result.component();

        // Retrieve the UUID associated with the firearm
        UUID uuid = interactionContext.getHeldItem().getFromMetadataOrNull("HFF_METADATA", Codec.UUID_BINARY);
        if (uuid != null) {
            // Check if the firearm is on cooldown
            if (cooldownHandler.isOnCooldown(new RootInteraction(), uuid.toString(), stats.getCooldown(), new float[]{stats.getCooldown()}, false)) {
                if (ConfigManager.isDebugMode()) {
                    player.sendMessage(Message.raw("Max Cooldown: " + cooldownHandler.getCooldown(uuid.toString()).getCooldown() + " TPS: " + Universe.get().getDefaultWorld().getTps()));
                }
                interactionContext.getState().state = InteractionState.Failed;
                return;
            }
        }

        // Proceed to the next interaction if the firearm is not on cooldown
        this.next = "hff:shoot_firearm";
    }
}
