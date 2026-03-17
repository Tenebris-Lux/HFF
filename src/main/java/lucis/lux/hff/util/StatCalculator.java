package lucis.lux.hff.util;

import lucis.lux.hff.data.AttachmentData;
import lucis.lux.hff.data.FirearmState;
import lucis.lux.hff.data.FirearmStats;
import lucis.lux.hff.data.registry.Registries;

/**
 * A utility class responsible for combining a firearm's intrinsic base statistics
 * with the extrinsic modifiers provided by its currently installed attachments.
 */
public class StatCalculator {

    /**
     * Calculates and generates a new, modified {@link FirearmStats} object based on  the
     * weapons active attachments.
     *
     * <p>The calculation follows a cumulative multiplication model for most modifiers.
     * For any given floating-point stat, the formula applied is:
     * $FinalStat = BaseStat \times \prod_{i=1}^{n} Modifier_i$
     * </p>
     *
     * <p>Flat bonuses (such as extra magazine capacity) are calculated using cumulative addition.</p>
     *
     * @param baseStats The immutable base statistics of the firearm.
     * @param state     The current runtime state containing the active attachments.
     * @return A new build {@link FirearmStats} instance reflecting all active modifiers,
     * or the original {@code baseStats} if no attachment are installed.
     */
    public static FirearmStats getModifiedStats(FirearmStats baseStats, FirearmState state) {
        if (state.getAttachments().isEmpty()) {
            return baseStats;
        }

        // Initialize multiplier accumulators
        float totalRecoilMult = 1.0f;
        float totalSpreadMult = 1.0f;
        float totalVelocityMult = 1.0f;
        float totalReloadMult = 1.0f;
        float totalRpmMult = 1.0f;

        // Initialize flat stat accumulators
        int totalExtraCapacity = 0;

        // Aggregate modifiers from all active attachments
        for (String attachmentId : state.getAttachments().values()) {
            AttachmentData att = Registries.ATTACHMENT_DATA.get(attachmentId);
            if (att != null) {
                totalRecoilMult *= att.recoilMultiplier();
                totalSpreadMult *= att.spreadMultiplier();
                totalVelocityMult *= att.velocityMultiplier();
                totalReloadMult *= att.reloadTimeMultiplier();
                totalRpmMult *= att.rpmMultiplier();
                totalExtraCapacity += att.extraMagazineCapacity();
            }
        }

        // Build and return a fresh, immutable stats object
        return baseStats.toBuilder()
                .verticalRecoil(baseStats.verticalRecoil() * totalRecoilMult)
                .horizontalRecoil(baseStats.horizontalRecoil() * totalRecoilMult)
                .spreadBase(baseStats.spreadBase() * totalSpreadMult)
                .projectileVelocity(baseStats.projectileVelocity() * totalVelocityMult)
                .reloadTime(baseStats.reloadTime() * totalReloadMult)
                .rpm(baseStats.rpm() * totalRpmMult)
                .projectileCapacity(baseStats.projectileCapacity() + totalExtraCapacity)
                .build();
    }
}