package lucis.lux.hff.util;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Component;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.server.core.entity.InteractionContext;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import lucis.lux.hff.HFF;
import lucis.lux.hff.components.FirearmStatsComponent;

import java.util.UUID;

public class EnsureEntity {
    public static <K extends Component<EntityStore>> ComponentRefResult<K> get(InteractionContext interactionContext, Class<K> type) {

        CommandBuffer<EntityStore> commandBuffer = interactionContext.getCommandBuffer();
        Ref<EntityStore> ref = interactionContext.getEntity();
        Player player = commandBuffer.getComponent(ref, Player.getComponentType());
        ItemStack item = interactionContext.getHeldItem();

        RefKeeper keeper = commandBuffer.getResource(HFF.getRefKeeper());

        UUID uuid = item.getFromMetadataOrNull("HFF_FIREARM_STATS_COMPONENT", Codec.UUID_BINARY);

        if (uuid != null) {
            Ref<EntityStore> itemEntityRef = keeper.getRef(uuid);

            if (itemEntityRef != null && itemEntityRef.isValid()) {
                try {
                    if (type == FirearmStatsComponent.class) {
                        K component = (K) commandBuffer.getComponent(itemEntityRef, HFF.get().getFirearmStatsComponentType());
                        FirearmStatsComponent firearmStats = (FirearmStatsComponent) component;
                        return new ComponentRefResult<>(component, itemEntityRef, false, firearmStats.isDisabled());
                    }
                } catch (Exception e) {
                    HFF.get().getLogger().atSevere().log("Error when getting component: " + e.getMessage());
                }
            }
        }


        interactionContext.setHeldItem(FirearmStatsAttacher.attachFirearmStats(ref, commandBuffer, item));
        // I have to forcefully replace the gun, setHeldItem does nothing
        player.getInventory().getHotbar().replaceItemStackInSlot(interactionContext.getHeldItemSlot(), item, interactionContext.getHeldItem());

        return new ComponentRefResult<>(null, null, true, false);

    }
}
