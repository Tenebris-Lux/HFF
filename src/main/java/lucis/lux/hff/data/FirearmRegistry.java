package lucis.lux.hff.data;

import java.util.HashMap;
import java.util.Map;

/**
 * The {@code FirearmRegistry} class serves ass a central registry for managing and retrieving
 * firearm statistics associated with specific itemIds. This registry allows for the storage
 * and retrieval of {@link FirearmStats} objects, which contain detailed statistics and properties
 * of firearms, such as rate of fire, projectile velocity, recoil, and more.
 *
 * <p>This class is designed to be used in the HFF (Hytale Firearm Framework) plugin, providing
 * a simple and efficient way to manage firearm statistics across different systems and interactions.</p>
 *
 * <p>Example usage:</p>
 * <pre>
 *      // Register firearm statistics for an item
 *      FirearmStats stats = FirearmStats.builder()
 *          .reloadTime(2.5f)
 *          .rpm(600.0f)
 *          .projectileVelocity(300.0f)
 *          .projectileAmount(1)
 *          .projectileCapacity(30)
 *          .spreadBase(2.0f)
 *          .movementPenalty(0.5f)
 *          .misfireChance(0.01f)
 *          .jamChance(0.005f)
 *          .verticalRecoil(0.5f)
 *          .horizontalRecoil(0.1f)
 *          .firearmClass(FirearmClass.PISTOL)
 *          .firearmType(FirearmType.HANDGUN)
 *          .fireMode(FireMode.SEMI_AUTOMATIC)
 *          .disabled(false)
 *          .calibre("9mm")
 *          .build();
 *      FirearmRegistry.register("example_gun", stats);
 *
 *      // Retrieve firearm statistics for an item
 *      FirearmStats retrievedStats = FirearmRegistry.get("example_gun");
 * </pre>
 *
 * @see FirearmStats
 */
public class FirearmRegistry {

    /**
     * A map that stores the firearm statistics associated with item IDs.
     * This map is static to ensure that the registry is globally accessible.
     */
    private static final Map<String, FirearmStats> REGISTRY = new HashMap<>();

    /**
     * Registers firearm statistics for a specific item ID.
     * If the item ID already exists in the registry, the existing statistics will be overwritten.
     *
     * @param itemId The ID of the item for which to register the firearm statistics.
     * @param stats  The firearm statistics to register.
     */
    public static void register(String itemId, FirearmStats stats) {
        REGISTRY.put(itemId, stats);
    }

    /**
     * Retrieves the firearm statistics associated with a specific item ID.
     *
     * @param itemId The ID of the item for which to retrieve the firearm statistics.
     * @return The firearm statistics associated with the item ID, or {@code null} if no statistics are registered.
     */
    public static FirearmStats get(String itemId) {
        return REGISTRY.get(itemId);
    }
}
