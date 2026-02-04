package lucis.lux.hff.ui;

import com.hypixel.hytale.server.core.entity.entities.player.hud.CustomUIHud;
import com.hypixel.hytale.server.core.ui.builder.UICommandBuilder;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

public class EmptyHUD extends CustomUIHud {
    public EmptyHUD(@NonNullDecl PlayerRef playerRef) {
        super(playerRef);
    }

    @Override
    protected void build(@NonNullDecl UICommandBuilder uiCommandBuilder) {

    }
}
