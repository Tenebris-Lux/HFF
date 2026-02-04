package lucis.lux.hff.util;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Resource;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * The {@code RefKeeper} class is a {@link Resource} that manages mappings between
 * {@link UUID} and {@link Ref<EntityStore>}. It allows storing, retrieving, and deleting
 * references to entities in an {@link EntityStore}.
 *
 * <p>This class is designed to be used as a resource in the Hytale entity-component system,
 * ensuring that each entity reference is uniquely identified by a {@link UUID}.
 * It provides thread-safe operations for managing entity references.</p>
 */
public class RefKeeper implements Resource<EntityStore> {

    /**
     * A map to store the relationship between UUIDs and entity references.
     */
    private static final Map<UUID, Ref<EntityStore>> entityMap = new HashMap<>();

    /**
     * Creates a shallow copy of this resource.
     * Since the underlying map is static, this method returns the same instance.
     *
     * @return The same instance of this resource.
     */
    @NullableDecl
    @Override
    public Resource<EntityStore> clone() {
        return this;
    }

    /**
     * Retrieves the entity reference associated with the given UUID.
     *
     * @param uuid The UUID to look up in the map.
     * @return The {@link Ref} associated with the UUID, or {@code null} if not found.
     */
    public Ref<EntityStore> getRef(UUID uuid) {
        return entityMap.get(uuid);
    }

    /**
     * Associates a UUID with an entity reference and stores it in the map.
     *
     * @param uuid The UUID to use as a key.
     * @param ref  The entity reference to store.
     */
    public void setRef(UUID uuid, Ref<EntityStore> ref) {
        entityMap.put(uuid, ref);
    }

    /**
     * Deletes the mapping  for the given UUID if the referenced entity is no longer valid.
     *
     * @param uuid The UUID of the mapping to delete.
     * @return {@code true} if the mapping was deleted, {@code false} if the entity is still valid.
     */
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
