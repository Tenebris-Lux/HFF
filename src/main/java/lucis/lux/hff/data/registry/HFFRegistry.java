package lucis.lux.hff.data.registry;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * The {@code HFFRegistry} class is a generic registry that provides a simple and efficient way
 * to store and retrieve key-value pairs. This class is designed to be used as a central registry
 * for managing data in the HFF (Hytale Firearm Framework) plugin.
 *
 * <p>This registry supports the following operations:</p>
 * <ul>
 *   <li>Registering a key-value pair.</li>
 *   <li>Retrieving a value by its key.</li>
 *   <li>Removing a key-value pair.</li>
 *   <li>Copying the entire registry.</li>
 *   <li>Clearing the registry.</li>
 *   <li>Updating an existing key-value pair.</li>
 * </ul>
 *
 * <p>This class is thread-unsafe. If thread safety is required, external synchronization should be used.</p>
 *
 * <p>Example usage:</p>
 * <pre>
 *     // Create a new registry
 *     HFFRegistry&lt;String, Integer&gt; registry = new HFFRegistry&lt;&gt;();
 *
 *     // Register a key-value pair
 *     registry.register("health", 100);
 *
 *     // Retrieve a value by its key
 *     Integer health = registry.get("health");
 *
 *     // Remove a key-value pair
 *     registry.remove("health");
 *
 *     // Copy the entire registry
 *     Map&lt;String, Integer&gt; registryCopy = registry.copy();
 *
 *     // Clear the registry
 *     registry.clear();
 *
 *     // Update an existing key-value pair
 *     registry.update("health", 200);
 * </pre>
 *
 * @param <K> The type of keys maintained by this registry.
 * @param <V> The type of mapped values.
 */
public class HFFRegistry<K, V> implements Serializable {
    /**
     * The internal map that stores the key-value pairs.
     */
    private final Map<K, V> REGISTRY = new HashMap<>();

    /**
     * Registers a key-value pair in the registry.
     * If the key already exists, the existing value will be overwritten.
     *
     * @param key   The key with which the specified value is to be associated.
     * @param value The value to be associated with the specified key.
     */
    public void register(K key, V value) {
        REGISTRY.put(key, value);
    }

    /**
     * Retrieves the value associated with the specified key.
     *
     * @param key The key whose associated value is to be returned.
     * @return The value associated with the specified key, or {@code null} if the key is not present in the registry.
     */
    public V get(K key) {
        return REGISTRY.get(key);
    }

    /**
     * Removes the key-value pair associated with the specified key from the registry.
     *
     * @param key The key whose mapping is to be removed from the registry.
     */
    public void remove(K key) {
        REGISTRY.remove(key);
    }

    /**
     * Returns a shallow copy of the registry as a new map.
     * The returned map is a new instance, so modifications to it will not affect the internal registry.
     *
     * @return A shallow copy of the registry.
     */
    public Map<K, V> copy() {
        return new HashMap<>(REGISTRY);
    }

    /**
     * Removes all key-value pairs from the registry.
     */
    public void clear() {
        REGISTRY.clear();
    }

    /**
     * Updates the value associated with the specified key in the registry.
     * If the key does not exist, a new key-value pair is added.
     *
     * @param key   The key with which the specified value is to be associated.
     * @param value The value to be associated with the specified key.
     */
    public void update(K key, V value) {
        REGISTRY.put(key, value);
    }
}