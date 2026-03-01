package lucis.lux.hff.commands;

import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractAsyncCommand;
import lucis.lux.hff.data.FirearmRegistry;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

import java.util.concurrent.CompletableFuture;

/**
 * The {@code ShowFirearmRegistryCommand} class is an asynchronous command that displays a list of all
 * registered firearms in the game. This command is useful for debugging and verifying that all firearms
 * have been correctly registered in the {@link FirearmRegistry}.
 *
 * <p>When executed, this command:</p>
 * <ul>
 *     <li>Iterates over all entries in the {@link FirearmRegistry}.</li>
 *     <li>Sends a message to the command context for each registered firearm, displaying its name.</li>
 * </ul>
 *
 * <p>This command is typically used by developers or administrators to check the status of registered firearms
 * and ensure that all expected firearms are present in the registry.</p>
 */
public class ShowFirearmRegistryCommand extends AbstractAsyncCommand {

    /**
     * Constructs a new {@code ShowFirearmRegistryCommand} with the specified command name and description.
     */
    public ShowFirearmRegistryCommand() {
        super("showfirearms", "Shows a list of all registered firearms");
    }

    /**
     * Executes the command asynchronously, sending a list of all registered firearms to the command context.
     *
     * <p>The following steps are performed:</p>
     * <ol>
     *     <li>Iterates over all entries in the {@link FirearmRegistry}.</li>
     *     <li>Sends a message to the command context for each registered firearm, displaying its name.</li>
     * </ol>
     *
     * @param commandContext The context in which the command is executed.
     * @return A {@link CompletableFuture} that completes when the command execution is finished.
     */
    @NonNullDecl
    @Override
    protected CompletableFuture<Void> executeAsync(@NonNullDecl CommandContext commandContext) {
        for (String name : FirearmRegistry.getList().keySet()) {
            commandContext.sendMessage(Message.raw(name));
        }
        return CompletableFuture.completedFuture(null);
    }
}
