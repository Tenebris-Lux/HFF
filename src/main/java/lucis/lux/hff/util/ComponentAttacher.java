package lucis.lux.hff.util;


import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.component.*;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import lucis.lux.hff.HFF;
import lucis.lux.hff.resources.RefKeeper;

import java.util.UUID;

/**
 * The {@code ComponentAttacher} class provides a generic way to attach any component to an {@link ItemStack}.
 * It creates a unique entity for each item and stores the reference using a {@link UUID} in the item's metadata.
 * This class is designed to be used in the HFF (Hytale Firearm Framework) plugin.
 *
 * <p>This utility class performs the following tasks:</p>
 * <ul>
 *     <li>Loads component statistics from resources using the {@link ComponentStatsLoader}.</li>
 *     <li>Creates a new entity for the item if it does not already exist.</li>
 *     <li>Attaches the specified component to the entity.</li>
 *     <li>Stores the entity reference in the item's metadata for future retrieval.</li>
 * </ul>
 *
 * <p>This class is typically used when initializing items that require custom components,
 * such as firearms, ammunition, or other interactive items.</p>
 */
public class ComponentAttacher {
    /**
     * Attaches a component of type {@code T} to the given {@link ItemStack}.
     * If the component does not exist, a new entity with the component is created.
     * If the entity already exists, the component is added to it.
     *
     * @param playerRef      The reference to the player's entity store.
     * @param commandBuffer  The command buffer used to add the entity and components.
     * @param itemStack      The item stack to which the component will be attached.
     * @param componentType  The type of the component to attach.
     * @param componentClass The class of the component to attach.
     * @param itemId         The ID of the item, used to load component statistics.
     * @param <T>            The type of the component.
     * @return The updated {@link ItemStack} with the component attached.
     */
    public static <T extends Component<EntityStore>> ItemStack attachComponent(Ref<EntityStore> playerRef, CommandBuffer<EntityStore> commandBuffer, ItemStack itemStack, ComponentType<EntityStore, T> componentType, Class<T> componentClass, String itemId) {

        // Load component statistics from resources
        ComponentStatsLoader<T> loader = new ComponentStatsLoader<>(componentClass);
        T component = loader.loadStatsFromResource(itemId);
        RefKeeper keeper = commandBuffer.getResource(HFF.getRefKeeper());

        // If loading fails, create a default component.
        if (component == null) {
            try {
                component = componentClass.getDeclaredConstructor().newInstance();
            } catch (Exception e) {
                HFF.get().getLogger().atSevere().log("Error creating default component: " + e.getMessage());
                return itemStack;
            }
        }

        // Retrieve the UUID from the item's metadata
        UUID uuid = itemStack.getFromMetadataOrNull("HFF_METADATA", Codec.UUID_BINARY);
        Ref<EntityStore> entityRef = null;

        // If the UUID exits, try to retrieve the entity reference
        if (uuid != null) {
            entityRef = keeper.getRef(uuid);
            if (entityRef != null && entityRef.isValid()) {
                // If the component already exits, return the item stack as-is
                if (commandBuffer.getComponent(entityRef, componentType) != null) {
                    return itemStack;
                }
            }
        }

        // If the entity reference is invalid or does not exist, create a new entity
        if (entityRef == null || !entityRef.isValid()) {
            Holder<EntityStore> entityHolder = playerRef.getStore().getRegistry().newHolder();
            entityHolder.addComponent(componentType, component);
            entityRef = commandBuffer.addEntity(entityHolder, AddReason.LOAD);

            // generate a new UUID and store it in the item's metadata
            uuid = UUID.randomUUID();
            itemStack = itemStack.withMetadata("HFF_METADATA", Codec.UUID_BINARY, uuid);
            keeper.setRef(uuid, entityRef);

            if (ConfigManager.isDebugMode()) {
                HFF.get().getLogger().atInfo().log("Created new entity " + entityRef + " with component: " + component.getClass());
            }
        } else {
            // If the entity exists, but the component is missing, add the component to the entity
            commandBuffer.addComponent(entityRef, componentType, component);
        }

        return itemStack;
    }
}
