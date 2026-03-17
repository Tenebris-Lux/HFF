package lucis.lux.hff.interactions;

import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.event.IEventDispatcher;
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.math.vector.Vector4d;
import com.hypixel.hytale.protocol.ChangeVelocityType;
import com.hypixel.hytale.protocol.InteractionState;
import com.hypixel.hytale.protocol.InteractionType;
import com.hypixel.hytale.server.core.HytaleServer;
import com.hypixel.hytale.server.core.entity.InteractionContext;
import com.hypixel.hytale.server.core.entity.knockback.KnockbackComponent;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.modules.entity.damage.Damage;
import com.hypixel.hytale.server.core.modules.entity.damage.DamageCause;
import com.hypixel.hytale.server.core.modules.entity.damage.DamageSystems;
import com.hypixel.hytale.server.core.modules.interaction.interaction.CooldownHandler;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.SimpleInstantInteraction;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import lucis.lux.hff.HFF;
import lucis.lux.hff.components.DamageComponent;
import lucis.lux.hff.events.FirearmHitEvent;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

/**
 * The {@code HitEnemyInteraction} class is a {@link SimpleInstantInteraction} responsible for handling
 * the logic when a projectile hits an enemy. This interaction calculates the final damage based on distance,
 * applies knockback, and dispatches events to notify other systems about the hit.
 *
 * <p>When triggered, this interaction:</p>
 * <ul>
 *   <li>Retrieves the projectile, shooter, and target entities.</li>
 *   <li>Calculates the distance between the projectile's starting position and the hit position.</li>
 *   <li>Computes the final damage based on the distance and the firearm's damage properties.</li>
 *   <li>Applies the damage to the target and handles knockback.</li>
 *   <li>Dispatches {@link FirearmHitEvent.Pre} and {@link FirearmHitEvent.Post} events to notify other systems.</li>
 * </ul>
 *
 * <p>This interaction is part of the Entity Component System (ECS) architecture in Hytale
 * and is registered during plugin initialization.</p>
 */
public class HitEnemyInteraction extends SimpleInstantInteraction {

    /**
     * The {@link BuilderCodec} for serializing and deserializing this interaction.
     */
    public static final BuilderCodec<HitEnemyInteraction> CODEC = BuilderCodec.builder(
            HitEnemyInteraction.class,
            HitEnemyInteraction::new,
            SimpleInstantInteraction.CODEC
    ).build();

    /**
     * Calculates the final damage based on the distance between the projectile's starting position
     * and the hit position. The damage is reduced based on the distance from the optimal range.
     *
     * @param damage   The damage component of the projectile.
     * @param distance The distance between the projectile's starting position and the hit position.
     * @return The final damage after applying distance-based falloff.
     */
    private static float getFinalDamage(DamageComponent damage, double distance) {
        float finalDamage = damage.getDamage();

        if (distance > damage.getOptimalRange()) {
            if (distance >= damage.getMaxRange()) {
                finalDamage *= damage.getMinDamageMultiplier();
            } else {
                float rangeDiff = damage.getMaxRange() - damage.getOptimalRange();
                float distPastOptimal = (float) (distance - damage.getOptimalRange());

                float falloffPercentage = distPastOptimal / rangeDiff;

                float maxDamageLoss = 1.0f - damage.getMinDamageMultiplier();
                finalDamage *= (1.0f - (falloffPercentage * maxDamageLoss));
            }
        }

        finalDamage *= HFF.get().getConfigData().getGlobalDamageMultiplier();
        return finalDamage;
    }

