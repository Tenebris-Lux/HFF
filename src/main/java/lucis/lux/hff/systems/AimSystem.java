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

/**
 * The {@code AimSystem} class is an {@link EntityTickingSystem} responsible for managing the aiming mechanics
 * of entities that have an {@link AimComponent}. This system adjusts the camera settings and other visual effects
 * when a player aims down the sights of a firearm.
 *
 * <p>This system performs the following tasks during each tick:</p>
 * <ul>
 *     <li>Checks if the entity is currently aiming using the {@link AimComponent}.</li>
 *     <li>Adjusts the camera settings to provide a first-person perspective when aiming.</li>
 *     <li>Resets the camera settings to default when the player stops aiming.</li>
 *     <li>Applies additional visual effects, such as reticles or vignettes, to enhance the aiming experience.</li>
 * </ul>
 *
 * <p>This system is part of the Entity Component System (ECS) architecture in Hytale and is registered
 * during plugin initialization. It only ticks entities that have an {@link AimComponent}.</p>
 *
 * <p>This system is typically used in conjunction with the {@link AimComponent} and is triggered by
 * interactions such as {@link lucis.lux.hff.interactions.ToggleAimInteraction}.</p>
 *
 * @see EntityTickingSystem
 * @see AimComponent
 * @see PlayerRef
 * @see ServerCameraSettings
 */
public class AimSystem extends EntityTickingSystem {

    /**
     * The component type for the {@link AimComponent}.
     */
    private final ComponentType<EntityStore, AimComponent> aimComponentType;

    /**
     * Constructs a new {@code AimSystem} with the specified component type for aiming.
     *
     * @param aimComponentType The component type for the {@link AimComponent}.
     */
    public AimSystem(ComponentType<EntityStore, AimComponent> aimComponentType) {
        this.aimComponentType = aimComponentType;
    }

    /**
     * Ticks the system, updating the camera settings for all entities with an {@link AimComponent}.
     * This method is called at regular intervals and adjusts the camera settings based on whether
     * the entity is currently aiming.
     *
     * <p>The following adjustments are made when aiming:</p>
     * <ul>
     *     <li>The camera is set to first-person view.</li>
     *     <li>The movement speed is reduced to simulate the slower movement while aiming.</li>
     *     <li>The camera position and rotation are smoothly interpolated for a natural feel.</li>
     *     <li>Additional visual effects, such as reticles or vignettes, can be added here.</li>
     * </ul>
     *
     * @param v              The time delta since the last tick.
     * @param i              The index of the entity in the archetype chunk.
     * @param archetypeChunk The archetype chunk containing all the entities.
     * @param store          The entity store.
     * @param commandBuffer  The command buffer applying changes to entities.
     */
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

    /**
     * Defines the query for this system, specifying that it should process entities
     * that have an {@link AimComponent}.
     *
     * @return A query that matches entities with the {@link AimComponent}.
     */
    @NullableDecl
    @Override
    public Query getQuery() {
        return Query.and(aimComponentType);
    }
}
