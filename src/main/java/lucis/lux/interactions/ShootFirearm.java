package lucis.lux.interactions;

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
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

public class ShootFirearm extends SimpleInstantInteraction {
    public static final BuilderCodec<ShootFirearm> CODEC = BuilderCodec.builder(ShootFirearm.class, ShootFirearm::new, SimpleInstantInteraction.CODEC).build();

    @Override
    protected void firstRun(@NonNullDecl InteractionType interactionType, @NonNullDecl InteractionContext interactionContext, @NonNullDecl CooldownHandler cooldownHandler) {
        CommandBuffer<EntityStore> commandBuffer = interactionContext.getCommandBuffer();
        if (commandBuffer == null) {
            interactionContext.getState().state = InteractionState.Failed;
            return;
        }

        Ref<EntityStore> ref = interactionContext.getEntity();
        Player player = commandBuffer.getComponent(ref, Player.getComponentType());

        if (player == null) {
            interactionContext.getState().state = InteractionState.Failed;
            return;
        }

        ItemStack itemStack = interactionContext.getHeldItem();
        if (itemStack == null) {
            interactionContext.getState().state = InteractionState.Failed;
            return;
        }

        player.sendMessage(Message.raw("Bang - the gun says"));
    }
}
