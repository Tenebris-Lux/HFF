package lucis.lux.hff.components;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.Component;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import lucis.lux.hff.components.enums.FireMode;
import lucis.lux.hff.components.enums.FirearmClass;
import lucis.lux.hff.components.enums.FirearmType;

import javax.annotation.Nullable;

/**
 * The {@code FirearmStatsComponent} class represents the core statistics and properties of a firearm
 * in the HFF (Hytale Firearm Framework) plugin. This component stores all relevant data
 * for firearm behaviour, such as rate of fire, projectile velocity, recoil, and supported ammunition types.
 *
 * <p>This component is typically attached to firearm items or entities and is used by systems like
 * {@link lucis.lux.hff.systems.FirearmSystem} to simulate realistic firearm mechanics, including
 * shooting, reloading, and malfunctions.</p>
 *
 * <p>All values can be loaded from JSON assets or set programmatically, allowing for flexible
 * configuration and modding support.</p>
 */
public class FirearmStatsComponent implements Component<EntityStore> {

    /**
     * The {@link BuilderCodec} for serializing and deserializing the {@code FirearmsStatsComponent}.
     * This codec handles all fields required to define a firearm's behaviour and properties.
     */
    public static final BuilderCodec<FirearmStatsComponent> CODEC = BuilderCodec.builder(FirearmStatsComponent.class, FirearmStatsComponent::new)
            .append(new KeyedCodec<>("RPM", Codec.DOUBLE), (c, v) -> c.rpm = v, c -> c.rpm)
            .add()
            .append(new KeyedCodec<>("ProjectileVelocity", Codec.DOUBLE), (c, v) -> c.projectileVelocity = v, c -> c.projectileVelocity)
            .add()
            .append(new KeyedCodec<>("ProjectileAmount", Codec.INTEGER), (c, v) -> c.projectileAmount = v, c -> c.projectileAmount)
            .add()
            .append(new KeyedCodec<>("SpreadBase", Codec.DOUBLE), (c, v) -> c.spreadBase = v, c -> c.spreadBase)
            .add()
            .append(new KeyedCodec<>("MovementPenalty", Codec.DOUBLE), (c, v) -> c.movementPenalty = v, c -> c.movementPenalty)
            .add()
            .append(new KeyedCodec<>("MisfireChance", Codec.DOUBLE), (c, v) -> c.misfireChance = v, c -> c.misfireChance)
            .add()
            .append(new KeyedCodec<>("JamChance", Codec.DOUBLE), (c, v) -> c.jamChance = v, c -> c.jamChance)
            .add()
            .append(new KeyedCodec<>("VerticalRecoil", Codec.DOUBLE), (c, v) -> c.verticalRecoil = v, c -> c.verticalRecoil)
            .add()
            .append(new KeyedCodec<>("HorizontalRecoil", Codec.DOUBLE), (c, v) -> c.horizontalRecoil = v, c -> c.horizontalRecoil)
            .add()
            .append(new KeyedCodec<>("Disabled", Codec.BOOLEAN), (c, v) -> c.disabled = v, c -> c.disabled)
            .add()
            .append(new KeyedCodec<>("ReloadTime", Codec.FLOAT), (c, v) -> c.reloadTime = v, c -> c.reloadTime)
            .add()
            .append(new KeyedCodec<>("ProjectileCapacity", Codec.INTEGER), (c, v) -> c.projectileCapacity = v, c -> c.projectileCapacity)
            .add()
            .append(new KeyedCodec<>("AmmoName", Codec.STRING_ARRAY), (c, v) -> c.ammoName = v, c -> c.ammoName)
            .add()
            .build();
    /**
     * Key for identifying the {@code FirearmStatsComponent} in serialized data.
     */
    public static final KeyedCodec<FirearmStatsComponent> KEY = new KeyedCodec<>("HFF_FIREARM_COMPONENT", CODEC);
    /**
     * Reference to the player entity that owns or uses this firearm.
     */
    Ref<EntityStore> playerRef;
    /**
     * Time in seconds required to reload the firearm.
     */
    private float reloadTime;
    /**
     * Rounds per minute (rate of fire).
     */
    private double rpm;
    /**
     * Velocity of the projectile when fired.
     */
    private double projectileVelocity;
    /**
     * Number of projectiles fired per shot (e.g., shotgun pellets).
     */
    private int projectileAmount;
    /**
     * Maximum number of projectiles the firearm can hold.
     */
    private int projectileCapacity;
    /**
     * Base spread of projectiles (in degrees).
     */
    private double spreadBase;
    /**
     * Penalty to accuracy when moving.
     */
    private double movementPenalty;
    /**
     * Chance of the firearm misfiring (0.0 to 1.0).
     */
    private double misfireChance;
    /**
     * Chance of the firearm jamming (0.0 to 1.0).
     */
    private double jamChance;
    /**
     * Vertical recoil strength.
     */
    private double verticalRecoil;
    /**
     * Horizontal recoil strength.
     */
    private double horizontalRecoil;
    /**
     * Historical and mechanical classification of the firearm.
     */
    private FirearmClass firearmClass;
    /**
     * Functional type of the firearm (e.g., handgun, rifle).
     */
    private FirearmType firearmType;
    /**
     * Firing mode of the firearm (e.g., semi-automatic, automatic).
     */
    private FireMode fireMode;
    /**
     * Whether the firearm is currently disabled.
     */
    private boolean disabled;
    /**
     * Names of compatible ammunition types.
     */
    private String[] ammoName;

