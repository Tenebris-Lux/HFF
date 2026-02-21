package lucis.lux.hff.data;

import java.util.HashMap;
import java.util.Map;

/**
 * The {@code AmmoRegistry} class serves as a central registry for managing and retrieving
 * ammunition data associated with specific item IDs. This registry allows for storage
 * and retrieval of {@link AmmoData} objects, which contain information such as calibre,
 * projectile ID, and damage values.
 *
 * <p>This class is designed to be used in the HFF (Hytale Firearm Framework) plugin
 * providing a simple and efficient way to manage ammunition data across different systems
 * and interactions.</p>
 *
 * <p>Example usage:</p>
 * <pre>
 *     // Register ammunition data for an item
 *     AmmoData ammoData = AmmoData.builder()
 *          .calibre("9mm")
 *          .projectileId("9mm_projectile")
 *          .damage(5.0f)
 *          .build();
 *     AmmoRegistry.register("example_gun", ammoData);
 *
 *     // Retrieve ammunition data for an item
 *     AmmoData retrievedAmmoData = AmmoRegistry.get("example_gun");
 * </pre>
 *
 * @see AmmoData
 */
public class AmmoRegistry {

    /**
     * A map that stores the ammunition data associated with item IDs.
     * This map is static to ensure that the registry is globally accessible.
     */
    private static final Map<String, AmmoData> REGISTRY = new HashMap<>();

    /**
     * Registers ammunition data for a specific item ID.
     * If the item ID already exists in the registry, the existing data will be overwritten.
     *
     * @param itemId The ID of the item for which to register the ammunition data.
     * @param stats  The ammunition data to register.
     */
    public static void register(String itemId, AmmoData stats) {
        REGISTRY.put(itemId, stats);
    }

    /**
     * Retrieves the ammunition data associated with a specific item ID.
     *
     * @param itemId The ID of the item for which to retrieve the ammunition data.
     * @return The ammunition data associated with the item ID, or {@code null} if no data is registered.
     */
    public static AmmoData get(String itemId) {
        return REGISTRY.get(itemId);
    }
}
