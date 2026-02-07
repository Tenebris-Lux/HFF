package lucis.lux.hff.util;


import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.component.*;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import lucis.lux.hff.HFF;

import java.util.UUID;

/**
 * The {@code ComponentAttacher} class provides a generic way to attach any component to an {@link ItemStack}.
 * It creates a unique entity for each item and stores the reference using a {@link UUID} in the item's metadata.
 */
public class ComponentAttacher {
    /**
     * Attaches a component of type {@code T} to the given {@link ItemStack}.
     * If the component does not exist, a new entity with the component is created.
     *
     * @param playerRef     The reference to the player's entity store.
     * @param commandBuffer The command buffer used to add the entity and components.
     * @param itemStack     The item stack to which the component will be attached.
     * @param componentType The type of the component to attach.
     * @param <T>           The type of the component.
     * @return The updated {@link ItemStack} with the component attached.
     */
    public static <T extends Component<EntityStore>> ItemStack attachComponent(Ref<EntityStore> playerRef, CommandBuffer<EntityStore> commandBuffer, ItemStack itemStack, ComponentType<EntityStore, T> componentType, Class<T> componentClass, String itemId) {

        ComponentStatsLoader<T> loader = new ComponentStatsLoader<>(componentClass);
        T component = loader.loadStatsFromResource(itemId);
        RefKeeper keeper = commandBuffer.getResource(HFF.getRefKeeper());

        if (component == null) {
            try {
                component = componentClass.getDeclaredConstructor().newInstance();
            } catch (Exception e) {
                HFF.get().getLogger().atSevere().log("Error creating default component: " + e.getMessage());
                return itemStack;
            }
        }

        UUID uuid = itemStack.getFromMetadataOrNull("HFF_METADATA", Codec.UUID_BINARY);
        Ref<EntityStore> entityRef = null;

        if (uuid != null) {
            entityRef = keeper.getRef(uuid);
            if (entityRef != null && entityRef.isValid()) {
                if (commandBuffer.getComponent(entityRef, componentType) != null) {
                    return itemStack;
                }
            }
        }

        if (entityRef == null || !entityRef.isValid()) {
            Holder<EntityStore> entityHolder = playerRef.getStore().getRegistry().newHolder();
            entityHolder.addComponent(componentType, component);
            entityRef = commandBuffer.addEntity(entityHolder, AddReason.LOAD);

            uuid = UUID.randomUUID();
            itemStack = itemStack.withMetadata("HFF_METADATA", Codec.UUID_BINARY, uuid);
            keeper.setRef(uuid, entityRef);

            if (ConfigManager.isDebugMode()) {
                HFF.get().getLogger().atInfo().log("Created new entity " + entityRef + " with component: " + component.getClass());
            }
        } else {    // if the entity exists, but the component is missing
            commandBuffer.addComponent(entityRef, componentType, component);
        }

        return itemStack;
    }
}
