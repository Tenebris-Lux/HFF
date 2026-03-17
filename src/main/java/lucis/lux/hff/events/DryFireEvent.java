package lucis.lux.hff.events;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.event.IEvent;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import lucis.lux.hff.data.FirearmState;

/**
 * The {@code DryFireEvent} record represents an event that is dispatched when a firearm is fired
 * but has no ammunition left. This event provides information about the player and the firearm's state
 * at the time of the dry fire.
 *
 * <p>This event is typically used in the HFF (Hytale Firearm Framework) plugin to notify other systems
 * about dry fire events and to allow for custom behaviour when a firearm is fired without ammunition.</p>
 *
 * <p>Example usage:</p>
 * <pre>
 *     // Register a listener for the DryFireEvent
 *     getEventRegistry().register(DryFireEvent.class, event -> {
 *         Ref<EntityStore> playerRef = event.playerRef();
 *         FirearmState state = event.state();
 *         // Custom logic when a dry fire occurs
 *     });
 * </pre>
 *
 * @param playerRef A reference to the player's entity store.
 * @param state     The state of the firearm at the time of the dry fire.
 */
public record DryFireEvent(Ref<EntityStore> playerRef, FirearmState state) implements IEvent<Void> {
}