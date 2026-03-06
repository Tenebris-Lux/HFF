package lucis.lux.hff.data;

import lucis.lux.hff.enums.AttachmentType;

public record AttachmentData(
        AttachmentType type,
        float recoilMultiplier,
        float spreadMultiplier,
        float velocityMultiplier,
        float reloadTimeMultiplier,
        float rpmMultiplier,
        int extraMagazineCapacity
) {
    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private AttachmentType type = AttachmentType.OPTIC;
        private float recoilMultiplier = 1.0f;
        private float spreadMultiplier = 1.0f;
        private float velocityMultiplier = 1.0f;
        private float reloadTimeMultiplier = 1.0f;
        private float rpmMultiplier = 1.0f;
        private int extraMagazineCapacity = 0;

        private Builder() {
        }

        public Builder type(AttachmentType type) {
            this.type = type;
            return this;
        }

        public Builder recoilMultiplier(float recoilMultiplier) {
            this.recoilMultiplier = recoilMultiplier;
            return this;
        }

        public Builder spreadMultiplier(float spreadMultiplier) {
            this.spreadMultiplier = spreadMultiplier;
            return this;
        }

        public Builder velocityMultiplier(float velocityMultiplier) {
            this.velocityMultiplier = velocityMultiplier;
            return this;
        }

        public Builder reloadTimeMultiplier(float reloadTimeMultiplier) {
            this.reloadTimeMultiplier = reloadTimeMultiplier;
            return this;
        }

        public Builder rpmMultiplier(float rpmMultiplier) {
            this.rpmMultiplier = rpmMultiplier;
            return this;
        }

        public Builder extraMagazineCapacity(int extraMagazineCapacity) {
            this.extraMagazineCapacity = extraMagazineCapacity;
            return this;
        }

        public AttachmentData build() {
            return new AttachmentData(type, recoilMultiplier, spreadMultiplier, velocityMultiplier, reloadTimeMultiplier, rpmMultiplier, extraMagazineCapacity);
        }
    }
}
