package lucis.lux.util;


import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import lucis.lux.HFF;
import lucis.lux.components.FirearmStatsComponent;

public class FirearmStatsAttacher {
    public static void attachFirearmStats(Ref<EntityStore> entityRef, CommandBuffer<EntityStore> commandBuffer, String itemId) {
        FirearmStatsComponent stats = FirearmStatsLoader.loadStatsFromResource(itemId);

        commandBuffer.addComponent(entityRef, HFF.get().getFirearmStatsComponentType(), stats);

        HFF.get().getLogger().atInfo().log("Attached FirearmStats component to entity: " + itemId);
    }
}
