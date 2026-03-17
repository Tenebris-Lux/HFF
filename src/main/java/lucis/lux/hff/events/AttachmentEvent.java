package lucis.lux.hff.events;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.event.ICancellable;
import com.hypixel.hytale.event.IEvent;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import lucis.lux.hff.data.FirearmState;
import lucis.lux.hff.enums.AttachmentType;

/**
 * The {@code AttachmentEvent} class is an abstract event that represents the attachment of an accessory
 * to a firearm. This class provides two nested classes, {@link Pre} and {@link Post}, to represent events
 * before and after an attachment is installed or removed.
 *
 * <p>The {@code Pre} event is cancellable and can be used to prevent the attachment from being installed or removed.
 * The {@link Post} event provides additional information about the attachment process after it has been completed.</p>
 *
 * <p>This event is typically used in the HFF (Hytale Firearm Framework) plugin to notify other systems about
 * attachment events and to allow for custom behaviour before or after an attachment is installed or removed.</p>
 *
 * <p>Example usage:</p>
 * <pre>
 *     // Register a listener for the Pre event
 *     getEventRegistry().register(AttachmentEvent.Pre.class, event -> {
 *         if (event.isCancelled()) {
 *             return;
 *         }
 *         // Custom logic before the attachment is installed or removed
 *     });
 *
 *     // Register a listener for the Post event
 *     getEventRegistry().register(AttachmentEvent.Post.class, event -> {
 *         String attachmentId = event.getAttachmentItemId();
 *         // Custom logic after the attachment is installed or removed
 *     });
 * </pre>
 */
public abstract class AttachmentEvent implements IEvent<Void> {

    /**
     * A reference to the player's entity store.
     */
    protected final Ref<EntityStore> playerRef;
    /**
     * The state of the firearm at the time of the attachment event.
     */
    protected final FirearmState state;
    /**
     * The type of attachment slot (e.g., optic, muzzle, grip).
     */
    protected final AttachmentType slotType;
    /**
     * The ID of the attachment item being installed or removed.
     */
    protected String attachmentItemId;

    /**
     * Constructs a new {@code AttachmentEvent} with the specified player reference, firearm state,
     * attachment slot type, and attachment item ID.
     *
     * @param playerRef        A reference to the player's entity store.
     * @param state            The state of the firearm.
     * @param slotType         The type of attachment slot.
     * @param attachmentItemId The ID of the attachment item being installed or removed.
     */
    protected AttachmentEvent(Ref<EntityStore> playerRef, FirearmState state, AttachmentType slotType, String attachmentItemId) {
        this.playerRef = playerRef;
        this.state = state;
        this.slotType = slotType;
        this.attachmentItemId = attachmentItemId;
    }

    /**
     * Returns a reference to the player's entity store.
     *
     * @return A reference to the player's entity store.
     */
    public Ref<EntityStore> getPlayerRef() {
        return playerRef;
    }

    /**
     * Returns the state of the firearm at the time of the attachment event.
     *
     * @return The state of the firearm.
     */
    public FirearmState getState() {
        return state;
    }

    /**
     * Returns the type of attachment slot.
     *
     * @return The type of attachment slot.
     */
    public AttachmentType getSlotType() {
        return slotType;
    }

    /**
     * Returns the ID of the attachment item being installed or removed.
     *
     * @return The ID of the attachment item.
     */
    public String getAttachmentItemId() {
        return attachmentItemId;
    }

    /**
     * Returns whether an attachment is being installed.
     *
     * @return {@code true} if an attachment is being installed, {@code false} otherwise.
     */
    public boolean isInstalling() {
        return attachmentItemId != null;
    }

    /**
     * The {@code Pre} class represents an event that is dispatched before an attachment is installed or removed.
     * This event is cancellable, allowing other systems to prevent the attachment from being installed or removed.
     */
    public static class Pre extends AttachmentEvent implements ICancellable {

        /**
         * Indicates whether the event is cancelled.
         */
        private boolean cancelled = false;

        /**
         * Constructs a new {@code Pre} event with the specified player reference, firearm state,
         * attachment slot type, and attachment item ID.
         *
         * @param playerRef        A reference to the player's entity store.
         * @param state            The state of the firearm.
         * @param slotType         The type of attachment slot.
         * @param attachmentItemId The ID of the attachment item being installed or removed.
         */
        public Pre(Ref<EntityStore> playerRef, FirearmState state, AttachmentType slotType, String attachmentItemId) {
            super(playerRef, state, slotType, attachmentItemId);
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

        /**
         * Sets the ID of the attachment item being installed or removed.
         *
         * @param attachmentItemId The ID of the attachment item.
         */
        public void setAttachmentItemId(String attachmentItemId) {
            this.attachmentItemId = attachmentItemId;
        }
    }

    /**
     * The {@code Post} class represents an event that is dispatched after an attachment is installed or removed.
     * This event provides additional information about the attachment process after it has been completed.
     */
    public static class Post extends AttachmentEvent {

        /**
         * Constructs a new {@code Post} event with the specified player reference, firearm state,
         * attachment slot type, and attachment item ID.
         *
         * @param playerRef        A reference to the player's entity store.
         * @param state            The state of the firearm.
         * @param slotType         The type of attachment slot.
         * @param attachmentItemId The ID of the attachment item being installed or removed.
         */
        public Post(Ref<EntityStore> playerRef, FirearmState state, AttachmentType slotType, String attachmentItemId) {
            super(playerRef, state, slotType, attachmentItemId);
        }
    }
}