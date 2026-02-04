package lucis.lux.hff.systems;

import com.hypixel.hytale.component.*;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.tick.EntityTickingSystem;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import lucis.lux.hff.components.FirearmStatsComponent;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

/**
 * The {@code FirearmSystem} class is an {@link EntityTickingSystem} responsible for updating
 * the state of entities that have a {@link FirearmStatsComponent}. This system is designed to
 * process and tick entities with the {@link FirearmStatsComponent} at regular intervals,
 * updating their elapsed time and handling firearm-related logic.
 *
 * <p>This system is part of the Entity Component System (ECS) architecture in Hytale and
 * is registered during plugin initialisation.</p>
 */
public class FirearmSystem extends EntityTickingSystem<EntityStore> {

    /**
     * The component type for the {@link FirearmStatsComponent}.
     */
    private final ComponentType<EntityStore, FirearmStatsComponent> firearmStatsComponentType;

    /**
     * Constructs a new {@code FirearmSystem} with the specified component type.
     *
     * @param firearmStatsComponentType The component type for the {@link FirearmStatsComponent}.
     */
    public FirearmSystem(ComponentType<EntityStore, FirearmStatsComponent> firearmStatsComponentType) {
        this.firearmStatsComponentType = firearmStatsComponentType;
    }

    /**
     * Ticks the system, updating the elapsed time for all entities with a {@link FirearmStatsComponent}.
     *
     * @param v              The time delta since the last tick.
     * @param index          The index of the entity in the archetype chunk.
     * @param archetypeChunk The archetype chunk containing all the entities.
     * @param store          The entity store.
     * @param commandBuffer  The command buffer for applying changes to entities.
     */
    @Override
    public void tick(float v, int index, @NonNullDecl ArchetypeChunk archetypeChunk, @NonNullDecl Store store, @NonNullDecl CommandBuffer commandBuffer) {
        FirearmStatsComponent firearmStats = (FirearmStatsComponent) archetypeChunk.getComponent(index, firearmStatsComponentType);
        Ref<EntityStore> ref = archetypeChunk.getReferenceTo(index);

        assert firearmStats != null;
        firearmStats.increaseElapsedTime(v);
    }

    /**
     * Defines the query for this system, specifying that it should process entities
     * that have a {@link FirearmStatsComponent}.
     *
     * @return A query that matches entities with the {@link FirearmStatsComponent}.
     */
    @NullableDecl
    @Override
    public Query getQuery() {
        return Query.and(firearmStatsComponentType);
    }
}
