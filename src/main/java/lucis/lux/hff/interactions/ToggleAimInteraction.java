package lucis.lux.hff.interactions;

import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.protocol.InteractionType;
import com.hypixel.hytale.server.core.entity.InteractionContext;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.modules.interaction.interaction.CooldownHandler;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.SimpleInstantInteraction;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import lucis.lux.hff.HFF;
import lucis.lux.hff.components.AimComponent;
import lucis.lux.hff.ui.AimHUD;
import lucis.lux.hff.ui.EmptyHUD;
import lucis.lux.hff.util.ConfigManager;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

public class ToggleAimInteraction extends SimpleInstantInteraction {
    public static final BuilderCodec<ToggleAimInteraction> CODEC = BuilderCodec
            .builder(ToggleAimInteraction.class, ToggleAimInteraction::new)
            .build();

    @Override
    protected void firstRun(@NonNullDecl InteractionType interactionType, @NonNullDecl InteractionContext interactionContext, @NonNullDecl CooldownHandler cooldownHandler) {
        CommandBuffer<EntityStore> commandBuffer = interactionContext.getCommandBuffer();
        Ref<EntityStore> ref = interactionContext.getEntity();
        commandBuffer.getComponent(ref, Player.getComponentType());
        Player player = commandBuffer.getComponent(ref, Player.getComponentType());
        PlayerRef playerRef = commandBuffer.getComponent(ref, PlayerRef.getComponentType());

        AimComponent aimComponent = commandBuffer.getComponent(ref, HFF.get().getAimComponentType());
        if (aimComponent == null) {
            aimComponent = commandBuffer.addComponent(ref, HFF.get().getAimComponentType());

            player.getHudManager().setCustomHud(playerRef, new AimHUD(playerRef));

        } else {
            if (aimComponent.isAiming()) {
                aimComponent.setAiming(false);
                player.getHudManager().setCustomHud(playerRef, new EmptyHUD(playerRef));
            } else {
                aimComponent.setAiming(true);
                player.getHudManager().setCustomHud(playerRef, new AimHUD(playerRef));
            }
        }

        if (ConfigManager.isDebugMode()) {
            HFF.get().getLogger().atInfo().log("Toggled aim mode");
        }
    }
}