    /**
     * Called when the interaction is first run. This method handles the logic when a projectile hits an enemy.
     *
     * <p>The following steps are performed:</p>
     * <ol>
     *   <li>Retrieves the projectile, shooter, and target entities.</li>
     *   <li>Calculates the hit position and retrieves the damage component of the projectile.</li>
     *   <li>Dispatches a {@link FirearmHitEvent.Pre} event to notify other systems about the hit.</li>
     *   <li>Calculates the final damage based on the distance and applies it to the target.</li>
     *   <li>Applies knockback to the target based on the projectile's flight direction and force.</li>
     *   <li>Dispatches a {@link FirearmHitEvent.Post} event to notify other systems about the hit.</li>
     * </ol>
     *
     * @param interactionType    The type of interaction.
     * @param interactionContext The context of the interaction, including references to the projectile, shooter, and target.
     * @param cooldownHandler    The handler for managing cooldowns.
     */
    @Override
    protected void firstRun(@NonNullDecl InteractionType interactionType, @NonNullDecl InteractionContext interactionContext, @NonNullDecl CooldownHandler cooldownHandler) {
        Ref<EntityStore> projectile = interactionContext.getEntity();
        Ref<EntityStore> shooter = interactionContext.getOwningEntity();
        Ref<EntityStore> target = interactionContext.getTargetEntity();

        TransformComponent targetTransform = interactionContext.getCommandBuffer().getComponent(target, TransformComponent.getComponentType());
        Vector3d hitPosition = targetTransform.getPosition();

        DamageComponent damage = interactionContext.getCommandBuffer().getComponent(projectile, HFF.get().getDamageComponentType());

        if (damage != null) {

            // Dispatch the FirearmHitEvent.Pre event
            IEventDispatcher<FirearmHitEvent.Pre, FirearmHitEvent.Pre> preDispatcher = HytaleServer.get().getEventBus().dispatchFor(FirearmHitEvent.Pre.class);

            if (preDispatcher.hasListener()) {
                FirearmHitEvent.Pre pre = new FirearmHitEvent.Pre(target, shooter, projectile, damage.getDamage());
                preDispatcher.dispatch(pre);

                if (pre.isCancelled()) {
                    interactionContext.getState().state = InteractionState.Failed;
                    return;
                }

                damage.setDamage(pre.getFinalDamage());
            }

            // Calculate the distance between the projectile's starting position and the hit position
            double distance = damage.getStartPosition().distanceTo(hitPosition);

            // Calculate the final damage based on distance
            float finalDamage = getFinalDamage(damage, distance);

            // Create a damage object to apply to the target
            Damage.ProjectileSource source = new Damage.ProjectileSource(shooter, projectile);
            DamageCause cause = DamageCause.getAssetMap().getAsset("Projectile");
            Damage damageObj = new Damage(source, cause, finalDamage);

            // Store the hit location in the damage object
            damageObj.putMetaObject(Damage.HIT_LOCATION, Vector4d.newPosition(hitPosition));

            // Calculate the knockback direction and force
            Vector3d impactDir = damage.getFlightDirection().clone();
            impactDir.normalize();

            float force = damage.getKnockbackForce();

            Vector3d kbVelocity = new Vector3d(
                    impactDir.getX() * force,
                    (impactDir.getY() * force) + (force * 0.2f),
                    impactDir.getZ() * force);

            // Create a knockback component to apply to the target
            KnockbackComponent knockback = new KnockbackComponent();
            knockback.setVelocity(kbVelocity);
            knockback.setVelocityType(ChangeVelocityType.Add);
            knockback.setDuration(0.0f);

            // Store the knockback component in the damage object
            damageObj.putMetaObject(Damage.KNOCKBACK_COMPONENT, knockback);

            // Apply the damage and knockback to the target
            DamageSystems.executeDamage(target, interactionContext.getCommandBuffer(), damageObj);

            // Log debug information if debug mode is enabled
            if (HFF.get().getConfigData().isDebugMode()) {
                HFF.get().getLogger().atInfo().log("Hit at " + String.format("%.1f", distance) + "m. Damage: " + finalDamage);
            }

            // Dispatch the FirearmHitEvent.Post event
            IEventDispatcher<FirearmHitEvent.Post, FirearmHitEvent.Post> postDispatcher = HytaleServer.get().getEventBus().dispatchFor(FirearmHitEvent.Post.class);

            if (postDispatcher.hasListener()) {
                FirearmHitEvent.Post post = new FirearmHitEvent.Post(target, shooter, projectile, finalDamage);
                postDispatcher.dispatch(post);
            }
        }
    }
}
