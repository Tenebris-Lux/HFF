package lucis.lux.hff.listeners;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.server.core.event.events.entity.LivingEntityInventoryChangeEvent;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import lucis.lux.hff.data.FirearmRegistry;

import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The {@code FirearmUuidInitializer} class is responsible for initializing and managing UUIDs
 * for firearms in a player's inventory. This class listens for inventory change events and ensures
 * that each firearm item has a unique UUID stored in its metadata.
 *
 * <p>When an inventory change event is detected, this class:</p>
 * <ul>
 *     <li>Parses the event transaction string to determine the affected slot and item ID.</li>
 *     <li>Checks if the item is a registered firearm using the {@link FirearmRegistry}.</li>
 *     <li>If the item is a firearm and does not already have a UUID, a new UUID is generated and stored in the item's metadata.</li>
 * </ul>
 *
 * <p>This class is typically used to ensure that each firearm instance can be uniquely identified
 * and tracked throughout the game, which is essential for managing firearm stats, cooldowns, and other properties.</p>
 */
public class FirearmUuidInitializer {

    /**
     * Called when an inventory change event is detected. This method initializes a UUID for any firearm
     * items that do not already have one.
     *
     * <p>The following steps are performed:</p>
     * <ol>
     *     <li>Extracts the slot number from the event transaction string using a regular expression.</li>
     *     <li>Extracts the item ID from the event transaction string using a regular expression.</li>
     *     <li>Checks if the item is a registered firearm using the {@link FirearmRegistry}.</li>
     *     <li>If the item is a firearm and lacks a UUID, generates a new UUID and stores it in the item's metadata.</li>
     * </ol>
     *
     * @param event The inventory change event.
     */
    public static void onInventoryChanged(LivingEntityInventoryChangeEvent event) {
        String transactionString = event.getTransaction().toString();

        // Extracts the slot number from the transaction string
        Pattern slotPattern = Pattern.compile("slot=(\\d+)");
        Matcher slotMatcher = slotPattern.matcher(transactionString);

        short slot = -1;
        if (slotMatcher.find()) {
            slot = Short.parseShort(slotMatcher.group(1));
        }

        // Extracts the item ID from the transaction string
        Pattern idPattern = Pattern.compile("slotAfter=ItemStack\\{itemId=(\\w+)");
        Matcher idMatcher = idPattern.matcher(transactionString);

        String id = null;
        if (idMatcher.find()) {
            id = idMatcher.group(1);
        }

        // Check if the slot and ID are valid and if the item is a firearm
        if (slot >= 0 && id != null) {
            if (FirearmRegistry.get(id) != null) {
                ItemStack item = event.getItemContainer().getItemStack(slot);
                assert item != null;
                UUID uuid = item.getFromMetadataOrNull("HFF_STATE", Codec.UUID_BINARY);
                if (uuid == null) {
                    // Generate a new UUId and store it in the item's metadata
                    uuid = UUID.randomUUID();
                    ItemStack newItem = item.withMetadata("HFF_STATE", Codec.UUID_BINARY, uuid);
                    event.getItemContainer().replaceItemStackInSlot(slot, item, newItem);
                }
            }
        }
    }
}
