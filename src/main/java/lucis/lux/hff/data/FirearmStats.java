package lucis.lux.hff.data;

import lucis.lux.hff.enums.FireMode;
import lucis.lux.hff.enums.FirearmClass;
import lucis.lux.hff.enums.FirearmType;
import lucis.lux.hff.enums.MagazineType;

/**
 * The {@code FirearmStats} record represents the immutable statistics and properties of a firearm.
 * This record stores all relevant data for firearm behavior, such as rate of fire, projectile velocity,
 * recoil, range, and supported ammunition types.
 *
 * <p>This record is designed to be used in the HFF (Hytale Firearm Framework) plugin to define the properties
 * and behavior of firearms. It provides a convenient way to store and retrieve firearm statistics.</p>
 *
 * <p>Example usage:</p>
 * <pre>
 *     // Create a new FirearmStats object using the builder
 *     FirearmStats stats = FirearmStats.builder()
 *         .reloadTime(2.5f)
 *         .rpm(600.0f)
 *         .projectileVelocity(300.0f)
 *         .projectileAmount(1)
 *         .projectileCapacity(30)
 *         .spreadBase(2.0f)
 *         .movementPenalty(0.5f)
 *         .misfireChance(0.01f)
 *         .jamChance(0.005f)
 *         .verticalRecoil(0.5f)
 *         .horizontalRecoil(0.1f)
 *         .firearmClass(FirearmClass.PISTOL)
 *         .firearmType(FirearmType.HANDGUN)
 *         .fireMode(FireMode.SEMI_AUTOMATIC)
 *         .disabled(false)
 *         .calibre("9mm")
 *         .burstRounds(3)
 *         .optimalRange(20.0f)
 *         .maxRange(100.0f)
 *         .minDamageMultiplier(0.2f)
 *         .magazineType(MagazineType.INTERNAL)
 *         .build();
 * </pre>
 *
 * @param reloadTime          Time in seconds required to reload the firearm.
 * @param rpm                 Rounds per minute (rate of fire).
 * @param projectileVelocity  Velocity of the projectile when fired (in units per second).
 * @param projectileAmount    Number of projectiles fired per shot (e.g., shotgun pellets).
 * @param projectileCapacity  Maximum number of projectiles the firearm can hold.
 * @param spreadBase          Base spread of projectiles (in degrees).
 * @param movementPenalty     Penalty to accuracy when moving (multiplier for spread).
 * @param misfireChance       Chance of the firearm misfiring (0.0 to 1.0).
 * @param jamChance           Chance of the firearm jamming (0.0 to 1.0).
 * @param verticalRecoil      Vertical recoil strength.
 * @param horizontalRecoil    Horizontal recoil strength.
 * @param firearmClass        Historical and mechanical classification of the firearm.
 * @param firearmType         Functional type of the firearm (e.g., handgun, rifle).
 * @param fireMode            Firing mode of the firearm (e.g., semi-automatic, automatic).
 * @param disabled            Whether the HFF mechanics are disabled.
 * @param calibre             The calibre of the ammunition used by the firearm.
 * @param burstRounds         Number of rounds fired in burst mode (0 for non-burst fire).
 * @param optimalRange        Optimal range at which the projectile deals full damage.
 * @param maxRange            Maximum range at which the projectile can deal damage.
 * @param minDamageMultiplier Minimum damage multiplier applied at maximum range.
 * @param magazineType        Type of magazine used by the firearm.
 */
