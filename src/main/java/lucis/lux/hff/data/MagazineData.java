package lucis.lux.hff.data;

/**
 * The {@code MagazineData} record represents the data and properties of a firearm magazine.
 * This record stores the calibre of the ammunition the magazine is designed for and its capacity.
 *
 * <p>This record is immutable and provides a convenient way to store and retrieve magazine data.
 * It is typically used in conjunction with firearm components to define the properties and behaviour
 * of magazines in the HFF (Hytale Firearm Framework) plugin.</p>
 *
 * <p>Example usage:</p>
 * <pre>
 *     // Create a new MagazineData object using the builder
 *     MagazineData magazine = MagazineData.builder()
 *         .calibre("9mm")
 *         .capacity(30)
 *         .build();
 *
 *     // Create a new MagazineData object using the record constructor
 *     MagazineData magazine = new MagazineData("9mm", 30);
 * </pre>
 */
public record MagazineData(String calibre, int capacity) {

    /**
     * Creates a new {@link Builder} instance for constructing a {@code MagazineData} object.
     * This is the preferred way to create a new {@code MagazineData} object.
     *
     * @return A new builder instance.
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Creates a new {@link Builder} instance initialized with the values of this {@code MagazineData} object.
     * This allows for easy modification of existing magazine data.
     *
     * @return A new builder instance initialized with the values of this object.
     */
    public Builder toBuilder() {
        return new Builder()
                .calibre(calibre)
                .capacity(capacity);
    }

    /**
     * The {@code Builder} class provides a fluent interface for constructing {@code MagazineData} objects.
     * This allows for easy and readable creation of magazine data.
     */
    public static final class Builder {

        /**
         * The calibre of the ammunition the magazine is designed for.
         */
        private String calibre = "default";

        /**
         * The capacity of the magazine.
         */
        private int capacity = 1;

        /**
         * Constructs a new builder with default values.
         */
        private Builder() {
        }

        /**
         * Sets the calibre of the ammunition the magazine is designed for.
         *
         * @param calibre The calibre of the ammunition.
         * @return This builder instance.
         */
        public Builder calibre(String calibre) {
            this.calibre = calibre;
            return this;
        }

        /**
         * Sets the capacity of the magazine.
         *
         * @param capacity The capacity of the magazine.
         * @return This builder instance.
         */
        public Builder capacity(int capacity) {
            this.capacity = capacity;
            return this;
        }

        /**
         * Builds a new {@code MagazineData} object with the values set in this builder.
         * This method finalizes the construction of the magazine data.
         *
         * @return A new {@code MagazineData} object.
         */
        public MagazineData build() {
            return new MagazineData(calibre, capacity);
        }
    }
}
