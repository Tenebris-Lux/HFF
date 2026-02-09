package lucis.lux.hff.components;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.Component;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

/**
 * The {@code AmmoComponent} class represents the ammunition properties and state of a firearm
 * within the game. It is a {@link Component} attached to entities that can hold or use ammunition,
 * such as firearms or other projectile-based weapons.
 *
 * <p>>This component stores the following properties:
 * <ul>
 *     <li>{@code velocityMod}: A modifier for the projectile's velocity.</li>
 *     <li>{@code damageMod}: A modifier for the projectile's damage.</li>
 *     <li>{@code spreadMod}: A modifier for the projectile's spread.</li>
 *     <li>{@code loadedAmount}: The current number of projectiles loaded in the firearm.</li>
 *     <li>{@code ammoName}: The name of the ammunition type.</li>
 *     <li>{@code isReloading}: A flag indicating whether the firearm is currently reloading.</li>
 * </ul></p>
 *
 * <p>This component is serializable and deserializable using the provided {@link BuilderCodec}.
 * It supports cloning to create independent copies of the component.</p>
 */
public class AmmoComponent implements Component<EntityStore> {

    /**
     * The {@link BuilderCodec} for serializing and deserializing this component.
     * It defines how each field is encoded and decoded.
     */
    public static final BuilderCodec<AmmoComponent> CODEC = BuilderCodec.builder(AmmoComponent.class, AmmoComponent::new)
            .append(new KeyedCodec<>("VelocityMod", Codec.FLOAT), (c, v) -> c.velocityMod = v, c -> c.velocityMod)
            .add()
            .append(new KeyedCodec<>("DamageMod", Codec.FLOAT), (c, v) -> c.damageMod = v, c -> c.damageMod)
            .add()
            .append(new KeyedCodec<>("SpreadMod", Codec.FLOAT), (c, v) -> c.spreadMod = v, c -> c.spreadMod)
            .add()
            .append(new KeyedCodec<>("AmmoName", Codec.STRING), (c, v) -> c.ammoName = v, c -> c.ammoName)
            .add()
            .append(new KeyedCodec<>("IsReloading", Codec.BOOLEAN), (c, v) -> c.isReloading = v, c -> c.isReloading)
            .add()
            .build();

    private float velocityMod;
    private float damageMod;
    private float spreadMod;
    private int loadedAmount;
    private String ammoName;
    private boolean isReloading;

    /**
     * Constructs a new {@code AmmoComponent} with default values.
     * Default values are:
     * <ul>
     *     <li>{@code velocityMod = 1.0f}</li>
     *     <li>{@code damageMod = 1.0f}</li>
     *     <li>{@code spreadMod = 1.0f}</li>
     *     <li>{@code loadedAmount = 0}</li>
     *     <li>{@code ammoName = null}</li>
     *     <li>{@code isReloading = false}</li>
     * </ul>
     */
    public AmmoComponent() {
        this.damageMod = 1.0f;
        this.spreadMod = 1.0f;
        this.velocityMod = 1.0f;
        this.loadedAmount = 0;
        this.ammoName = null;
        this.isReloading = false;
    }

    /**
     * Constructs a new {@code AmmoComponent} with the specified modifiers and ammunition name.
     *
     * @param damageMod   The damage modifier for the ammunition.
     * @param spreadMod   The spread modifier for the ammunition.
     * @param velocityMod The velocity modifier for the ammunition.
     * @param ammoName    The name of the ammunition type.
     */
    public AmmoComponent(float damageMod, float spreadMod, float velocityMod, String ammoName) {
        this.velocityMod = velocityMod;
        this.spreadMod = spreadMod;
        this.damageMod = damageMod;
        this.ammoName = ammoName;
    }

    /**
     * Constructs a new {@code AmmoComponent} as a copy of another component.
     *
     * @param other The component to copy.
     */
    public AmmoComponent(AmmoComponent other) {
        this.damageMod = other.damageMod;
        this.spreadMod = other.spreadMod;
        this.velocityMod = other.velocityMod;
        this.loadedAmount = other.loadedAmount;
        this.ammoName = other.ammoName;
        this.isReloading = other.isReloading;
    }

    /**
     * Creates and returns a copy of this component.
     *
     * @return A new instance of {@code AmmoComponent} with the same values as this component.
     */
    @NullableDecl
    @Override
    public Component<EntityStore> clone() {
        return new AmmoComponent(this);
    }

    /**
     * Returns the velocity modifier of the ammunition.
     *
     * @return The velocity modifier.
     */
    public float getVelocityMod() {
        return velocityMod;
    }

    /**
     * Sets the velocity modifier of the ammunition.
     *
     * @param velocityMod The new velocity modifier.
     */
    public void setVelocityMod(float velocityMod) {
        this.velocityMod = velocityMod;
    }

    /**
     * Returns the damage modifier of the ammunition.
     *
     * @return The damage modifier.
     */
    public float getDamageMod() {
        return damageMod;
    }

    /**
     * Sets the damage modifier of the ammunition.
     *
     * @param damageMod The new damage modifier.
     */
    public void setDamageMod(float damageMod) {
        this.damageMod = damageMod;
    }

    /**
     * Returns the spread modifier of the ammunition.
     *
     * @return The spread modifier.
     */
    public float getSpreadMod() {
        return spreadMod;
    }

    /**
     * Sets the spread modifier of the ammunition.
     *
     * @param spreadMod The new spread modifier.
     */
    public void setSpreadMod(float spreadMod) {
        this.spreadMod = spreadMod;
    }

    /**
     * Returns the name of the ammunition type.
     *
     * @return The name of the ammunition type.
     */
    public String getAmmoName() {
        return ammoName;
    }

    /**
     * Sets the name of the ammunition type.
     *
     * @param ammoName The new name of the ammunition type.
     */
    public void setAmmoName(String ammoName) {
        this.ammoName = ammoName;
    }

    /**
     * Returns the current number of projectiles loaded in the firearm.
     *
     * @return The number of loaded projectiles.
     */
    public int getLoadedAmount() {
        return loadedAmount;
    }

    /**
     * Sets the number of projectiles loaded in the firearm.
     *
     * @param loadedAmount The new number of loaded projectiles.
     */
    public void setLoadedAmount(int loadedAmount) {
        this.loadedAmount = loadedAmount;
    }

    /**
     * Returns whether the firearm is currently reloading.
     *
     * @return {@code true} if the firearm is reloading, {@code false} otherwise.
     */
    public boolean isReloading() {
        return isReloading;
    }

    /**
     * Sets whether the firearm is currently reloading.
     *
     * @param reloading {@code true} if the firearm is reloading, {@code false} otherwise.
     */
    public void setReloading(boolean reloading) {
        this.isReloading = reloading;
    }

    /**
     * Toggles the reloading state of the firearm.
     * If the firearm is reloading, it will stop reloading, and vice versa.
     */
    public void toggleReloading() {
        isReloading = !isReloading;
    }

    /**
     * Increments the number of loaded projectiles by one.
     */
    public void incrementLoadedAmount() {
        this.loadedAmount++;
    }

    /**
     * Uses one projectile from the firearm.
     *
     * @return {@code true} if a projectile was used, {@code false} if no projectiles were loaded.
     */
    public boolean useAmmo() {
        if (this.loadedAmount > 0) {
            this.loadedAmount--;
            return true;
        } else return false;
    }
}
