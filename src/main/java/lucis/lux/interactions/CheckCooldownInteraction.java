package lucis.lux.interactions;


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
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.SimpleInstantInteraction;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import lucis.lux.HFF;
import lucis.lux.components.FirearmStatsComponent;
import lucis.lux.util.FirearmStatsAttacher;
import lucis.lux.util.RefKeeper;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

import java.util.UUID;

public class CheckCooldownInteraction extends SimpleInstantInteraction {

    public static final BuilderCodec<CheckCooldownInteraction> CODEC = BuilderCodec.builder(CheckCooldownInteraction.class, CheckCooldownInteraction::new, SimpleInstantInteraction.CODEC).build();


    @Override
    protected void firstRun(@NonNullDecl InteractionType interactionType, @NonNullDecl InteractionContext interactionContext, @NonNullDecl CooldownHandler cooldownHandler) {
        CommandBuffer<EntityStore> commandBuffer = interactionContext.getCommandBuffer();
        Ref<EntityStore> ref = interactionContext.getEntity();
        Player player = commandBuffer.getComponent(ref, Player.getComponentType());
        ItemStack item = interactionContext.getHeldItem();

        RefKeeper keeper = commandBuffer.getResource(HFF.getRefKeeper());

        FirearmStatsComponent stats;

        try {
            UUID uuid = item.getFromMetadataOrNull("HFF_FIREARM_STATS_COMPONENT", Codec.UUID_BINARY);
            Ref<EntityStore> itemEntityRef = keeper.getRef(uuid);
            stats = commandBuffer.getComponent(itemEntityRef, HFF.get().getFirearmStatsComponentType());
        } catch (NullPointerException e) {

            interactionContext.setHeldItem(FirearmStatsAttacher.attachFirearmStats(ref, commandBuffer, item));
            // I have to forcefully replace the gun, setHeldItem does nothing
            player.getInventory().getHotbar().replaceItemStackInSlot(interactionContext.getHeldItemSlot(), item, interactionContext.getHeldItem());
            FirearmStatsComponent stats3 = interactionContext.getHeldItem().getFromMetadataOrNull(FirearmStatsComponent.KEY);

            return;
        }


        if (!stats.isTimeElapsed()) {
            player.sendMessage(Message.raw("Remaining Cooldown: " + stats.getRemainingTime()));
            interactionContext.getState().state = InteractionState.Failed;
            return;
        }
        player.sendMessage(Message.raw("Remaining Cooldown: " + stats.getRemainingTime() + "\n RPM: " + stats.getRpm()));
        stats.resetElapsedTime();
    }
}
