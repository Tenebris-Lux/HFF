package lucis.lux.hff.data.registry;

import lucis.lux.hff.data.*;

import java.util.UUID;

/**
 * The {@code Registries} class serves as a central registry manager for the HFF (Hytale Firearm Framework) plugin.
 * This class provides static instances of {@link HFFRegistry} for managing various types of data related to firearms,
 * ammunition, attachments, and magazines. These registries allow for efficient storage, retrieval, and management
 * of game-related data.
 *
 * <p>The following registries are provided:</p>
 * <ul>
 *   <li>{@link #FIREARM_STATES}: A registry for managing the state of firearms, keyed by their unique UUIDs.</li>
 *   <li>{@link #MAGAZINE_STATES}: A registry for managing the state of magazines, keyed by their unique UUIDs.</li>
 *   <li>{@link #FIREARM_STATS}: A registry for managing firearm statistics, keyed by firearm item IDs.</li>
 *   <li>{@link #AMMO_DATA}: A registry for managing ammunition data, keyed by ammunition item IDs.</li>
 *   <li>{@link #ATTACHMENT_DATA}: A registry for managing attachment data, keyed by attachment item IDs.</li>
 *   <li>{@link #MAGAZINE_DATA}: A registry for managing magazine data, keyed by magazine item IDs.</li>
 * </ul>
 *
 * <p>Example usage:</p>
 * <pre>
 *     // Register firearm statistics
 *     Registries.FIREARM_STATS.register("example_gun", firearmStats);
 *
 *     // Retrieve ammunition data
 *     AmmoData ammoData = Registries.AMMO_DATA.get("9mm_ammo");
 *
 *     // Update magazine state
 *     Registries.MAGAZINE_STATES.update(magazineUuid, magazineState);
 * </pre>
 *
 * @see HFFRegistry
 * @see FirearmState
 * @see MagazineState
 * @see FirearmStats
 * @see AmmoData
 * @see AttachmentData
 * @see MagazineData
 */
public class Registries {

    /**
     * A registry for managing the state of firearms, keyed by their unique UUIDs.
     * This registry allows for tracking the operational state of firearms during gameplay.
     */
    public static final HFFRegistry<UUID, FirearmState> FIREARM_STATES = new HFFRegistry<>();
    /**
     * A registry for managing the state of magazines, keyed by their unique UUIDs.
     * This registry allows for tracking the operational state of magazines during gameplay.
     */
    public static final HFFRegistry<UUID, MagazineState> MAGAZINE_STATES = new HFFRegistry<>();

    /**
     * A registry for managing firearm statistics, keyed by firearm item IDs.
     * This registry allows for storing and retrieving detailed statistics and properties of firearms.
     */
    public static final HFFRegistry<String, FirearmStats> FIREARM_STATS = new HFFRegistry<>();
    /**
     * A registry for managing ammunition data, keyed by ammunition item IDs.
     * This registry allows for storing and retrieving data such as caliber, projectile ID, and damage values.
     */
    public static final HFFRegistry<String, AmmoData> AMMO_DATA = new HFFRegistry<>();
    /**
     * A registry for managing attachment data, keyed by attachment item IDs.
     * This registry allows for storing and retrieving data related to firearm attachments.
     */
    public static final HFFRegistry<String, AttachmentData> ATTACHMENT_DATA = new HFFRegistry<>();
    /**
     * A registry for managing magazine data, keyed by magazine item IDs.
     * This registry allows for storing and retrieving data related to firearm magazines.
     */
    public static final HFFRegistry<String, MagazineData> MAGAZINE_DATA = new HFFRegistry<>();


}