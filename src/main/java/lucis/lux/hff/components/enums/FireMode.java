package lucis.lux.hff.components.enums;

/**
 * The {@code FireMode} enum defines the different firing modes available for firearms.
 * Each mode specifies how a firearm behaves when the trigger is pulled, ranging from single shots
 * to fully automatic fire. This classification is essential for gameplay mechanics, weapon balancing,
 * and realistic simulation of firearms.
 *
 * <p>Fire modes influence the rate of fire, ammunition consumption, and tactical use of a weapon.
 * For example, automatic weapons are suitable for suppressing fire, while single-action or bolt-action
 * weapons are often used for precision shooting.</p>
 *
 * <p>The supported fire modes are:</p>
 * <ul>
 *     <li>{@link #SELECT_FIRE}: Allows the user to switch between different firing modes (e.g., semi-automatic and automatic).</li>
 *     <li>{@link #SEMI_AUTOMATIC}: Fires one round per trigger pull, automatically chambering the nect round.</li>
 *     <li>{@link #BURST}: Fires a fixed number of rounds (usually 2 or 3) per trigger pull.</li>
 *     <li>{@link #AUTOMATIC}: Continuously fires rounds as long the trigger is held down.</li>
 *     <li>{@link #SINGLE_SHOT}: Fires one round per trigger pull, requiring manual action to chamber the next round (e.g., bolt-action rifles).</li>
 *     <li>{@link #SINGLE_ACTION}: Requires the hammer to be manually cocked before each shot (e.g., single-action revolvers).</li>
 *     <li>{@link #DOUBLE_ACTION}: Cocks and fires the weapon with a single trigger pull (e.g., double-action revolvers).</li>
 *     <li>{@link #MANUAL}: Requires manual operation for each shot (e.g., muskets or break-action shotguns).</li>
 *     <li>{@link #OTHER}: Fire modes that do not fit into the above categories, such as experimental or custom designs.</li>
 * </ul>
 */
public enum FireMode {
    /**
     * Allows switching between different firing modes (e.g., semi-automatic and automatic).
     */
    SELECT_FIRE,

    /**
     * Fires one round per trigger pull, automatically chambering the next round.
     */
    SEMI_AUTOMATIC,

    /**
     * Fires a fixed number of rounds (usually 2 or 3) per trigger pull.
     */
    BURST,

    /**
     * Continuously fires rounds as long as the trigger is held down.
     */
    AUTOMATIC,

    /**
     * Fires one round per trigger pull, requiring manual action to chamber the next round (e.g., bolt-action rifles).
     */
    SINGLE_SHOT,

    /**
     * Requires the hammer to be manually cocked before each shot (e.g., single-action revolvers).
     */
    SINGLE_ACTION,

    /**
     * Cocks and fires the weapon with a single trigger pull (e.g., double-action revolvers).
     */
    DOUBLE_ACTION,

    /**
     * Requires manual operation for each shot (e.g., muskets or break-action shotguns).
     */
    MANUAL,

    /**
     * Fire modes that do not fit into the above categories, such as experimental or custom designs.
     */
    OTHER
}
