package lucis.lux.interactions;

import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.protocol.InteractionState;
import com.hypixel.hytale.protocol.InteractionType;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.entity.InteractionContext;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.modules.interaction.interaction.CooldownHandler;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.SimpleInstantInteraction;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import lucis.lux.HFF;
import lucis.lux.components.FirearmStatsComponent;
import lucis.lux.util.FirearmStatsAttacher;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

public class ShootFirearmInteraction extends SimpleInstantInteraction {
    public static final BuilderCodec<ShootFirearmInteraction> CODEC = BuilderCodec.builder(ShootFirearmInteraction.class, ShootFirearmInteraction::new, SimpleInstantInteraction.CODEC).build();

    @Override
    protected void firstRun(@NonNullDecl InteractionType interactionType, @NonNullDecl InteractionContext interactionContext, @NonNullDecl CooldownHandler cooldownHandler) {

        CommandBuffer<EntityStore> commandBuffer = interactionContext.getCommandBuffer();
        if (commandBuffer == null) {
            interactionContext.getState().state = InteractionState.Failed;
            return;
        }

        Store<EntityStore> store = commandBuffer.getExternalData().getStore();

        if (store == null) {
            interactionContext.getState().state = InteractionState.Failed;
            return;
        }

        Ref<EntityStore> ref = interactionContext.getEntity();

        Player player = commandBuffer.getComponent(ref, Player.getComponentType());

        FirearmStatsComponent stats = ref.getStore().getComponent(ref, HFF.get().getFirearmStatsComponentType());

        player.sendMessage(Message.raw("bang"));

    }
}
