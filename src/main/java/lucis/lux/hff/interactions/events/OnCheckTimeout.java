package lucis.lux.hff.interactions.events;

import com.hypixel.hytale.event.IEvent;

/**
 * The {@code OnCheckTimeout} class represents an event that is triggered when a timeout check is performed.
 * This event can be used to notify systems or components that a specific timeout-related action has occurred,
 * such as checking if a cooldown or delay has expired.
 *
 * <p>This event is particular useful in gameplay mechanics where timing is critical, such as firearm cooldowns,
 * ability delays, or other time sensitive operations. The event carries a data string that can be used to identify
 * the context or source of the timeout check.</p>
 *
 * <p>Example use cases include:</p>
 * <ul>
 *     <li>Triggering actions when a firearm cooldown has expired.</li>
 *     <li>Notifying systems when a player's ability is ready to be used again.</li>
 *     <li>Logging or debugging timeout-related events.</li>
 * </ul>
 *
 * @param data A string containing additional data or context about the timeout check.
 */
public record OnCheckTimeout(String data) implements IEvent<Void> {
}
