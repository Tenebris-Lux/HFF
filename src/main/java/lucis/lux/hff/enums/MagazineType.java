package lucis.lux.hff.enums;

/**
 * The {@code MagazineType} enum represents the type of magazine used by a firearm.
 * This enum defines two types of magazines:
 * <ul>
 *   <li>{@link #INTERNAL}: An internal magazine, which is part of the firearm itself.</li>
 *   <li>{@link #EXTERNAL}: An external magazine, which can be detached and replaced.</li>
 * </ul>
 *
 * <p>This enum is used in the HFF (Hytale Firearm Framework) plugin to define the type of magazine
 * used by a firearm, which affects how ammunition is managed and reloaded.</p>
 */
public enum MagazineType {
    /**
     * Represents an internal magazine, which is built into the firearm.
     * Internal magazines are typically not detachable and are reloaded by inserting projectiles directly into the firearm.
     */
    INTERNAL,

    /**
     * Represents an external magazine, which can be detached and replaced.
     * External magazines are typically reloaded by swapping out the magazine with a loaded one.
     */
    EXTERNAL
}
