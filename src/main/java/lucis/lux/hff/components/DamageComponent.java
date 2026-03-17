package lucis.lux.hff.components;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.Component;
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

/**
 * The {@code DamageComponent} class is a component that represents the damage properties of a projectile.
 * This component is used to track and manage various attributes related to damage, such as the base damage,
 * optimal and maximum range, damage falloff, knockback force, and flight direction.
 *
 * <p>This component is part of the Entity Component System (ECS) architecture in Hytale and is used to
 * calculate the damage dealt by projectiles based on their flight distance and other factors.</p>
 *
 * <p>Example usage:</p>
 * <pre>
 *     // Create a new DamageComponent with default values
 *     DamageComponent damageComponent = new DamageComponent();
 *
 *     // Set custom damage properties
 *     damageComponent.setDamage(10.0f);
 *     damageComponent.setOptimalRange(20.0f);
 *     damageComponent.setMaxRange(100.0f);
 *     damageComponent.setMinDamageMultiplier(0.1f);
 *     damageComponent.setKnockbackForce(0.5f);
 *     damageComponent.setFlightDirection(new Vector3d(0, 0, 1));
 * </pre>
 */
public class DamageComponent implements Component<EntityStore> {

    /**
     * The {@link BuilderCodec} for serializing and deserializing this component.
     */
    public static final BuilderCodec<DamageComponent> CODEC = BuilderCodec.builder(DamageComponent.class, DamageComponent::new)
            .append(new KeyedCodec<>("Damage", Codec.FLOAT), (c, v) -> c.damage = v, c -> c.damage)
            .add()
            .build();

    /**
     * The base damage dealt by the projectile.
     */
    private float damage;

    /**
     * The starting position of the projectile.
     */
    private Vector3d startPosition;

    /**
     * The optimal range at which the projectile deals full damage.
     */
    private float optimalRange;

    /**
     * The maximum range at which the projectile can deal damage.
     */
    private float maxRange;

    /**
     * The minimum damage multiplier applied when the projectile is at maximum range.
     */
    private float minDamageMultiplier;

    /**
     * The force of the knockback applied when the projectile hits a target.
     */
    private float knockbackForce;

    /**
     * The direction in which the projectile is flying.
     */
    private Vector3d flightDirection;

    /**
     * Constructs a new {@code DamageComponent} with default values.
     * Default values are:
     * <ul>
     *   <li>Damage: 1</li>
     *   <li>Optimal Range: 15.0</li>
     *   <li>Maximum Range: 30.0</li>
     *   <li>Minimum Damage Multiplier: 0.2</li>
     *   <li>Knockback Force: 0.1</li>
     *   <li>Start Position: (0, 0, 0)</li>
     *   <li>Flight Direction: (0, 0, 0)</li>
     * </ul>
     */
    public DamageComponent() {
        this.damage = 1;
        this.maxRange = 30.0f;
        this.minDamageMultiplier = 0.2f;
        this.optimalRange = 15.0f;
        this.startPosition = new Vector3d();
        this.knockbackForce = 0.1f;
        this.flightDirection = new Vector3d();
    }

    /**
     * Constructs a new {@code DamageComponent} with the specified damage properties.
     *
     * @param damage              The base damage dealt by the projectile.
     * @param startPosition       The starting position of the projectile.
     * @param optimalRange        The optimal range at which the projectile deals full damage.
     * @param maxRange            The maximum range at which the projectile can deal damage.
     * @param minDamageMultiplier The minimum damage multiplier applied when the projectile is at maximum range.
     * @param knockbackForce      The force of the knockback applied when the projectile hits a target.
     * @param flightDirection     The direction in which the projectile is flying.
     */
    public DamageComponent(float damage, Vector3d startPosition, float optimalRange, float maxRange, float minDamageMultiplier, float knockbackForce, Vector3d flightDirection) {
        this.damage = damage;
        this.startPosition = startPosition;
        this.minDamageMultiplier = minDamageMultiplier;
        this.maxRange = maxRange;
        this.optimalRange = optimalRange;
        this.knockbackForce = knockbackForce;
        this.flightDirection = flightDirection;
    }

    /**
     * Constructs a new {@code DamageComponent} by copying the state from another component.
     *
     * @param other The component to copy.
     */
    public DamageComponent(DamageComponent other) {
        this.damage = other.damage;
        this.optimalRange = other.optimalRange;
        this.minDamageMultiplier = other.minDamageMultiplier;
        this.maxRange = other.maxRange;
        this.startPosition = other.startPosition;
        this.knockbackForce = other.knockbackForce;
        this.flightDirection = other.flightDirection;
    }

    /**
     * Creates a copy of this component.
     *
     * @return A copy of this component.
     */
    @NullableDecl
    @Override
    public Component<EntityStore> clone() {
        return new DamageComponent(this);
    }

    /**
     * Returns the base damage dealt by the projectile.
     *
     * @return The base damage.
     */
    public float getDamage() {
        return damage;
    }

    /**
     * Sets the base damage dealt by the projectile.
     *
     * @param damage The base damage to set.
     */
    public void setDamage(float damage) {
        this.damage = damage;
    }

    /**
     * Returns the starting position of the projectile.
     *
     * @return The starting position.
     */
    public Vector3d getStartPosition() {
        return startPosition;
    }

    /**
     * Sets the starting position of the projectile.
     *
     * @param startPosition The starting position to set.
     */
    public void setStartPosition(Vector3d startPosition) {
        this.startPosition = startPosition;
    }

    /**
     * Returns the optimal range at which the projectile deals full damage.
     *
     * @return The optimal range.
     */
    public float getOptimalRange() {
        return optimalRange;
    }

    /**
     * Sets the optimal range at which the projectile deals full damage.
     *
     * @param optimalRange The optimal range to set.
     */
    public void setOptimalRange(float optimalRange) {
        this.optimalRange = optimalRange;
    }

    /**
     * Returns the maximum range at which the projectile can deal damage.
     *
     * @return The maximum range.
     */
    public float getMaxRange() {
        return maxRange;
    }

    /**
     * Sets the maximum range at which the projectile can deal damage.
     *
     * @param maxRange The maximum range to set.
     */
    public void setMaxRange(float maxRange) {
        this.maxRange = maxRange;
    }

    /**
     * Returns the minimum damage multiplier applied when the projectile is at maximum range.
     *
     * @return The minimum damage multiplier.
     */
    public float getMinDamageMultiplier() {
        return minDamageMultiplier;
    }

    /**
     * Sets the minimum damage multiplier applied when the projectile is at maximum range.
     *
     * @param minDamageMultiplier The minimum damage multiplier to set.
     */
    public void setMinDamageMultiplier(float minDamageMultiplier) {
        this.minDamageMultiplier = minDamageMultiplier;
    }

    /**
     * Returns the force of the knockback applied when the projectile hits a target.
     *
     * @return The knockback force.
     */
    public float getKnockbackForce() {
        return knockbackForce;
    }

    /**
     * Sets the force of the knockback applied when the projectile hits a target.
     *
     * @param knockbackForce The knockback force to set.
     */
    public void setKnockbackForce(float knockbackForce) {
        this.knockbackForce = knockbackForce;
    }

    /**
     * Returns the direction in which the projectile is flying.
     *
     * @return The flight direction.
     */
    public Vector3d getFlightDirection() {
        return flightDirection;
    }

    /**
     * Sets the direction in which the projectile is flying.
     *
     * @param flightDirection The flight direction to set.
     */
    public void setFlightDirection(Vector3d flightDirection) {
        this.flightDirection = flightDirection;
    }
}
