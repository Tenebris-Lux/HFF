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

/**
 * The {@code ToggleAimInteraction} class is a {@link SimpleInstantInteraction} responsible for toggling
 * the aiming mode of a player's firearm. This interaction is triggered when a player attempts to aim
 * down the sights of a firearm, toggling the aiming state and updating the HUD accordingly.
 *
 * <p>When triggered, this interaction:</p>
 * <ul>
 *     <li>Retrieves or creates an {@link AimComponent} for the player.</li>
 *     <li>Toggles the aiming state of the player.</li>
 *     <li>Updates the player's HUD to show or hide the aiming interface.</li>
 *     <li>Logs a debug message if debug mode is enabled.</li>
 * </ul>
 *
 * <p>This interaction is part of the Entity Component System (ECS) architecture in Hytale
 * and is registered during plugin initialization.</p>
 */
public class ToggleAimInteraction extends SimpleInstantInteraction {
    /**
     * The {@link BuilderCodec} for serializing and deserializing this interaction.
     */
    public static final BuilderCodec<ToggleAimInteraction> CODEC = BuilderCodec
            .builder(ToggleAimInteraction.class, ToggleAimInteraction::new)
            .build();

    /**
     * Called when the interaction is first run. This method toggles the aiming mode for the player.
     *
     * <p>The following steps are performed:</p>
     * <ol>
     *   <li>Retrieves the player and their {@link PlayerRef}.</li>
     *   <li>Retrieves or creates an {@link AimComponent} for the player.</li>
     *   <li>Toggles the aiming state of the player.</li>
     *   <li>Updates the player's HUD to show or hide the aiming interface.</li>
     *   <li>Logs a debug message if debug mode is enabled.</li>
     * </ol>
     *
     * @param interactionType    The type of interaction.
     * @param interactionContext The context of the interaction, including references to the player and held item.
     * @param cooldownHandler    The handler for managing cooldowns.
     */
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