public record FirearmStats(
        float reloadTime,
        float rpm,
        float projectileVelocity,
        int projectileAmount,
        int projectileCapacity,
        float spreadBase,
        float movementPenalty,
        float misfireChance,
        float jamChance,
        float verticalRecoil,
        float horizontalRecoil,
        FirearmClass firearmClass,
        FirearmType firearmType,
        FireMode fireMode,
        boolean disabled,
        String calibre,
        int burstRounds,
        float optimalRange,
        float maxRange,
        float minDamageMultiplier,
        MagazineType magazineType
) {

    /**
     * Creates a new {@link Builder} instance for constructing a {@code FirearmStats} object.
     * This is the preferred way to create a new {@code FirearmStats} object.
     *
     * @return A new builder instance.
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Calculates the cooldown time between shots in seconds.
     * This is derived from the rate of fire (RPM).
     *
     * @return The cooldown time in seconds.
     */
    public float getCooldown() {
        return 60.0f / rpm;
    }

    /**
     * Creates a new {@link Builder} instance initialized with the values of this {@code FirearmStats} object.
     * This allows for easy modification of existing firearm statistics.
     *
     * @return A new builder instance initialized with the values of this object.
     */
    public Builder toBuilder() {
        return new Builder()
                .reloadTime(this.reloadTime)
                .rpm(this.rpm)
                .projectileVelocity(this.projectileVelocity)
                .projectileAmount(this.projectileAmount)
                .projectileCapacity(this.projectileCapacity)
                .spreadBase(this.spreadBase)
                .movementPenalty(this.movementPenalty)
                .misfireChance(this.misfireChance)
                .jamChance(this.jamChance)
                .verticalRecoil(this.verticalRecoil)
                .horizontalRecoil(this.horizontalRecoil)
                .firearmClass(this.firearmClass)
                .firearmType(this.firearmType)
                .fireMode(this.fireMode)
                .disabled(this.disabled)
                .calibre(this.calibre)
                .burstRounds(this.burstRounds)
                .optimalRange(this.optimalRange)
                .maxRange(this.maxRange)
                .minDamageMultiplier(this.minDamageMultiplier)
                .magazineType(this.magazineType);
    }

    /**
     * The {@code Builder} class provides a fluent interface for constructing {@code FirearmStats} objects.
     * This allows for easy and readable creation of firearm statistics.
     */
    public static final class Builder {

        // Default values for the builder
        private float reloadTime = 1.0f;
        private float rpm = 1.0f;
        private float projectileVelocity = 1.0f;
        private int projectileAmount = 1;
        private int projectileCapacity = 1;
        private float spreadBase = 0.0f;
        private float movementPenalty = 0.0f;
        private float misfireChance = 0.0f;
        private float jamChance = 0.0f;
        private float verticalRecoil = 0.0f;
        private float horizontalRecoil = 0.0f;
        private FirearmClass firearmClass = FirearmClass.OTHER;
        private FirearmType firearmType = FirearmType.OTHER;
        private FireMode fireMode = FireMode.OTHER;
        private boolean disabled = false;
        private String calibre = "default";
        private int burstRounds = 0;
        private float optimalRange = 15.0f;
        private float maxRange = 50.0f;
        private float minDamageMultiplier = 0.2f;
        private MagazineType magazineType = MagazineType.INTERNAL;

        /**
         * Constructs a new builder with default values.
         */
        private Builder() {
        }

        // Builder methods for each field

        /**
         * Sets the time in seconds required to reload the firearm.
         *
         * @param reloadTime The reload time in seconds.
         * @return This builder instance.
         */
        public Builder reloadTime(float reloadTime) {
            this.reloadTime = reloadTime;
            return this;
        }

        /**
         * Sets the rounds per minute (rate of fire).
         *
         * @param rpm The rounds per minute.
         * @return This builder instance.
         */
        public Builder rpm(float rpm) {
            this.rpm = rpm;
            return this;
        }

        /**
         * Sets the velocity of the projectile when fired.
         *
         * @param projectileVelocity The projectile velocity.
         * @return This builder instance.
         */
        public Builder projectileVelocity(float projectileVelocity) {
            this.projectileVelocity = projectileVelocity;
            return this;
        }

        /**
         * Sets the number of projectiles fired per shot.
         *
         * @param projectileAmount The number of projectiles per shot.
         * @return This builder instance.
         */
        public Builder projectileAmount(int projectileAmount) {
            this.projectileAmount = projectileAmount;
            return this;
        }

        /**
         * Sets the maximum number of projectiles the firearm can hold.
         *
         * @param projectileCapacity The maximum projectile capacity.
         * @return This builder instance.
         */
        public Builder projectileCapacity(int projectileCapacity) {
            this.projectileCapacity = projectileCapacity;
            return this;
        }

        /**
         * Sets the base spread of projectiles (in degrees).
         *
         * @param spreadBase The base spread in degrees.
         * @return This builder instance.
         */
        public Builder spreadBase(float spreadBase) {
            this.spreadBase = spreadBase;
            return this;
        }

        /**
         * Sets the penalty to accuracy when moving.
         *
         * @param movementPenalty The movement penalty.
         * @return This builder instance.
         */
        public Builder movementPenalty(float movementPenalty) {
            this.movementPenalty = movementPenalty;
            return this;
        }

        /**
         * Sets the chance of the firearm misfiring.
         *
         * @param misfireChance The misfire chance (0.0 to 1.0).
         * @return This builder instance.
         */
        public Builder misfireChance(float misfireChance) {
            this.misfireChance = misfireChance;
            return this;
        }

        /**
         * Sets the chance of the firearm jamming.
         *
         * @param jamChance The jam chance (0.0 to 1.0).
         * @return This builder instance.
         */
        public Builder jamChance(float jamChance) {
            this.jamChance = jamChance;
            return this;
        }

        /**
         * Sets the vertical recoil strength.
         *
         * @param verticalRecoil The vertical recoil strength.
         * @return This builder instance.
         */
        public Builder verticalRecoil(float verticalRecoil) {
            this.verticalRecoil = verticalRecoil;
            return this;
        }

        /**
         * Sets the horizontal recoil strength.
         *
         * @param horizontalRecoil The horizontal recoil strength.
         * @return This builder instance.
         */
        public Builder horizontalRecoil(float horizontalRecoil) {
            this.horizontalRecoil = horizontalRecoil;
            return this;
        }

        /**
         * Sets the historical and mechanical classification of the firearm.
         *
         * @param firearmClass The firearm class.
         * @return This builder instance.
         */
        public Builder firearmClass(FirearmClass firearmClass) {
            this.firearmClass = firearmClass;
            return this;
        }

        /**
         * Sets the functional type of the firearm.
         *
         * @param firearmType The firearm type.
         * @return This builder instance.
         */
        public Builder firearmType(FirearmType firearmType) {
            this.firearmType = firearmType;
            return this;
        }

        /**
         * Sets the firing mode of the firearm.
         *
         * @param fireMode The fire mode.
         * @return This builder instance.
         */
        public Builder fireMode(FireMode fireMode) {
            this.fireMode = fireMode;
            return this;
        }

        /**
         * Sets whether the HFF mechanics are disabled.
         *
         * @param disabled Whether the mechanics are disabled.
         * @return This builder instance.
         */
        public Builder disabled(boolean disabled) {
            this.disabled = disabled;
            return this;
        }

        /**
         * Sets the calibre of the ammunition used by the firearm.
         *
         * @param calibre The calibre of the ammunition.
         * @return This builder instance.
         */
        public Builder calibre(String calibre) {
            this.calibre = calibre;
            return this;
        }

        /**
         * Sets the number of rounds fired in burst mode.
         *
         * @param burstRounds The number of rounds in burst mode (0 for non-burst fire).
         * @return This builder instance.
         */
        public Builder burstRounds(int burstRounds) {
            this.burstRounds = burstRounds;
            return this;
        }

        /**
         * Sets the optimal range at which the projectile deals full damage.
         *
         * @param optimalRange The optimal range.
         * @return This builder instance.
         */
        public Builder optimalRange(float optimalRange) {
            this.optimalRange = optimalRange;
            return this;
        }

        /**
         * Sets the maximum range at which the projectile can deal damage.
         *
         * @param maxRange The maximum range.
         * @return This builder instance.
         */
        public Builder maxRange(float maxRange) {
            this.maxRange = maxRange;
            return this;
        }

        /**
         * Sets the minimum damage multiplier applied at maximum range.
         *
         * @param minDamageMultiplier The minimum damage multiplier.
         * @return This builder instance.
         */
        public Builder minDamageMultiplier(float minDamageMultiplier) {
            this.minDamageMultiplier = minDamageMultiplier;
            return this;
        }

        /**
         * Sets the type of magazine used by the firearm.
         *
         * @param magazineType The magazine type.
         * @return This builder instance.
         */
        public Builder magazineType(MagazineType magazineType) {
            this.magazineType = magazineType;
            return this;
        }

        /**
         * Builds a new {@code FirearmStats} object with the values set in this builder.
         * This method finalizes the construction of the firearm statistics.
         *
         * @return A new {@code FirearmStats} object.
         */
        public FirearmStats build() {
            return new FirearmStats(
                    reloadTime,
                    rpm,
                    projectileVelocity,
                    projectileAmount,
                    projectileCapacity,
                    spreadBase,
                    movementPenalty,
                    misfireChance,
                    jamChance,
                    verticalRecoil,
                    horizontalRecoil,
                    firearmClass,
                    firearmType,
                    fireMode,
                    disabled,
                    calibre,
                    burstRounds,
                    optimalRange,
                    maxRange,
                    minDamageMultiplier,
                    magazineType);
        }
    }
}
