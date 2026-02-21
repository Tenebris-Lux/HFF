package lucis.lux.hff.data;

/**
 * The {@code AmmoData} record represents the data associated with the type of ammunition.
 * This record stores the calibre, projectile ID, and damage value of the ammunition.
 *
 * <p>This record is immutable and provides a convenient way to store and retrieve
 * ammunition data. It is typically used in conjunction with firearm components to
 * define the properties of ammunition used by firearms.</p>
 *
 * <p>Example usage:</p>
 * <pre>
 *     AmmoData ammoData = AmmoData.builder()
 *          .calibre("9mm")
 *          .projectileId("9mm_projectile")
 *          .damage(5.0f)
 *          .build();
 * </pre>
 *
 * @param calibre      The calibre of the ammunition.
 * @param projectileId The ID of the projectile associated with the ammunition.
 * @param damage       The damage value of the ammunition.
 */
public record AmmoData(String calibre, String projectileId, float damage) {

    /**
     * Creates a new {@link Builder} instance for constructing an {@code AmmoData} object.
     *
     * @return A new builder instance.
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Creates a new {@link Builder} instance initialized with the values of this {@code AmmoData} object.
     *
     * @return A new builder instance initialized with the values of this object.
     */
    public Builder toBuilder() {
        return new Builder()
                .calibre(calibre)
                .damage(damage)
                .projectileId(projectileId);
    }

    /**
     * The {@code Builder} class provides a fluent interface for constructing {@code AmmoData} objects.
     */
    public static final class Builder {

        /**
         * The calibre of the ammunition. Defaults to "default".
         */
        private String calibre = "default";
        /**
         * The ID of the projectile associated with the ammunition. Defaults to "Example_Projectile".
         */
        private String projectileId = "Example_Projectile";
        /**
         * The damage value of the ammunition. Defaults to 1.0.
         */
        private float damage = 1.0f;

        /**
         * Constructs a new builder with default values.
         */
        private Builder() {
        }

        /**
         * Sets the calibre of the ammunition.
         *
         * @param caliber The calibre of the ammunition.
         * @return This builder instance.
         */
        public Builder calibre(String caliber) {
            this.calibre = caliber;
            return this;
        }

        /**
         * Sets the ID of the projectile associated with the ammunition.
         *
         * @param projectileId The ID of the projectile.
         * @return This builder instance.
         */
        public Builder projectileId(String projectileId) {
            this.projectileId = projectileId;
            return this;
        }

        /**
         * Sets the damage value of the ammunition.
         *
         * @param damage The damage value.
         * @return This builder instance.
         */
        public Builder damage(float damage) {
            this.damage = damage;
            return this;
        }

        /**
         * Builds a new {@code AmmoData} object with the values set in this builder.
         *
         * @return A new {@code AmmoData} object.
         */
        public AmmoData build() {
            return new AmmoData(calibre, projectileId, damage);
        }
    }
}
