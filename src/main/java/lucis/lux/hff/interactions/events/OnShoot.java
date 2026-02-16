package lucis.lux.hff.interactions.events;

import com.hypixel.hytale.event.IEvent;
import lucis.lux.hff.components.AmmoComponent;
import lucis.lux.hff.components.FirearmStatsComponent;
import lucis.lux.hff.interactions.ShootFirearmInteraction;
import lucis.lux.hff.systems.FirearmSystem;

/**
 * The {@code OnShoot} class represents an event that is triggered when a firearm is fired.
 * This event is dispatched via the Hytale event bus and can be used to modify systems or components
 * that a shooting action has occurred, such as spawning projectiles, applying recoil, or updating
 * ammunition counts.
 *
 * <p>This event is particularly useful in the context of the HFF (Hytale Firearm Framework)
 * plugin, where it can be used to synchronize shooting mechanics across different systems,
 * such as {@link FirearmSystem}, or to trigger side effects like sound,
 * visual effects, or logging.</p>
 *
 * <p>Example use cases include:</p>
 * <ul>
 *     <li>Triggering projectile spawning logic in response to a shooting action.</li>
 *     <li>Applying recoil or spread adjustments to the player's aim.</li>
 *     <li>Updating ammunition counts or triggering reloading logic.</li>
 *     <li>Logging or debugging shooting actions for development purposes.</li>
 * </ul>
 *
 * <p>This event is typically dispatched in the {@link ShootFirearmInteraction}
 * class after validating that the firearm is not disabled and all required components are present.</p>
 *
 * @param data A string containing additional data or context about the shooting event.
 *             This can be used to pass information such as the type of firearm, ammunition used,
 *             or other relevant metadata.
 * @see ShootFirearmInteraction
 * @see FirearmStatsComponent
 * @see AmmoComponent
 */
public record OnShoot(String data) implements IEvent<Void> {
}
