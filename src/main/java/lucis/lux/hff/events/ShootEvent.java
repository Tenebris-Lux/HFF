package lucis.lux.hff.events;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.event.ICancellable;
import com.hypixel.hytale.event.IEvent;
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import lucis.lux.hff.data.FirearmState;
import lucis.lux.hff.data.FirearmStats;

/**
 * The {@code ShootEvent} class is an abstract event that represents the shooting of a firearm.
 * This class provides two nested classes, {@link Pre} and {@link Post}, to represent events before and after a firearm is shot.
 *
 * <p>The {@link Pre} event is cancellable and can be used to prevent the firearm from being shot or to modify
 * the position or direction of the projectile before it is fired. The {@link Post} event provides additional
 * information about the projectile's position and direction after it has been fired.</p>
 *
 * <p>This event is typically used in the HFF (Hytale Firearm Framework) plugin to notify other systems about firearm shooting events
 * and to allow for custom behavior before and after a firearm is shot.</p>
 *
 * <p>Example usage:</p>
 * <pre>
 *     // Register a listener for the Pre event
 *     getEventRegistry().register(ShootEvent.Pre.class, event -> {
 *         if (event.isCancelled()) {
 *             return;
 *         }
 *         // Custom logic before the firearm is shot
 *         event.setDirection(new Vector3d(0, 0, 1)); // Modify the direction
 *     });
 *
 *     // Register a listener for the Post event
 *     getEventRegistry().register(ShootEvent.Post.class, event -> {
 *         Vector3d position = event.getPosition();
 *         Vector3d direction = event.getDirection();
 *         // Custom logic after the firearm is shot
 *     });
 * </pre>
 */
public abstract class ShootEvent implements IEvent<Void> {

    /**
     * A reference to the player who shot the firearm.
     */
    protected final Ref<EntityStore> playerRef;

    /**
     * The state of the firearm at the time of the shooting event.
     */
    protected final FirearmState state;

    /**
     * The statistics of the firearm at the time of the shooting event.
     */
    protected final FirearmStats stats;

    /**
     * The position of the projectile when it was fired.
     */
    protected Vector3d position;

    /**
     * The direction of the projectile when it was fired.
     */
    protected Vector3d direction;

    /**
     * Constructs a new {@code ShootEvent} with the specified player, firearm state, firearm statistics,
     * projectile position, and projectile direction.
     *
     * @param playerRef A reference to the player who shot the firearm.
     * @param state     The state of the firearm.
     * @param stats     The statistics of the firearm.
     * @param position  The position of the projectile.
     * @param direction The direction of the projectile.
     */
    protected ShootEvent(Ref<EntityStore> playerRef, FirearmState state, FirearmStats stats, Vector3d position, Vector3d direction) {
        this.playerRef = playerRef;
        this.state = state;
        this.stats = stats;
        this.position = position;
        this.direction = direction;
    }

    /**
     * Returns a reference to the player who shot the firearm.
     *
     * @return A reference to the player who shot the firearm.
     */
    public Ref<EntityStore> getPlayerRef() {
        return playerRef;
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

    /**
     * The {@code Pre} class represents an event that is dispatched before a firearm is shot.
     * This event is cancellable, allowing other systems to prevent the firearm from being shot.
     * It also allows for modifying the position or direction of the projectile before it is fired.
     */
    public static class Pre extends ShootEvent implements ICancellable {

        /**
         * Indicates whether the event is cancelled.
         */
        private boolean cancelled = false;

        /**
         * Constructs a new {@code Pre} event with the specified player, firearm state, firearm statistics,
         * projectile position, and projectile direction.
         *
         * @param playerRef A reference to the player who is about to shoot the firearm.
         * @param state     The state of the firearm.
         * @param stats     The statistics of the firearm.
         * @param position  The position of the projectile.
         * @param direction The direction of the projectile.
         */
        public Pre(Ref<EntityStore> playerRef, FirearmState state, FirearmStats stats, Vector3d position, Vector3d direction) {
            super(playerRef, state, stats, position, direction);
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

        /**
         * Sets the direction of the projectile.
         *
         * @param direction The direction to set.
         */
        public void setDirection(Vector3d direction) {
            this.direction = direction;
        }

        /**
         * Sets the position of the projectile.
         *
         * @param position The position to set.
         */
        public void setPosition(Vector3d position) {
            this.position = position;
        }
    }

    /**
     * The {@code Post} class represents an event that is dispatched after a firearm is shot.
     * This event provides additional information about the projectile's position and direction after it has been fired.
     */
    public static class Post extends ShootEvent {

        /**
         * Constructs a new {@code Post} event with the specified player, firearm state, firearm statistics,
         * projectile position, and projectile direction.
         *
         * @param playerRef A reference to the player who shot the firearm.
         * @param state     The state of the firearm.
         * @param stats     The statistics of the firearm.
         * @param position  The position of the projectile.
         * @param direction The direction of the projectile.
         */
        public Post(Ref<EntityStore> playerRef, FirearmState state, FirearmStats stats, Vector3d position, Vector3d direction) {
            super(playerRef, state, stats, position, direction);
        }
    }
}
