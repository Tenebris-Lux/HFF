package lucis.lux.hff.util;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Component;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.server.core.entity.InteractionContext;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import lucis.lux.hff.HFF;
import lucis.lux.hff.components.AmmoComponent;
import lucis.lux.hff.components.FirearmStatsComponent;

import java.util.UUID;

public class EnsureEntity {
    public static <K extends Component<EntityStore>> ComponentRefResult<K> get(InteractionContext interactionContext, Class<K> type, ComponentType<EntityStore, K> componentType, String itemId) {

        CommandBuffer<EntityStore> commandBuffer = interactionContext.getCommandBuffer();
        Ref<EntityStore> playerRef = interactionContext.getEntity();
        Player player = commandBuffer.getComponent(playerRef, Player.getComponentType());
        ItemStack item = interactionContext.getHeldItem();

        RefKeeper keeper = commandBuffer.getResource(HFF.getRefKeeper());

        UUID uuid = item.getFromMetadataOrNull("HFF_METADATA", Codec.UUID_BINARY);
        Ref<EntityStore> entityRef = null;
        K component = null;
        boolean isDisabled = false;

        if (uuid != null) {
            try {
                entityRef = keeper.getRef(uuid);

                if (entityRef != null && entityRef.isValid()) {
                    component = commandBuffer.getComponent(entityRef, componentType);
                    if (component != null) {
                        if (type == FirearmStatsComponent.class) {
                            FirearmStatsComponent firearmStats = (FirearmStatsComponent) component;
                            isDisabled = ((FirearmStatsComponent) component).isDisabled();
                        }
                        return new ComponentRefResult<>(component, entityRef, false, isDisabled);
                    }
                }
            } catch (Exception e) {
                HFF.get().getLogger().atSevere().log("Error when getting component: " + e.getMessage());
            }
        }

        component = createDefaultComponent(type);
        ItemStack updatedItem = ComponentAttacher.attachComponent(playerRef, commandBuffer, item, componentType, type, itemId);
        interactionContext.setHeldItem(updatedItem);
        // I have to forcefully replace the gun, setHeldItem does nothing on its own
        player.getInventory().getHotbar().replaceItemStackInSlot(interactionContext.getHeldItemSlot(), item, interactionContext.getHeldItem());

        return new ComponentRefResult<>(component, null, true, false);

    }

    @SuppressWarnings("unchecked")
    private static <K extends Component<EntityStore>> K createDefaultComponent(Class<K> type) {
        try {
            if (type == FirearmStatsComponent.class) {
                return (K) new FirearmStatsComponent();
            } else if (type == AmmoComponent.class) {
                return (K) new AmmoComponent();
            }
        } catch (Exception e) {
            HFF.get().getLogger().atSevere().log("Error creating the standard component: " + e.getMessage());
        }
        return null;
    }
}
