package lucis.lux.util;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Resource;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class RefKeeper implements Resource<EntityStore> {

    private static final Map<UUID, Ref<EntityStore>> entityMap = new HashMap<>();

    @NullableDecl
    @Override
    public Resource<EntityStore> clone() {
        return this;
    }

    public Ref<EntityStore> getRef(UUID uuid) {
        return entityMap.get(uuid);
    }

    public void setRef(UUID uuid, Ref<EntityStore> ref) {
        entityMap.put(uuid, ref);
    }

    // Deletes only if the referenced entity has been deleted beforehand.
    public boolean deleteRef(UUID uuid) {
        Ref<EntityStore> ref = entityMap.get(uuid);
        if (ref.isValid()) {
            return false;
        } else {
            entityMap.remove(uuid);
            return true;
        }
    }

}
