package lucis.lux.hff.data;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

import java.util.LinkedList;

/**
 * The {@code FirearmState} class represents the current state of a firearm, including its owner
 * and the projectiles currently loaded into it. This class is used to track the operational state
 * of a firearm, such as the number of loaded projectiles and the entity that owns the firearm.
 *
 * <p>This class is typically used in the HFF (Hytale Firearm Framework) plugin to manage the state
 * of firearms during gameplay. It allows for tracking which projectiles are loaded
 * and which entity currently owns the firearm.</p>
 *
 * <p>Example usage:</p>
 * <pre>
 *     FirearmState state = new FirearmState();
 *     state.setOwner(playerRef):
 *     state.loadProjectile("9mm_projectile");
 *     String nextProjectile = state.consumeNextProjectile();
 * </pre>
 */
public class FirearmState {

    /**
     * A list of projectiles currently loaded into the firearm.
     * The list is implemented as a {@link LinkedList} to allow efficient addition and removal of projectiles.
     */
    private final LinkedList<String> loadedProjectiles;

    /**
     * A reference to the entity that currently owns this firearm.
     */
    private Ref<EntityStore> owner;

    /**
     * Constructs a new {@code FirearmState} with no owner and an empty list of loaded projectiles.
     */
    public FirearmState() {
        this.loadedProjectiles = new LinkedList<>();
        this.owner = null;
    }

    /**
     * Returns the reference to the entity that currently owns this firearm.
     *
     * @return The reference to the owner entity, or {@code null} if there is no owner.
     */
    public Ref<EntityStore> getOwner() {
        return owner;
    }

    /**
     * Sets the reference to the entity that owns this firearm.
     *
     * @param owner The reference to the owner entity.
     */
    public void setOwner(Ref<EntityStore> owner) {
        this.owner = owner;
    }

    /**
     * Returns the current number of loaded projectiles in the firearm.
     *
     * @return The number of loaded projectiles.
     */
    public int getCurrentAmmoCount() {
        return loadedProjectiles.size();
    }

    /**
     * Loads a projectile into the firearm.
     * The projectile is added to the front of the list of loaded projectiles.
     *
     * @param projectileId The ID of the projectile to load.
     */
    public void loadProjectile(String projectileId) {
        loadedProjectiles.push(projectileId);
    }

    /**
     * Consumes the next projectile from the firearm.
     * The projectile is removed from the front of the list of loaded projectiles.
     *
     * @return The ID of the next projectile, or {@code null} if there are no projectiles loaded.
     */
    public String consumeNextProjectile() {
        return loadedProjectiles.poll();
    }
}
