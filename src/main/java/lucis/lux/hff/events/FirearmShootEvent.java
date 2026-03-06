package lucis.lux.hff.events;

import com.hypixel.hytale.event.ICancellable;
import com.hypixel.hytale.event.IEvent;
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.server.core.entity.entities.Player;
import lucis.lux.hff.data.FirearmState;
import lucis.lux.hff.data.FirearmStats;

/**
 * The {@code FirearmShootEvent} class is an abstract event that represents the shooting of a firearm.
 * This class provides two nested classes, {@link Pre} and {@link Post}, to represent events before and after a firearm is shot.
 *
 * <p>The {@link Pre} event is cancellable and can be used to prevent the firearm from being shot.
 * The {@link Post} event provides additional information about the projectile's position and direction after it has been fired.</p>
 *
 * <p>This event is typically used in the HFF (Hytale Firearm Framework) plugin to notify other systems about firearm shooting events
 * and to allow for custom behaviour before and after a firearm is shot.</p>
 *
 * <p>Example usage:</p>
 * <pre>
 *     // Register a listener for the Pre event
 *     getEventListener().register(FirearmShootEvent.Post.class, event -> {
 *         Vector3d position = event.getPosition();
 *         Vector3d direction = event.getDirection();
 *         // Some custom logic
 *     });
 * </pre>
 */
public abstract class FirearmShootEvent implements IEvent<Void> {

    /**
     * The player who shot the firearm.
     */
    protected final Player player;
    /**
     * The state of the firearm at the time of the shooting event.
     */
    protected final FirearmState state;
    /**
     * The statistics of the firearm at the time of the shooting event.
     */
    protected final FirearmStats stats;

    /**
     * Constructs a new {@code FirearmShootEvent} with the specified player, firearm state, and firearm statistics.
     *
     * @param player The player who shot the firearm.
     * @param state  The state of the firearm.
     * @param stats  The statistics of the firearm.
     */
    protected FirearmShootEvent(Player player, FirearmState state, FirearmStats stats) {
        this.player = player;
        this.state = state;
        this.stats = stats;
    }

    /**
     * Returns the player who shot the firearm.
     *
     * @return The player who shot the firearm.
     */
    public Player getPlayer() {
        return player;
    }

    /**
     * Returns the state of the firearm at the time of the shooting event.
     *
     * @return The state of the firearm.
     */
    public FirearmState getState() {
        return state;
    }

    /**
     * Returns the statistics of the firearm at the time of the shooting event.
     *
     * @return The statistics of the firearm.
     */
    public FirearmStats getStats() {
        return stats;
    }

    /**
     * The {@code Pre} class represents an event that is dispatched before a firearm is shot.
     * This event is cancellable, allowing other systems to prevent the firearm from being shot.
     */
    public static class Pre extends FirearmShootEvent implements ICancellable {

        /**
         * Indicates whether the event is cancelled.
         */
        private boolean cancelled = false;

        /**
         * Constructs a new {@code Pre} event with the specified player, firearm state, and firearm statistics.
         *
         * @param player The player who is about to shoot the firearm.
         * @param state  The state of the firearm.
         * @param stats  The statistics of the firearm.
         */
        public Pre(Player player, FirearmState state, FirearmStats stats) {
            super(player, state, stats);
        }

        /**
         * Returns whether the event is cancelled.
         *
         * @return {@code true} if the event is cancelled, {@code false} otherwise.
         */
        @Override
        public boolean isCancelled() {
            return this.cancelled;
        }

        /**
         * Sets whether the event is cancelled.
         *
         * @param b {@code true} to cancel the event, {@code false} otherwise.
         */
        @Override
        public void setCancelled(boolean b) {
            this.cancelled = b;
        }
    }

    /**
     * The {@code Post} class represents an event that is dispatched after a firearm is shot.
     * This event provides additional information about the projectile's position and direction.
     */
    public static class Post extends FirearmShootEvent {

        /**
         * The position of the projectile when it was fired.
         */
        private final Vector3d position;
        /**
         * The direction of the projectile when it was fired.
         */
        private final Vector3d direction;

        /**
         * Constructs a new {@code Post} event with the specified player, firearm state, firearm statistics,
         * projectile position, and projectile direction.
         *
         * @param player    The player who shot the firearm.
         * @param state     The state of the firearm.
         * @param stats     The statistics of the firearm.
         * @param position  The position of the projectile.
         * @param direction The direction of the projectile.
         */
        public Post(Player player, FirearmState state, FirearmStats stats, Vector3d position, Vector3d direction) {
            super(player, state, stats);
            this.direction = direction;
            this.position = position;
        }

        /**
         * Returns the position of the projectile when it was fired.
         *
         * @return The position of the projectile.
         */
        public Vector3d getPosition() {
            return position;
        }

        /**
         * Returns the direction of the projectile when it was fired.
         *
         * @return The direction of the projectile.
         */
        public Vector3d getDirection() {
            return direction;
        }
    }
}
