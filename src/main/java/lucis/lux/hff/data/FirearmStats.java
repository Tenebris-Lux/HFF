package lucis.lux.hff.data;

import lucis.lux.hff.enums.FireMode;
import lucis.lux.hff.enums.FirearmClass;
import lucis.lux.hff.enums.FirearmType;

/**
 * The {@code FirearmStats} record represents the statistics and properties of a firearm.
 * This record stores all relevant data for firearm behaviour, such as rate of fire, projectile velocity,
 * recoil, and supported ammunition types.
 *
 * <p>This record is immutable and provides a convenient way to store and retrieve firearm statistics.
 * It is typically used in conjunction with firearm components to define the properties and behaviour
 * of firearms in the HFF (Hytale Firearm Framework) plugin.</p>
 *
 * <p>Example usage:</p>
 * <pre>
 *      FirearmStats stats = FirearmStats.builder()
 *          .reloadTime(2.5f)
 *          .rpm(600.0f)
 *          .projectileVelocity(300.0f)
 *          .projectileAmount(1)
 *          .projectileCapacity(30)
 *          .spreadBase(2.0f)
 *          .movementPenalty(0.5f)
 *          .misfireChance(0.01f)
 *          .jamChance(0.005f)
 *          .verticalRecoil(0.5f)
 *          .horizontalRecoil(0.1f)
 *          .firearmClass(FirearmClass.PISTOL)
 *          .firearmType(FirearmType.HANDGUN)
 *          .fireMode(FireMode.SEMI_AUTOMATIC)
 *          .disabled(false)
 *          .calibre("9mm")
 *          .build();
 * </pre>
 *
 * @param reloadTime         Time in seconds required to reload the firearm.
 * @param rpm                Rounds per minute (rate of fire).
 * @param projectileVelocity Velocity of the projectile when fired.
 * @param projectileAmount   Number of projectiles fired per shot (e.g., shotgun pellets).
 * @param projectileCapacity Maximum number of projectiles the firearm can hold.
 * @param spreadBase         Base spread of projectiles (in degrees).
 * @param movementPenalty    Penalty to accuracy when moving.
 * @param misfireChance      Chance of the firearm misfiring (0.0 to 1.0).
 * @param jamChance          Chance of the firearm jamming (0.0 to 1.0).
 * @param verticalRecoil     Vertical recoil strength.
 * @param horizontalRecoil   Horizontal recoil strength.
 * @param firearmClass       Historical and mechanical classification of the firearm.
 * @param firearmType        Functional type of the firearm (e.g., handgun, rifle).
 * @param fireMode           Firing mode of the firearm (e.g., semi-automatic, automatic).
 * @param disabled           Whether the HFF mechanics are disabled.
 * @param calibre            The calibre of the ammunition used by the firearm.
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
        String calibre
) {

    /**
     * Creates a new {@link Builder} instance for constructing a {@code FirearmStats} object.
     *
     * @return A new builder instance.
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Calculates the cooldown time between shots in seconds.
     *
     * @return The cooldown time in seconds.
     */
    public float getCooldown() {
        return 60.0f / rpm;
    }

    /**
     * Creates a new {@link Builder} instance initialized with the values of this {@code FirearmStats} object.
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
                .calibre(this.calibre);
    }

    /**
     * The {@code Builder} class provides a fluent interface for constructing {@code FirearmStats} objects.
     */
    public static final class Builder {

        /**
         * Time in seconds required to reload the firearm.
         */
        private float reloadTime = 1.0f;
        /**
         * Rounds per minute (rate of fire).
         */
        private float rpm = 1.0f;
        /**
         * Velocity of the projectile when fired.
         */
        private float projectileVelocity = 1.0f;
        /**
         * Number of projectiles fired per shot (e.g., shotgun pellets).
         */
        private int projectileAmount = 1;
        /**
         * Maximum number of projectiles the firearm can hold.
         */
        private int projectileCapacity = 1;
        /**
         * Base spread of projectiles (in degrees).
         */
        private float spreadBase = 0.0f;
        /**
         * Penalty to accuracy when moving.
         */
        private float movementPenalty = 0.0f;
        /**
         * Chance of the firearm misfiring (0.0 to 1.0).
         */
        private float misfireChance = 0.0f;
        /**
         * Chance of the firearm jamming (0.0 to 1.0).
         */
        private float jamChance = 0.0f;
        /**
         * Vertical recoil strength.
         */
        private float verticalRecoil = 0.0f;
        /**
         * Horizontal recoil strength.
         */
        private float horizontalRecoil = 0.0f;
        /**
         * Historical and mechanical classification of the firearm.
         */
        private FirearmClass firearmClass = FirearmClass.OTHER;
        /**
         * Functional type of the firearm (e.g., handgun, rifle).
         */
        private FirearmType firearmType = FirearmType.OTHER;
        /**
         * Firing mode of the firearm (e.g., semi-automatic, automatic).
         */
        private FireMode fireMode = FireMode.OTHER;
        /**
         * Whether the HFF mechanics are disabled.
         */
        private boolean disabled = false;

        /**
         * The calibre of the ammunition used by the firearm.
         */
        private String calibre = "default";

        /**
         * Constructs a new builder with default values.
         */
        private Builder() {
        }

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
         * Builds a new {@code FirearmStats} object with the values set in this builder.
         *
         * @return A new {@code FirearmStats} object.
         */
        public FirearmStats build() {
            return new FirearmStats(reloadTime, rpm, projectileVelocity, projectileAmount, projectileCapacity, spreadBase, movementPenalty, misfireChance, jamChance, verticalRecoil, horizontalRecoil, firearmClass, firearmType, fireMode, disabled, calibre);
        }
    }
}
