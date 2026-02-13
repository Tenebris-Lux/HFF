package lucis.lux.hff.ui;

import com.hypixel.hytale.server.core.entity.entities.player.hud.CustomUIHud;
import com.hypixel.hytale.server.core.ui.builder.UICommandBuilder;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

/**
 * The {@code EmptyHUD} class is a utility class designed to hide custom HUDs (Heads-Up Displays)
 * by replacing them with an empty HUD. This class extends {@link CustomUIHud} but does not append
 * any UI elements to the builder, effectively creating an invisible HUD.
 *
 * <p>This class is particular useful in scenarios where a custom HUD needs to be temporarily
 * or permanently hidden without removing the HUD system entirely. By replacing an existing HUD
 * with an instance of this class, all visual elements are removed from the player's screen.</p>
 *
 * <p>Example use cases include:</p>
 * <ul>
 *     <li>Hiding the aiming HUD when the player exits aim mode.</li>
 *     <li>Temporarily disabling HUD elements during cutscenes or specific gameplay events.</li>
 *     <li>Providing a clean way to toggle HUD visibility without complex logic.</li>
 * </ul>
 *
 * <p>To hide a custom HUD, replace it with an instance of this class: For example:</p>
 * <pre>
 *     // Replace the current HUD with an empty HUD to hide it
 *     playerRef.getHudManager().setCustomHud(new EmptyHUD(playerRef));
 * </pre>
 *
 * @see CustomUIHud
 * @see UICommandBuilder
 * @see PlayerRef
 */
public class EmptyHUD extends CustomUIHud {
    /**
     * Constructs a new {@code EmptyHUD} for the specified player.
     * This HUD will not display any visual elements when shown.
     *
     * @param playerRef The reference to the player for whom this HUD is being created.
     */
    public EmptyHUD(@NonNullDecl PlayerRef playerRef) {
        super(playerRef);
    }

    /**
     * Builds the UI elements for the HUD.
     * This method is intentionally left empty, so no UI elements are appended to the builder.
     * As a result, this HUD will not display any visual elements when shown, effectively hiding
     * any previously displayed HUD.
     *
     * @param uiCommandBuilder The UI command builder used to construct the HUD.
     */
    @Override
    protected void build(@NonNullDecl UICommandBuilder uiCommandBuilder) {
        // Intentionally left empty to create an empty HUD and hide any existing HUD elements
    }
}
