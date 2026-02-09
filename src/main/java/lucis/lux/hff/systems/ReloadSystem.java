package lucis.lux.hff.systems;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.component.*;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.tick.EntityTickingSystem;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.entity.movement.MovementStatesComponent;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import lucis.lux.hff.HFF;
import lucis.lux.hff.components.AmmoComponent;
import lucis.lux.hff.components.FirearmStatsComponent;
import lucis.lux.hff.resources.RefKeeper;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

import java.util.UUID;

/**
 * The {@code ReloadSystem} class is an {@link EntityTickingSystem} responsible for monitoring and managing
 * the reloading process of a firearm in real-time. This system checks, for player movement or weapon changes
 * and interrupts the reloading process if necessary.
 *
 * <p>This system performs the following tasks during each tick:
 * <ul>
 *     <li>Checks if the player is moving (not idle) using the {@link MovementStatesComponent}.</li>
 *     <li>Verifies if the player has switched weapons by comparing the current weapon's UUID with the stored reference.</li>
 *     <li>Interrupts the reloading process if either of the above conditions is met.</li>
 * </ul></p>
 *
 * <p>The system is designed to work within the Entity Component System (ECS) architecture of Hytale
 * and is registered during plugin initialization. It only ticks entities that have an {@link AmmoComponent}.</p>
 */
public class ReloadSystem extends EntityTickingSystem {
    private final ComponentType<EntityStore, AmmoComponent> ammoComponentType;

    /**
     * Constructs a new {@code ReloadSystem} with the specified component type for ammunition.
     *
     * @param ammoComponentType The component type for ammunition.
     */
    public ReloadSystem(ComponentType<EntityStore, AmmoComponent> ammoComponentType) {
        this.ammoComponentType = ammoComponentType;
    }

    /**
     * Ticks the system for each entity with an {@link AmmoComponent}. This method checks if the player
     * is moving or has switched weapons and interrupts the reloading process if necessary.
     *
     * @param v              The delta time since the last tick.
     * @param i              The index of the entity in the archetype chunk.
     * @param archetypeChunk The chunk of entities of the same archetype.
     * @param store          The component store.
     * @param commandBuffer  The command buffer for applying changes.
     */
    @Override
    public void tick(float v, int i, @NonNullDecl ArchetypeChunk archetypeChunk, @NonNullDecl Store store, @NonNullDecl CommandBuffer commandBuffer) {
        AmmoComponent ammo = (AmmoComponent) archetypeChunk.getComponent(i, ammoComponentType);
        FirearmStatsComponent firearmStats = (FirearmStatsComponent) archetypeChunk.getComponent(i, HFF.get().getFirearmStatsComponentType());
        Ref<EntityStore> ref = archetypeChunk.getReferenceTo(i);

        Ref<EntityStore> playerRef = firearmStats.getPlayerRef();

        if (playerRef == null || !playerRef.isValid()) return;

        Player player = (Player) commandBuffer.getComponent(playerRef, Player.getComponentType());

        MovementStatesComponent movementState = (MovementStatesComponent) store.getComponent(playerRef, MovementStatesComponent.getComponentType());
        UUID uuid = player.getInventory().getActiveHotbarItem().getFromMetadataOrNull("HFF_METADATA", Codec.UUID_BINARY);
        RefKeeper keeper = (RefKeeper) commandBuffer.getResource(HFF.getRefKeeper());

        if (ammo == null || movementState == null) {
            return; // Skip tick
        }

        if (!movementState.getMovementStates().idle || keeper.getRef(uuid) != ref) {
            ammo.setReloading(false);
        }
    }

    /**
     * Defines the query for selecting entities to tick. This system only ticks entities that have an
     * {@link AmmoComponent}.
     *
     * @return The query for selecting entities.
     */
    @NullableDecl
    @Override
    public Query getQuery() {
        return Query.and(ammoComponentType);
    }
}
