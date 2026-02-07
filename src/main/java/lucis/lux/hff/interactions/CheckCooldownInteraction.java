package lucis.lux.hff.interactions;


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
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.SimpleInstantInteraction;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import lucis.lux.hff.HFF;
import lucis.lux.hff.components.FirearmStatsComponent;
import lucis.lux.hff.interactions.events.OnCheckTimeout;
import lucis.lux.hff.util.ComponentRefResult;
import lucis.lux.hff.util.ConfigManager;
import lucis.lux.hff.util.EnsureEntity;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

public class CheckCooldownInteraction extends SimpleInstantInteraction {

    public static final BuilderCodec<CheckCooldownInteraction> CODEC = BuilderCodec.builder(CheckCooldownInteraction.class, CheckCooldownInteraction::new, SimpleInstantInteraction.CODEC).build();


    @Override
    protected void firstRun(@NonNullDecl InteractionType interactionType, @NonNullDecl InteractionContext interactionContext, @NonNullDecl CooldownHandler cooldownHandler) {
        CommandBuffer<EntityStore> commandBuffer = interactionContext.getCommandBuffer();
        Ref<EntityStore> ref = interactionContext.getEntity();
        Player player = commandBuffer.getComponent(ref, Player.getComponentType());

        ComponentRefResult<FirearmStatsComponent> result = EnsureEntity.get(interactionContext, FirearmStatsComponent.class, HFF.get().getFirearmStatsComponentType(), interactionContext.getHeldItem().getItemId());

        IEventDispatcher<OnCheckTimeout, OnCheckTimeout> dispatcher = HytaleServer.get().getEventBus().dispatchFor(OnCheckTimeout.class);

        if (dispatcher.hasListener()) {
            OnCheckTimeout event = new OnCheckTimeout("data");
            dispatcher.dispatch(event);
        }

        if (result.newlyCreated() || result.disabled()) {
            interactionContext.getState().state = InteractionState.Failed;
            return;
        }

        FirearmStatsComponent stats = result.component();


        if (!stats.isTimeElapsed()) {
            if (ConfigManager.isDebugMode()) {
                player.sendMessage(Message.raw("Remaining Cooldown: " + stats.getRemainingTime()));
            }
            interactionContext.getState().state = InteractionState.Failed;
            return;
        }

        stats.resetElapsedTime();
        this.next = "hff:shoot_firearm";
    }
}