    /**
     * Constructs a new {@code FirearmStatsComponent} with default values.
     * Defaults are set to represent a basic, functional firearm.
     */
    public FirearmStatsComponent() {
        this(1f, 1f, 1, 0f, 0f, 0f, 0f, 0f, 0f, false, 1, 1.0f, new String[0]);
    }

    /**
     * Constructs a new {@code FirearmStatsComponent} with the specified parameters.
     *
     * @param rpm                Rounds per minute.
     * @param projectileVelocity Velocity of the projectile.
     * @param projectileAmount   Number of projectiles per shot.
     * @param spreadBase         Base spread of projectiles.
     * @param movementPenalty    Accuracy penalty when moving.
     * @param misfireChance      Chance of misfire.
     * @param jamChance          Chance of jamming.
     * @param verticalRecoil     Vertical recoil strength.
     * @param horizontalRecoil   Horizontal recoil strength.
     * @param disabled           Whether the firearm is disabled.
     * @param projectileCapacity Maximum projectile capacity.
     * @param reloadTime         Time in seconds to reload a single projectile.
     * @param ammoName           Array of compatible ammunition names.
     */
    public FirearmStatsComponent(double rpm, double projectileVelocity, int projectileAmount, double spreadBase, double movementPenalty, double misfireChance, double jamChance, double verticalRecoil, double horizontalRecoil, boolean disabled, int projectileCapacity, float reloadTime, String[] ammoName) {
        this.verticalRecoil = verticalRecoil;
        this.spreadBase = spreadBase;
        this.rpm = rpm;
        this.projectileVelocity = projectileVelocity;
        this.projectileAmount = projectileAmount;
        this.movementPenalty = movementPenalty;
        this.misfireChance = misfireChance;
        this.jamChance = jamChance;
        this.horizontalRecoil = horizontalRecoil;
        this.disabled = disabled;
        this.projectileCapacity = projectileCapacity;
        this.reloadTime = reloadTime;
        this.ammoName = ammoName;
    }

    /**
     * Constructs a new {@code FirearmStatsComponent} as a copy of another.
     *
     * @param other The component to copy.
     */
    public FirearmStatsComponent(FirearmStatsComponent other) {
        this.horizontalRecoil = other.horizontalRecoil;
        this.jamChance = other.jamChance;
        this.misfireChance = other.misfireChance;
        this.movementPenalty = other.movementPenalty;
        this.projectileAmount = other.projectileAmount;
        this.projectileVelocity = other.projectileVelocity;
        this.rpm = other.rpm;
        this.spreadBase = other.spreadBase;
        this.verticalRecoil = other.verticalRecoil;
        this.projectileCapacity = other.projectileCapacity;
        this.disabled = other.disabled;
        this.reloadTime = other.reloadTime;
        this.ammoName = other.ammoName;
        this.playerRef = other.playerRef;
    }

    /**
     * Creates and returns a copy of this component.
     *
     * @return A new {@code FirearmStatsComponent} with the same values as this component.
     */
    @Nullable
    @Override
    public Component<EntityStore> clone() {
        return new FirearmStatsComponent(this);
    }

    /**
     * Returns the firearm's rate of fire in rounds per minute.
     *
     * @return The rounds per minute.
     */
    public double getRpm() {
        return rpm;
    }

    /**
     * Sets the firearm's rate of fire in rounds per minute.
     *
     * @param rpm The new rounds per minute value.
     */
    public void setRpm(double rpm) {
        this.rpm = rpm;
    }

    /**
     * Returns the velocity of the projectile.
     *
     * @return The projectile velocity.
     */
    public double getProjectileVelocity() {
        return projectileVelocity;
    }

    /**
     * Sets the velocity of the projectile.
     *
     * @param projectileVelocity The new projectile velocity.
     */
    public void setProjectileVelocity(double projectileVelocity) {
        this.projectileVelocity = projectileVelocity;
    }

    /**
     * Returns the number of projectiles fired per shot.
     *
     * @return The projectile amount.
     */
    public int getProjectileAmount() {
        return projectileAmount;
    }

    /**
     * Sets the number of projectiles fired per shot.
     *
     * @param projectileAmount The new projectile amount.
     */
    public void setProjectileAmount(int projectileAmount) {
        this.projectileAmount = projectileAmount;
    }

    /**
     * Returns the base spread of projectiles.
     *
     * @return The base spread.
     */
    public double getSpreadBase() {
        return spreadBase;
    }

    /**
     * Sets the base spread of projectiles.
     *
     * @param spreadBase The new base spread.
     */
    public void setSpreadBase(double spreadBase) {
        this.spreadBase = spreadBase;
    }

    /**
     * Returns the accuracy penalty when moving.
     *
     * @return The movement penalty.
     */
    public double getMovementPenalty() {
        return movementPenalty;
    }

