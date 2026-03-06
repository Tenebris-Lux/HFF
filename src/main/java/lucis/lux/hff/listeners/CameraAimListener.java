package lucis.lux.hff.listeners;

import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.protocol.ClientCameraView;
import com.hypixel.hytale.protocol.MovementForceRotationType;
import com.hypixel.hytale.protocol.ServerCameraSettings;
import com.hypixel.hytale.protocol.Vector3f;
import com.hypixel.hytale.protocol.packets.camera.SetServerCamera;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import lucis.lux.hff.HFF;
import lucis.lux.hff.components.AimComponent;
import lucis.lux.hff.events.FirearmAimEvent;

public class CameraAimListener {

    public static void onAimStateChanged(FirearmAimEvent.Post event){
        Store<EntityStore> store = event.getPlayer().getStore();
        AimComponent aimComponent = store.getComponent(event.getPlayer(), HFF.get().getAimComponentType());
        PlayerRef playerRef = store.getComponent(event.getPlayer(), PlayerRef.getComponentType());

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
}
