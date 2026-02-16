package lucis.lux.hff.components.enums;

/**
 * The {@code FirearmType} enum categorizes firearms into distinct types based on their design,
 * function, and role. This classification is useful for gameplay mechanics, asset management,
 * and balancing, where the type of firearm may influence its behaviour, statistics, or availability.
 *
 * <p>Each type represents a broad category of firearms, ranging from small handguns to heavy
 * machine guns and specialized launchers. This enum is designed to be extensible and can be
 * used to filter, sort, or apply logic specific to each firearm type.</p>
 *
 * <p>The supported types are:</p>
 * <ul>
 *     <li>{@link #HANDGUN}: Small, portable firearms designed for one-handed use, such as pistols.</li>
 *     <li>{@link #REVOLVER}: Handguns with a revolving cylinder containing multiple chambers for ammunition.</li>
 *     <li>{@link #MACHINE_PISTOL}: Compact, fully automatic firearms, often used for close-quarter combat.</li>
 *     <li>{@link #SUBMACHINE_GUN}: Lightweight, automatic firearms chambered for pistol-calibre ammunition.</li>
 *     <li>{@link #SHOTGUN}: Firearms that fire a spread of small pellets os a single slug, effective at short range.</li>
 *     <li>{@link #LONG_GUN}: A general category for long-barrelled firearms, such as rifles and muskets.</li>
 *     <li>{@link #AUTOMATIC_RIFLE}: Rifles capable of fully automatic fire, often used in military contexts.</li>
 *     <li>{@link #LIGHT_MACHINE_GUN}: Lightweight machine guns designed for mobility and sustained fire.</li>
 *     <li>{@link #GP_MACHINE_GUN}: General-purpose machine guns, versatile for both infantry and vehicle roles.</li>
 *     <li>{@link #BATTLE_RIFLE}: Powerful rifles designed for intermediate range, often with select-fire capabilities.</li>
 *     <li>{@link #ASSAULT_RIFLE}: Select-fire rifles chambered for intermediate cartridges, standard for modern infantry.</li>
 *     <li>{@link #MARKSMAN_RIFLE}: Precision rifles designed for accurate long-range fire, often semi-automatic.</li>
 *     <li>{@link #SNIPER_RIFLE}: High-precision rifles optimized for extreme range and accuracy, typically bolt-action.</li>
 *     <li>{@link #ROCKET_LAUNCHER}: Weapons designed to launch explosive rockets, used against vehicles or fortifications.</li>
 *     <li>{@link #GRENADE_LAUNCHER}: Firearms that launch explosive grenades, effective for indirect fire or area denial.</li>
 *     <li>{@link #OTHER}: Firearms or projectile weapons that not fit into the above categories, such as experimental or fictional designs.</li>
 * </ul>
 */
public enum FirearmType {
    /**
     * Small, portable firearms designed for one-handed use, such as pistols.
     */
    HANDGUN,

    /**
     * Handguns with a revolving cylinder containing multiple chambers for ammunition.
     */
    REVOLVER,

    /**
     * Compact, fully automatic handguns, often used for close-quarters combat.
     */
    MACHINE_PISTOL,

    /**
     * Lightweight, automatic firearms chambered for pistol-calibre ammunition.
     */
    SUBMACHINE_GUN,

    /**
     * Firearms that fire a spread of small pellets or a single slug, effective at short range.
     */
    SHOTGUN,

    /**
     * A general category for long-barrelled firearms, such as rifles and muskets.
     */
    LONG_GUN,

    /**
     * Rifles capable of fully automatic fire, often used in military contexts.
     */
    AUTOMATIC_RIFLE,

    /**
     * Lightweight machine guns designed for mobility and sustained fire.
     */
    LIGHT_MACHINE_GUN,

    /**
     * General-purpose machine guns, versatile for both infantry and vehicle roles.
     */
    GP_MACHINE_GUN,

    /**
     * Powerful rifles designed for intermediate range, often with select-fire capabilities.
     */
    BATTLE_RIFLE,

    /**
     * Select-fire rifles chambered for intermediate cartridges, standard for modern infantry.
     */
    ASSAULT_RIFLE,

    /**
     * Precision rifles designed for accurate long-range fire, often semi-automatic.
     */
    MARKSMAN_RIFLE,

    /**
     * High-precision rifles optimized for extreme range and accuracy, typically bolt-action.
     */
    SNIPER_RIFLE,

    /**
     * Weapons designed to launch explosive rockets, used against vehicles or fortifications.
     */
    ROCKET_LAUNCHER,

    /**
     * Firearms that launch explosive grenades, effective for indirect fire or area denial.
     */
    GRENADE_LAUNCHER,

    /**
     * Firearms or projectile weapons that do not fit into the above categories.
     */
    OTHER
}
