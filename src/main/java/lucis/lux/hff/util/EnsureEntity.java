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
import lucis.lux.hff.resources.RefKeeper;

import java.util.UUID;

/**
 * The {@code EnsureEntity} utility class provides methods to ensure that an entity and its associated components
 * (such as {@link FirearmStatsComponent} or {@link AmmoComponent}) are properly initialized and available.
 * This class is designed to handle the creation end retrieval of components for items, particularly firearms,
 * in the HFF (Hytale Firearm Plugin) plugin.
 *
 * <p>This class performs the following tasks:</p>
 * <ul>
 *     <li>Retrieves or creates an entity reference for an item, if it does not already exist.</li>
 *     <li>Ensures that the required component (e.g., firearm stats, ammo) is attached to the item.</li>
 *     <li>Handles the case where the component is missing or invalid by creating a new one.</li>
 *     <li>Updates the item in the player's inventory to reflect the attached component.</li>
 * </ul>
 *
 * <p>This utility is typically used in interactions such as shooting or reloading, where the presence of a valid
 * component is required to proceed.</p>
 *
 * @see FirearmStatsComponent
 * @see AmmoComponent
 * @see ComponentRefResult
 * @see ComponentAttacher
 */
public class EnsureEntity {
    /**
     * Retrieves or creates a component of the specified type for the item held by the player in the given interaction context.
     * If the component does not exist or is invalid, a new component is created and attached to the item.
     *
     * @param interactionContext The interaction context, providing access to the player, held item, and command buffer.
     * @param type               The class of the component to retrieve or create.
     * @param componentType      The component type, used to register and retrieve the component.
     * @param itemId             The ID of the item, used to attach the component.
     * @param <K>                The type of the component.
     * @return A {@link ComponentRefResult} containing the component, its entity reference, and flags indicating if it was newly created or disabled.
     */
    public static <K extends Component<EntityStore>> ComponentRefResult<K> get(InteractionContext interactionContext, Class<K> type, ComponentType<EntityStore, K> componentType, String itemId) {

        CommandBuffer<EntityStore> commandBuffer = interactionContext.getCommandBuffer();
        Ref<EntityStore> playerRef = interactionContext.getEntity();
        Player player = commandBuffer.getComponent(playerRef, Player.getComponentType());
        ItemStack item = interactionContext.getHeldItem();

        RefKeeper keeper = commandBuffer.getResource(HFF.getRefKeeper());

        // Retrieve the UUID associated with the item
        UUID uuid = item.getFromMetadataOrNull("HFF_METADATA", Codec.UUID_BINARY);
        Ref<EntityStore> entityRef = null;
        K component = null;
        boolean isDisabled = false;

        // If the UUID exists, try to retrieve the component
        if (uuid != null) {
            try {
                entityRef = keeper.getRef(uuid);

                if (entityRef != null && entityRef.isValid()) {
                    component = commandBuffer.getComponent(entityRef, componentType);
                    if (component != null) {
                        if (type == FirearmStatsComponent.class) {
                            FirearmStatsComponent firearmStats = (FirearmStatsComponent) component;
                            isDisabled = firearmStats.isDisabled();
                            firearmStats.setPlayerRef(playerRef);
                        }
                        return new ComponentRefResult<>(component, entityRef, false, isDisabled);
                    }
                }
            } catch (Exception e) {
                HFF.get().getLogger().atSevere().log("Error when getting component: " + e.getMessage());
            }
        }

        // If the component is missing or invalid, create a new one
        component = createDefaultComponent(type);
        ItemStack updatedItem = ComponentAttacher.attachComponent(playerRef, commandBuffer, item, componentType, type, itemId);
        interactionContext.setHeldItem(updatedItem);

        // Forcefully replace the item in the hotbar slot
        player.getInventory().getHotbar().replaceItemStackInSlot(interactionContext.getHeldItemSlot(), item, interactionContext.getHeldItem());

        return new ComponentRefResult<>(component, null, true, false);

    }

    /**
     * Creates a new default component of the specified type.
     * This method is used when a component is missing or invalid and needs to be created from scratch.
     *
     * @param type The class of the component to create.
     * @param <K>  The type of the component.
     * @return A new instance of the component, or {@code null} if the component could not be created.
     */
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
