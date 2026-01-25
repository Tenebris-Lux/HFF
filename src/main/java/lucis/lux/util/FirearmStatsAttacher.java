package lucis.lux.util;


import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.component.AddReason;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Holder;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.math.util.HashUtil;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import lucis.lux.HFF;
import lucis.lux.components.FirearmStatsComponent;

import java.util.UUID;

public class FirearmStatsAttacher {
    public static ItemStack attachFirearmStats(Ref<EntityStore> playerRef, CommandBuffer<EntityStore> commandBuffer, ItemStack itemStack) {
        FirearmStatsComponent stats = FirearmStatsLoader.loadStatsFromResource(itemStack.getItemId());

        Holder<EntityStore> itemHolder = playerRef.getStore().getRegistry().newHolder();
        itemHolder.addComponent(HFF.get().getFirearmStatsComponentType(), stats);

        Ref<EntityStore> itemRef = commandBuffer.addEntity(itemHolder, AddReason.LOAD);

        UUID uuid = UUID.randomUUID();

        itemStack = itemStack.withMetadata("HFF_FIREARM_STATS_COMPONENT", Codec.UUID_BINARY, uuid);
        commandBuffer.getResource(HFF.getRefKeeper()).setRef(uuid, itemRef);

        HFF.get().getLogger().atInfo().log("Attached FirearmStats component to entity: " + itemRef);
        return itemStack;
    }
}
