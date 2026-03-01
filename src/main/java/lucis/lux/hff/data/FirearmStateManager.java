package lucis.lux.hff.data;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * The {@code FirearmStateManager} class serves as a central manager for tracking and managing
 * the state of firearms in the HFF (Hytale Firearm Framework) plugin. This class provides methods
 * to register, retrieve, and update the state of firearms using their unique UUIDs.
 *
 * <p>This manager is typically used to persist and manage the operational state of firearms,
 * such as loaded projectiles, across different game sessions or interactions.</p>
 *
 * <p>Example usage:</p>
 * <pre>
 *     // Register a new firearm state
 *     UUID weaponId = UUID.randomUUID();
 *     FirearmState state = new FirearmState();
 *     FirearmStateManager.registerState(weaponId, state);
 *
 *     // Retrieve a firearm state
 *     FirearmState retrievedState = FirearmStateManager.getState(weaponId);
 *
 *     // Update a firearm state
 *     FirearmStateManager.updateState(weaponId, state);
 *
 *     // Get a copy of the entire state map
 *     Map&lt;UUID, FirearmState&gt; stateMap = FirearmStateManager.getStateMap();
 * </pre>
 *
 * @see FirearmState
 */
public class FirearmStateManager {

    /**
     * A map that stores the state of firearms, keyed by their unique UUIDs.
     * This map is static to ensure that the state is globally accessible.
     */
    private static final Map<UUID, FirearmState> STATEMAP = new HashMap<>();

    /**
     * Registers a new firearm state in the manager.
     * If a state already exists for the given weapon ID, it will be overwritten.
     *
     * @param weaponId The unique UUID of the weapon.
     * @param state    The state of the firearm to register.
     */
    public static void registerState(UUID weaponId, FirearmState state) {
        STATEMAP.put(weaponId, state);
    }

    /**
     * Retrieves the state of a firearm by its unique UUID.
     *
     * @param weaponId The unique UUID of the weapon.
     * @return The state of the firearm, or {@code null} if no state is registered for the given UUID.
     */
    public static FirearmState getState(UUID weaponId) {
        return STATEMAP.get(weaponId);
    }

    /**
     * Updates the state of a firearm in the manager.
     * If no state exists for the given weapon ID, a new entry will be created.
     *
     * @param weaponId The unique UUID of the weapon.
     * @param state    The new state of the firearm.
     */
    public static void updateState(UUID weaponId, FirearmState state) {
        STATEMAP.put(weaponId, state);
    }

    /**
     * Returns a copy of the entire state map.
     * The returned map is a new instance, so modifications to it will not affect the internal state map.
     *
     * @return A copy of the map containing all registered firearm states.
     */
    public static Map<UUID, FirearmState> getStateMap() {
        return new HashMap<>(STATEMAP);
    }
}
