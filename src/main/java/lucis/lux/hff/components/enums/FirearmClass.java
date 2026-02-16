package lucis.lux.hff.components.enums;

/**
 * The {@code FirearmClass} enum represents the historical classification of firearms
 * based on their technological era and mechanism. Each class corresponds to a distinct
 * period or type of firearm development, ranging from pre-gunpowder weapons to modern firearms.
 *
 * <p>This classification is useful for categorising firearms in historical contexts,
 * gameplay mechanics, or asset management, where the era or technology of a firearm
 * may influence its behaviour, appearance, or availability.</p>
 *
 * <p>The supported firearm classes are:</p>
 * <ul>
 *     <li>{@link #PRE_GUNPOWDER}: Firearms or projectile weapons that predate the invention of gunpowder,
 *     such as crossbows or early mechanical projectile launchers.</li>
 *     <li>{@link #EARLY_GUNPOWDER}: The earliest firearms that utilized gunpowder, often primitive
 *     in design and limited in effectiveness.</li>
 *     <li>{@link #MATCHLOCK}: Firearms that use a matchlock mechanism, where a slow-burning match is used
 *     to ignite the gunpowder.</li>
 *     <li>{@link #WHEELLOCK}: Firearms that use a wheellock mechanism, which generates sparks by spinning
 *     a serrated wheel against a piece of pyrite.</li>
 *     <li>{@link #FLINTLOCK}: Firearms that use a flintlock mechanism, where a piece of flint strikes steel
 *     to create sparks and ignite the gunpowder.</li>
 *     <li>{@link #PERCUSSION_CAP}: Firearms that use percussion caps, which are small explosive caps that
 *     ignite the gunpowder when struck by the hammer.</li>
 *     <li>{@link #MODERN}: Modern firearms, typically using cartridge-based ammunition and advanced
 *     mechanical or electronic firing mechanisms.</li>
 *     <li>{@link #OTHER}: Firearms or projectile weapons that do not fit into the above categories,
 *     such as experimental or fictional designs.</li>
 * </ul>
 */
public enum FirearmClass {
    /**
     * Pre-gunpowder projectile weapons, such as crossbows.
     */
    PRE_GUNPOWDER,
    /**
     * Early firearms that utilized gunpowder in a primitive form.
     */
    EARLY_GUNPOWDER,
    /**
     * Firearms using a matchlock ignition mechanism.
     */
    MATCHLOCK,
    /**
     * Firearms using a wheellock ignition mechanism.
     */
    WHEELLOCK,
    /**
     * Firearms using a flintlock ignition mechanism.
     */
    FLINTLOCK,
    /**
     * Firearms using percussion caps for ignition.
     */
    PERCUSSION_CAP,
    /**
     * Modern firearms, typically using cartridge-based ammunition.
     */
    MODERN,
    /**
     * Firearms or projectile weapons that do not fit into the above categories.
     */
    OTHER
}