    /**
     * Sets the accuracy penalty when moving.
     *
     * @param movementPenalty The new movement penalty.
     */
    public void setMovementPenalty(double movementPenalty) {
        this.movementPenalty = movementPenalty;
    }

    /**
     * Returns the chance of the firearm misfiring.
     *
     * @return The misfire chance.
     */
    public double getMisfireChance() {
        return misfireChance;
    }

    /**
     * Sets the chance of the firearm misfiring.
     *
     * @param misfireChance The new misfire chance.
     */
    public void setMisfireChance(double misfireChance) {
        this.misfireChance = misfireChance;
    }

    /**
     * Returns the chance of the firearm jamming.
     *
     * @return The jam chance.
     */
    public double getJamChance() {
        return jamChance;
    }

    /**
     * Sets the chance of the firearm jamming.
     *
     * @param jamChance The new jam chance.
     */
    public void setJamChance(double jamChance) {
        this.jamChance = jamChance;
    }

    /**
     * Returns the vertical recoil strength.
     *
     * @return The vertical recoil.
     */
    public double getVerticalRecoil() {
        return verticalRecoil;
    }

    /**
     * Sets the vertical recoil strength.
     *
     * @param verticalRecoil The new vertical recoil.
     */
    public void setVerticalRecoil(double verticalRecoil) {
        this.verticalRecoil = verticalRecoil;
    }

    /**
     * Returns whether the firearm is disabled.
     *
     * @return {@code true} if the firearm is disabled, {@code false} otherwise.
     */
    public boolean isDisabled() {
        return disabled;
    }

    /**
     * Sets whether the firearm is disabled.
     *
     * @param disabled The new disabled state.
     */
    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }

    /**
     * Returns the horizontal recoil strength.
     *
     * @return The horizontal recoil.
     */
    public double getHorizontalRecoil() {
        return horizontalRecoil;
    }

    /**
     * Sets the horizontal recoil strength.
     *
     * @param horizontalRecoil The new horizontal recoil.
     */
    public void setHorizontalRecoil(double horizontalRecoil) {
        this.horizontalRecoil = horizontalRecoil;
    }

    /**
     * Returns the historical and mechanical classification of the firearm.
     *
     * @return The firearm class.
     */
    public FirearmClass getFirearmClass() {
        return firearmClass;
    }

    /**
     * Sets the historical and mechanical classification of the firearm.
     *
     * @param firearmClass The new firearm class.
     */
    public void setFirearmClass(FirearmClass firearmClass) {
        this.firearmClass = firearmClass;
    }

    /**
     * Returns the functional type of the firearm.
     *
     * @return The firearm type.
     */
    public FirearmType getFirearmType() {
        return firearmType;
    }

    /**
     * Sets the functional type of the firearm.
     *
     * @param firearmType The new firearm type.
     */
    public void setFirearmType(FirearmType firearmType) {
        this.firearmType = firearmType;
    }

    /**
     * Returns the firing mode of the firearm.
     *
     * @return The fire mode.
     */
    public FireMode getFireMode() {
        return fireMode;
    }

    /**
     * Sets the firing mode of the firearm.
     *
     * @param fireMode The new fire mode.
     */
    public void setFireMode(FireMode fireMode) {
        this.fireMode = fireMode;
    }

    /**
     * Returns the cooldown time between shots in seconds.
     *
     * @return The cooldown time.
     */
    public float getCooldown() {
        return (float) (60 / rpm);
    }

    /**
     * Returns the maximum projectile capacity.
     *
     * @return The projectile capacity.
     */
    public int getProjectileCapacity() {
        return projectileCapacity;
    }

    /**
     * Sets the maximum projectile capacity.
     *
     * @param projectileCapacity The new projectile capacity.
     */
    public void setProjectileCapacity(int projectileCapacity) {
        this.projectileCapacity = projectileCapacity;
    }

    /**
     * Returns the time in seconds required to reload the firearm.
     *
     * @return The reload time.
     */
    public float getReloadTime() {
        return reloadTime;
    }

    /**
     * Sets the time in seconds required to reload the firearm.
     *
     * @param reloadTime The new reload time.
     */
    public void setReloadTime(float reloadTime) {
        this.reloadTime = reloadTime;
    }

    /**
     * Returns the names of compatible ammunition types.
     *
     * @return The array of ammunition names.
     */
    public String[] getAmmoName() {
        return ammoName;
    }

    /**
     * Sets the names of compatible ammunition types.
     *
     * @param ammoName The new array of ammunition names.
     */
    public void setAmmoName(String[] ammoName) {
        this.ammoName = ammoName;
    }

    /**
     * Returns the reference to the player entity that owns or uses this firearm.
     *
     * @return The player reference.
     */
    public Ref<EntityStore> getPlayerRef() {
        return playerRef;
    }

    /**
     * Sets the reference to the player entity that owns or uses this firearm.
     *
     * @param playerRef The new player reference.
     */
    public void setPlayerRef(Ref<EntityStore> playerRef) {
        this.playerRef = playerRef;
    }
}
