package lucis.lux.hff.util;

import lucis.lux.hff.data.AttachmentData;
import lucis.lux.hff.data.AttachmentRegistry;
import lucis.lux.hff.data.FirearmState;
import lucis.lux.hff.data.FirearmStats;

public class StatCalculator {
    public static FirearmStats getModifiedStats(FirearmStats baseStats, FirearmState state) {
        if (state.getAttachments().isEmpty()) {
            return baseStats;
        }

        float totalRecoilMult = 1.0f;
        float totalSpreadMult = 1.0f;
        float totalVelocityMult = 1.0f;
        float totalReloadMult = 1.0f;
        float totalRpmMult = 1.0f;
        int totalExtraCapacity = 0;

        for (String attachmentId : state.getAttachments().values()) {
            AttachmentData att = AttachmentRegistry.get(attachmentId);
            if (att != null) {
                totalRecoilMult *= att.recoilMultiplier();
                totalSpreadMult *= att.spreadMultiplier();
                totalVelocityMult *= att.velocityMultiplier();
                totalReloadMult *= att.reloadTimeMultiplier();
                totalRpmMult *= att.rpmMultiplier();
                totalExtraCapacity += att.extraMagazineCapacity();
            }
        }

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
