package lucis.lux.systems;

import com.hypixel.hytale.component.*;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.tick.EntityTickingSystem;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import lucis.lux.components.FirearmStatsComponent;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

public class FirearmSystem extends EntityTickingSystem<EntityStore> {

    private final ComponentType<EntityStore, FirearmStatsComponent> firearmStatsComponentType;

    public FirearmSystem(ComponentType<EntityStore, FirearmStatsComponent> firearmStatsComponentType) {
        this.firearmStatsComponentType = firearmStatsComponentType;
    }

    @Override
    public void tick(float v, int index, @NonNullDecl ArchetypeChunk archetypeChunk, @NonNullDecl Store store, @NonNullDecl CommandBuffer commandBuffer) {
        FirearmStatsComponent firearmStats = (FirearmStatsComponent) archetypeChunk.getComponent(index, firearmStatsComponentType);
        Ref<EntityStore> ref = archetypeChunk.getReferenceTo(index);

        assert firearmStats != null;
        firearmStats.increaseElapsedTime(v);
    }

    @NullableDecl
    @Override
    public Query getQuery() {
        return Query.and(firearmStatsComponentType);
    }
}
