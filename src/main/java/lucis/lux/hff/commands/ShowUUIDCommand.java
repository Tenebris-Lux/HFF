package lucis.lux.hff.commands;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractPlayerCommand;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import lucis.lux.hff.data.registry.Registries;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

import java.util.UUID;

/**
 * The {@code ShowUUIDCommand} class is a player command that displays the UUID of the currently held weapon.
 * This command is useful for debugging and verifying that weapons have been correctly assigned a UUID
 * and that their state is properly registered in the {@link }.
 *
 * <p>When executed, this command:</p>
 * <ul>
 *   <li>Retrieves the UUID of the currently held weapon from its metadata.</li>
 *   <li>Checks if the weapon's state is registered in the {@link }.</li>
 *   <li>Sends a message to the player with the UUID and registration status of the weapon.</li>
 * </ul>
 *
 * <p>This command is typically used by developers or administrators to check the status of weapons
 * and ensure that their UUIDs and states are correctly managed.</p>
 */
public class ShowUUIDCommand extends AbstractPlayerCommand {

    /**
     * Constructs a new {@code ShowUUIDCommand} with the specified command name, description, and usage restrictions.
     */
    public ShowUUIDCommand() {
        super("showuuid", "Shows the UUID of the currently held weapon", false);
    }

    /**
     * Executes the command, displaying the UUID and registration status of the currently held weapon.
     *
     * <p>The following steps are performed:</p>
     * <ol>
     *   <li>Retrieves the player and their currently held weapon.</li>
     *   <li>Extracts the UUID of the weapon from its metadata.</li>
     *   <li>Checks if the weapon's state is registered in the {@link }.</li>
     *   <li>Sends a message to the player with the UUID and registration status of the weapon.</li>
     * </ol>
     *
     * @param commandContext The context in which the command is executed.
     * @param store          The component store.
     * @param ref            The reference to the player's entity.
     * @param playerRef      The reference to the player.
     * @param world          The world in which the command is executed.
     */
    @Override
    protected void execute(@NonNullDecl CommandContext commandContext, @NonNullDecl Store<EntityStore> store, @NonNullDecl Ref<EntityStore> ref, @NonNullDecl PlayerRef playerRef, @NonNullDecl World world) {
        Player player = store.getComponent(ref, Player.getComponentType());
        UUID weaponUuid = player.getInventory().getActiveHotbarItem().getFromMetadataOrNull("HFF_STATE", Codec.UUID_BINARY);
        boolean registered = false;

        if (weaponUuid != null) {
            registered = Registries.FIREARM_STATES.get(weaponUuid) != null;
        }

        commandContext.sendMessage(Message.raw("UUID: " + weaponUuid + "\nRegistered: " + registered));
    }
}