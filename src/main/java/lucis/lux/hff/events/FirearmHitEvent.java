package lucis.lux.hff.events;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.event.ICancellable;
import com.hypixel.hytale.event.IEvent;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

/**
 * The {@code FirearmHitEvent} class is an abstract event that represents a projectile from a firearm
 * hitting a target. This class provides two nested classes, {@link Pre} and {@link Post}, to represent events
 * before and after a projectile hits a target.
 *
 * <p>The {@code Pre} event is cancellable and can be used to prevent the hit from occurring or to modify
 * the final damage dealt. The {@link Post} event provides additional information about the hit after it has occurred.</p>
 *
 * <p>This event is typically used in the HFF (Hytale Firearm Framework) plugin to notify other systems about
 * firearm hit events and to allow for custom behaviour before or after a projectile hits a target.</p>
 *
 * <p>Example usage:</p>
 * <pre>
 *     // Register a listener for the Pre event
 *     getEventRegistry().register(FirearmHitEvent.Pre.class, event -> {
 *         if (event.isCancelled()) {
 *             return;
 *         }
 *         // Custom logic before the hit occurs
 *         event.setFinalDamage(event.getFinalDamage() * 1.5f); // Increase damage by 50%
 *     });
 *
 *     // Register a listener for the Post event
 *     getEventRegistry().register(FirearmHitEvent.Post.class, event -> {
 *         Ref<EntityStore> target = event.getTarget();
 *         float finalDamage = event.getFinalDamage();
 *         // Custom logic after the hit occurs
 *     });
 * </pre>
 */
public abstract class FirearmHitEvent implements IEvent<Void> {

    /**
     * A reference to the entity that was hit by the projectile.
     */
    protected final Ref<EntityStore> target;
    /**
     * A reference to the entity that fired the projectile.
     */
    protected final Ref<EntityStore> shooter;
    /**
     * A reference to the projectile entity.
     */
    protected final Ref<EntityStore> projectile;
    /**
     * The final damage dealt by the projectile to the target.
     */
    protected float finalDamage;

    /**
     * Constructs a new {@code FirearmHitEvent} with the specified target, shooter, projectile, and final damage.
     *
     * @param target      A reference to the entity that was hit by the projectile.
     * @param shooter     A reference to the entity that fired the projectile.
     * @param projectile  A reference to the projectile entity.
     * @param finalDamage The final damage dealt by the projectile to the target.
     */
    protected FirearmHitEvent(Ref<EntityStore> target, Ref<EntityStore> shooter, Ref<EntityStore> projectile, float finalDamage) {
        this.target = target;
        this.projectile = projectile;
        this.shooter = shooter;
        this.finalDamage = finalDamage;
    }

    /**
     * Returns a reference to the projectile entity.
     *
     * @return A reference to the projectile entity.
     */
    public Ref<EntityStore> getProjectile() {
        return projectile;
    }

    /**
     * Returns a reference to the entity that fired the projectile.
     *
     * @return A reference to the entity that fired the projectile.
     */
    public Ref<EntityStore> getShooter() {
        return shooter;
    }

    /**
     * Returns a reference to the entity that was hit by the projectile.
     *
     * @return A reference to the entity that was hit by the projectile.
     */
    public Ref<EntityStore> getTarget() {
        return target;
    }

    /**
     * Returns the final damage dealt by the projectile to the target.
     *
     * @return The final damage dealt by the projectile.
     */
    public float getFinalDamage() {
        return finalDamage;
    }

    /**
     * The {@code Pre} class represents an event that is dispatched before a projectile hits a target.
     * This event is cancellable and allows for modifying the final damage dealt.
     */
    public static class Pre extends FirearmHitEvent implements ICancellable {

        /**
         * Indicates whether the event is cancelled.
         */
        private boolean cancelled = false;

        /**
         * Constructs a new {@code Pre} event with the specified target, shooter, projectile, and final damage.
         *
         * @param target      A reference to the entity that was hit by the projectile.
         * @param shooter     A reference to the entity that fired the projectile.
         * @param projectile  A reference to the projectile entity.
         * @param finalDamage The final damage dealt by the projectile to the target.
         */
        public Pre(Ref<EntityStore> target, Ref<EntityStore> shooter, Ref<EntityStore> projectile, float finalDamage) {
            super(target, shooter, projectile, finalDamage);
        }

        /**
         * Sets the final damage dealt by the projectile to the target.
         *
         * @param finalDamage The final damage to set.
         */
        public void setFinalDamage(float finalDamage) {
            this.finalDamage = finalDamage;
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
     * The {@code Post} class represents an event that is dispatched after a projectile hits a target.
     * This event provides additional information about the hit after it has occurred.
     */
    public static class Post extends FirearmHitEvent {

        /**
         * Constructs a new {@code Post} event with the specified target, shooter, projectile, and final damage.
         *
         * @param target      A reference to the entity that was hit by the projectile.
         * @param shooter     A reference to the entity that fired the projectile.
         * @param projectile  A reference to the projectile entity.
         * @param finalDamage The final damage dealt by the projectile to the target.
         */
        public Post(Ref<EntityStore> target, Ref<EntityStore> shooter, Ref<EntityStore> projectile, float finalDamage) {
            super(target, shooter, projectile, finalDamage);
        }
    }
}