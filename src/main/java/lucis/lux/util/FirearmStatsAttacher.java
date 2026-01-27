package lucis.lux.util;


import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.component.AddReason;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Holder;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import lucis.lux.components.FirearmStatsComponent;
import lucis.lux.core.HFF;

import java.util.UUID;

/**
 * The {@code FirearmStatsAttacher} class is responsible for attaching a {@link FirearmStatsComponent}
 * to an {@link ItemStack} in the game. It creates a unique entity for each firearm item and stores
 * the reference to this entity using a {@link UUID} in the item's metadata. This ensures that eacg
 * firearm instance has its own unique statistics and state.
 */
public class FirearmStatsAttacher {
    /**
     * Attaches a {@link FirearmStatsComponent} to the given {@link ItemStack}.
     * This method loads the firearm statistics from the resource associated with the item ID,
     * creates a new entity for the firearm, and stores the reference to this entity in the item's metadata.
     *
     * @param playerRef     The reference to the player's entity store.
     * @param commandBuffer The command buffer used to add the entity and components.
     * @param itemStack     The item stack to which the firearm statistics will be attached.
     * @return The updated {@link ItemStack} with the firearm statistics component attached.
     */
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
