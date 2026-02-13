package lucis.lux.hff.ui;

import com.hypixel.hytale.server.core.entity.entities.player.hud.CustomUIHud;
import com.hypixel.hytale.server.core.ui.builder.UICommandBuilder;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

/**
 * The {@code AimHUD} class represents a custom HUD (Heads-Up Display) for aiming in the HFF plugin.
 * This HUD is displayed when a player aims down the sights of a firearm, providing visual feedback
 * such as crosshairs, weapon information, or other relevant UI elements.
 *
 * <p>This class extends {@link CustomUIHud} and is responsible for building the UI elements
 * that are shown when a player is aiming. The UI is defined in a separate file, typically
 * located at "HUD/aim.ui".</p>
 *
 * <p>This HUD is typically shown and hidden in response to player interactions, such as toggling
 * the aim mode via the {@link lucis.lux.hff.interactions.ToggleAimInteraction} class.</p>
 *
 * @see CustomUIHud
 * @see UICommandBuilder
 * @see PlayerRef
 */
public class AimHUD extends CustomUIHud {
    /**
     * Constructs a new {@code AimHUD} for the specified player.
     *
     * @param playerRef The reference to the player for whom this HUD is being created.
     */
    public AimHUD(@NonNullDecl PlayerRef playerRef) {
        super(playerRef);
    }

    /**
     * Builds the UI elements for the aiming HUD.
     * This method appends the UI file "HUD/aim.ui" to the UI command builder, which defines
     * the visual elements and layout of the HUD.
     *
     * @param uiCommandBuilder The UI command builder used to construct the HUD.
     */
    @Override
    protected void build(@NonNullDecl UICommandBuilder uiCommandBuilder) {
        uiCommandBuilder.append("HUD/aim.ui");
    }
}
