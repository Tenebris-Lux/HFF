package lucis.lux.hff.events;

import com.hypixel.hytale.event.ICancellable;
import com.hypixel.hytale.event.IEvent;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import lucis.lux.hff.data.FirearmStats;

/**
 * The {@code ReloadEvent} class is an abstract event that represents the reloading of a firearm.
 * This class provides two nested classes, {@link Pre} and {@link Post}, to represent events before and after a firearm is reloaded.
 *
 * <p>The {@code Pre} event is cancellable and can be used to prevent the firearm from being reloaded.
 * The {@link Post} event provides additional information about whether the reloading process was successful.</p>
 *
 * <p>This event is typically used in the HFF (Hytale Firearm Framework) plugin to notify other systems about firearm reloading events
 * and to allow for custom behaviour before or after a firearm is reloaded.</p>
 *
 * <p>Example usage:</p>
 * <pre>
 *     // Register a listener for the Pre event
 *     getEventRegistry().register(ReloadEvent.Pre.class, event -> {
 *         if (event.isCancelled()) {
 *             return;
 *         }
 *         // Custom logic before the firearm is reloaded
 *     });
 *
 *     // Register a listener for the Post event
 *     getEventRegistry().register(ReloadEvent.Post.class, event -> {
 *         boolean success = event.isSuccess();
 *         // Custom logic after the firearm is reloaded
 *     });
 * </pre>
 */
public abstract class ReloadEvent implements IEvent<Void> {

    /**
     * The player who is reloading the firearm.
     */
    protected final Player player;
    /**
     * The firearm item being reloaded.
     */
    protected final ItemStack weapon;
    /**
     * The statistics of the firearm being reloaded.
     */
    protected final FirearmStats stats;

    /**
     * Constructs a new {@code ReloadEvent} with the specified player, firearm item, and firearm statistics.
     *
     * @param player The player who is reloading the firearm.
     * @param weapon The firearm item being reloaded.
     * @param stats  The statistics of the firearm.
     */
    protected ReloadEvent(Player player, ItemStack weapon, FirearmStats stats) {
        this.player = player;
        this.stats = stats;
        this.weapon = weapon;
    }

    /**
     * Returns the player who is reloading the firearm.
     *
     * @return The player who is reloading the firearm.
     */
    public Player getPlayer() {
        return player;
    }

    /**
     * Returns the firearm item being reloaded.
     *
     * @return The firearm item being reloaded.
     */
    public ItemStack getWeapon() {
        return weapon;
    }

    /**
     * Returns the statistics of the firearm being reloaded.
     *
     * @return The statistics of the firearm.
     */
    public FirearmStats getStats() {
        return stats;
    }

    /**
     * The {@code Pre} class represents an event that is dispatched before a firearm is reloaded.
     * This event is cancellable, allowing other systems to prevent the firearm from being reloaded.
     */
    public static class Pre extends ReloadEvent implements ICancellable {

        /**
         * Indicates whether the event is cancelled.
         */
        private boolean cancelled = false;

        /**
         * Constructs a new {@code Pre} event with the specified player, firearm item, and firearm statistics.
         *
         * @param player The player who is about to reload the firearm.
         * @param weapon The firearm item being reloaded.
         * @param stats  The statistics of the firearm.
         */
        public Pre(Player player, ItemStack weapon, FirearmStats stats) {
            super(player, weapon, stats);
        }

        /**
         * Returns whether the event is cancelled.
         *
         * @return {@code true} if the event is cancelled, {@code false} otherwise.
         */
        @Override
        public boolean isCancelled() {
            return cancelled;
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
     * The {@code Post} class represents an event that is dispatched after a firearm is reloaded.
     * This event provides additional information about whether the reloading process was successful.
     */
    public static class Post extends ReloadEvent {

        /**
         * Indicates whether the reloading process was successful.
         */
        private final boolean success;

        /**
         * Constructs a new {@code Post} event with the specified player, firearm item, firearm statistics, and success status.
         *
         * @param player  The player who reloaded the firearm.
         * @param weapon  The firearm item that was reloaded.
         * @param stats   The statistics of the firearm.
         * @param success Whether the reloading process was successful.
         */
        public Post(Player player, ItemStack weapon, FirearmStats stats, boolean success) {
            super(player, weapon, stats);
            this.success = success;
        }

        /**
         * Returns whether the reloading process was successful.
         *
         * @return {@code true} if the reloading process was successful, {@code false} otherwise.
         */
        public boolean isSuccess() {
            return success;
        }
    }
}
