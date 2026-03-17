package lucis.lux.hff.commands;

import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractAsyncCommand;
import lucis.lux.hff.data.registry.Registries;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

import java.util.concurrent.CompletableFuture;

/**
 * The {@code ShowProjectilesCommand} class is an asynchronous command that displays a list of all
 * registered projectiles in the game. This command is useful for debugging and verifying that all projectiles
 * have been correctly registered in the {@link }.
 *
 * <p>When executed, this command:</p>
 * <ul>
 *   <li>Iterates over all entries in the {@link }.</li>
 *   <li>Sends a message to the command context for each registered projectile, displaying its name.</li>
 * </ul>
 *
 * <p>This command is typically used by developers or administrators to check the status of registered projectiles
 * and ensure that all expected projectiles are present in the registry.</p>
 */

public class ShowProjectilesCommand extends AbstractAsyncCommand {

    /**
     * Constructs a new {@code ShowProjectilesCommand} with the specified command name and description.
     */
    public ShowProjectilesCommand() {
        super("showprojectiles", "Shows a list of all registered projectiles");
    }

    /**
     * Executes the command asynchronously, sending a list of all registered projectiles to the command context.
     *
     * <p>The following steps are performed:</p>
     * <ol>
     *   <li>Iterates over all entries in the {@link }.</li>
     *   <li>Sends a message to the command context for each registered projectile, displaying its name.</li>
     * </ol>
     *
     * @param commandContext The context in which the command is executed.
     * @return A {@link CompletableFuture} that completes when the command execution is finished.
     */
    @NonNullDecl
    @Override
    protected CompletableFuture<Void> executeAsync(@NonNullDecl CommandContext commandContext) {
        for (String name : Registries.AMMO_DATA.copy().keySet()) {
            commandContext.sendMessage(Message.raw(name));
        }
        return CompletableFuture.completedFuture(null);
    }
}