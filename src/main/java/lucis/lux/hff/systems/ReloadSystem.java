package lucis.lux.hff.systems;

import com.hypixel.hytale.component.*;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.tick.EntityTickingSystem;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.entity.movement.MovementStatesComponent;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import lucis.lux.hff.HFF;
import lucis.lux.hff.components.ReloadingComponent;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

/**
 * The {@code ReloadSystem} class is an {@link EntityTickingSystem} responsible for monitoring and managing
 * the reloading process of a firearm in real-time. This system checks, for player movement
 * and interrupts the reloading process if necessary.
 *
 * <p>This system performs the following tasks during each tick:
 * <ul>
 *     <li>Checks if the player is moving (sprinting or jumping) using the {@link MovementStatesComponent}.</li>
 *     <li>Interrupts the reloading process if the player moves.</li>
 * </ul></p>
 *
 * <p>The system is designed to work within the Entity Component System (ECS) architecture of Hytale
 * and is registered during plugin initialization. It only ticks entities that have a {@link ReloadingComponent}.</p>
 *
 * <p>This system is part of the HFF (Hytale Firearm Framework) plugin and is used in conjunction with
 * the {@link ReloadingComponent} to provide realistic firearm reloading mechanics.</p>
 *
 * @see EntityTickingSystem
 * @see ReloadingComponent
 * @see MovementStatesComponent
 */
public class ReloadSystem extends EntityTickingSystem<EntityStore> {
    /**
     * The component type for ammunition, used to identify entities with reloading components.
     */
    private final ComponentType<EntityStore, ReloadingComponent> reloadingComponentType;

    /**
     * Constructs a new {@code ReloadSystem} with the specified component type for ammunition.
     *
     * @param reloadingComponentType The component type for ammunition.
     */
    public ReloadSystem(ComponentType<EntityStore, ReloadingComponent> reloadingComponentType) {
        this.reloadingComponentType = reloadingComponentType;
    }

    /**
     * Ticks the system for each entity with a {@link ReloadingComponent}. This method checks if the player
     * is moving and interrupts the reloading process if necessary.
     *
     * <p>The following steps are performed during each tick:</p>
     * <ol>
     *     <li>Retrieves the {@link ReloadingComponent} for the entity.</li>
     *     <li>Gets the player reference and checks if it is valid.</li>
     *     <li>Gets the player's {@link MovementStatesComponent} to check for movement.</li>
     *     <li>If the player is moving (sprinting or jumping), the reloading process is interrupted.</li>
     * </ol>
     *
     * @param v              The delta time since the last tick.
     * @param i              The index of the entity in the archetype chunk.
     * @param archetypeChunk The chunk of entities of the same archetype.
     * @param store          The component store.
     * @param commandBuffer  The command buffer for applying changes.
     */
    @Override
    public void tick(float v, int i, @NonNullDecl ArchetypeChunk archetypeChunk, @NonNullDecl Store store, @NonNullDecl CommandBuffer commandBuffer) {

        ReloadingComponent reloading = (ReloadingComponent) archetypeChunk.getComponent(i, reloadingComponentType);

        Ref<EntityStore> ref = archetypeChunk.getReferenceTo(i);

        if (!ref.isValid()) {
            return;
        }

        Player player = (Player) commandBuffer.getComponent(ref, Player.getComponentType());

        MovementStatesComponent movementState = (MovementStatesComponent) store.getComponent(ref, MovementStatesComponent.getComponentType());

        if (movementState != null && (movementState.getMovementStates().jumping || movementState.getMovementStates().sprinting) && reloading.isReloading()) {
            reloading.setReloading(false);
            if (HFF.get().getConfigData().isDebugMode()) {
                player.sendMessage(Message.raw("Reload interrupted: player sprinted or jumped"));
            }
        }
    }

    /**
     * Defines the query for selecting entities to tick. This system only ticks entities that have a
     * {@link ReloadingComponent}.
     *
     * @return The query for selecting entities.
     */
    @NullableDecl
    @Override
    public Query getQuery() {
        return Query.and(reloadingComponentType);
    }
}