package lucis.lux.hff.systems;

import com.hypixel.hytale.component.ArchetypeChunk;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.tick.EntityTickingSystem;
import com.hypixel.hytale.protocol.ClientCameraView;
import com.hypixel.hytale.protocol.MovementForceRotationType;
import com.hypixel.hytale.protocol.ServerCameraSettings;
import com.hypixel.hytale.protocol.Vector3f;
import com.hypixel.hytale.protocol.packets.camera.SetServerCamera;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import lucis.lux.hff.HFF;
import lucis.lux.hff.components.AimComponent;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

public class AimSystem extends EntityTickingSystem {

    private final ComponentType<EntityStore, AimComponent> aimComponentType;

    public AimSystem(ComponentType<EntityStore, AimComponent> aimComponentType) {
        this.aimComponentType = aimComponentType;
    }

    @Override
    public void tick(float v, int i, @NonNullDecl ArchetypeChunk archetypeChunk, @NonNullDecl Store store, @NonNullDecl CommandBuffer commandBuffer) {
        AimComponent aimComponent = (AimComponent) archetypeChunk.getComponent(i, HFF.get().getAimComponentType());
        PlayerRef playerRef = (PlayerRef) archetypeChunk.getComponent(i, PlayerRef.getComponentType());

        if (aimComponent != null && playerRef != null) {
            if (aimComponent.isAiming()) {
                ServerCameraSettings settings = new ServerCameraSettings();
                settings.isFirstPerson = true;
                settings.positionLerpSpeed = 0.2f;
                settings.rotationLerpSpeed = 0.2f;
                settings.displayReticle = false;
                settings.displayCursor = false;
                settings.movementMultiplier = new Vector3f(0.7f, 1.0f, 0.7f);
                settings.eyeOffset = true;
                settings.movementForceRotationType = MovementForceRotationType.CameraRotation;
                //TODO: custom particle effect and vignette
                playerRef.getPacketHandler().writeNoCache(new SetServerCamera(ClientCameraView.Custom, true, settings));
            } else {
                playerRef.getPacketHandler().writeNoCache(new SetServerCamera(ClientCameraView.Custom, false, null));
            }
        }
    }

    @NullableDecl
    @Override
    public Query getQuery() {
        return Query.and(aimComponentType);
    }
}
