package lucis.lux.hff.interactions;

import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.protocol.InteractionState;
import com.hypixel.hytale.protocol.InteractionType;
import com.hypixel.hytale.server.core.HytaleServer;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.entity.InteractionContext;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.modules.interaction.interaction.CooldownHandler;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.SimpleInstantInteraction;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import lucis.lux.hff.HFF;
import lucis.lux.hff.components.AmmoComponent;
import lucis.lux.hff.components.FirearmStatsComponent;
import lucis.lux.hff.util.ComponentRefResult;
import lucis.lux.hff.util.ConfigManager;
import lucis.lux.hff.util.EnsureEntity;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * The {@code ReloadInteraction} class is a {@link SimpleInstantInteraction} responsible for handling
 * the reload mechanism of firearms in the game. This interaction is triggered when a player attempts
 * to reload a firearm, initiating a timed sequence that gradually reloads the ammunition.
 *
 * <p>Whenn triggered, this interaction:
 * <ul>
 *     <li>Checks if the player's held item is a valid firearm with a {@link FirearmStatsComponent} and a {@link AmmoComponent}.</li>
 *     <li>Toggles the reloading state of the firearm.</li>
 *     <li>Schedules a timed task to incrementally reload ammunition, using {@link HytaleServer#SCHEDULED_EXECUTOR}.</li>
 *     <li>Provides feedback to the player in debug mode, indicating the progress of the reloading process.</li>
 * </ul></p>
 *
 * <p>The reloading process is asynchronous and uses a recursive approach to simulate
 * the loading of each projectile. Each projectile is loaded after a delay defined by the firearm's
 * reload time. The process stops when the firearm's ammunition capacity is reached or if reloading
 * is interrupted.</p>
 */
public class ReloadInteraction extends SimpleInstantInteraction {

    /**
     * The {@link BuilderCodec} for serializing and deserializing this interaction.
     */
    public static final BuilderCodec<ReloadInteraction> CODEC = BuilderCodec.builder(ReloadInteraction.class, ReloadInteraction::new, SimpleInstantInteraction.CODEC).build();

    /**
     * Called when the interaction is first run. This method initializes the reloading process
     * for the player's held firearm.
     *
     * @param interactionType    The type of interaction.
     * @param interactionContext The context of the interaction, including references to the player and held item.
     * @param cooldownHandler    The handler for managing cooldowns.
     */
    @Override
    protected void firstRun(@NonNullDecl InteractionType interactionType, @NonNullDecl InteractionContext interactionContext, @NonNullDecl CooldownHandler cooldownHandler) {
        CommandBuffer<EntityStore> commandBuffer = interactionContext.getCommandBuffer();
        if (commandBuffer == null) {
            interactionContext.getState().state = InteractionState.Failed;
            return;
        }

        Store<EntityStore> store = commandBuffer.getExternalData().getStore();

        if (store == null) {
            interactionContext.getState().state = InteractionState.Failed;
            return;
        }

        Ref<EntityStore> ref = interactionContext.getEntity();

        Player player = commandBuffer.getComponent(ref, Player.getComponentType());

        ItemStack item = interactionContext.getHeldItem();

        ComponentRefResult<FirearmStatsComponent> statsResult = EnsureEntity.get(interactionContext, FirearmStatsComponent.class, HFF.get().getFirearmStatsComponentType(), item.getItemId());
        ComponentRefResult<AmmoComponent> ammoResult = EnsureEntity.get(interactionContext, AmmoComponent.class, HFF.get().getAmmoComponentType(), item.getItemId());
        if (statsResult.newlyCreated() || ammoResult.newlyCreated()) return;
        FirearmStatsComponent stats = statsResult.component();
        AmmoComponent ammo = ammoResult.component();
        // TODO: use an ammo item to reload

        ammo.toggleReloading();
        if (ConfigManager.isDebugMode()) player.sendMessage(Message.raw("Started reloading"));
        setReloadTimer(stats, ammo, player, 0);
    }

    /**
     * Schedules a timed task to incrementally reload the firearm's ammunition.
     * This method is called recursively to load each projectile after a delay.
     *
     * @param stats  The firearm's statistics component, containing reload time and capacity.
     * @param ammo   The ammunition component, tracking the current loaded amount.
     * @param player The player reloading the firearm.
     * @param depth  The current recursion depth, representing the number of projectiles loaded so far.
     */
    private void setReloadTimer(FirearmStatsComponent stats, AmmoComponent ammo, Player player, int depth) {
        if (!ammo.isReloading() || depth >= stats.getProjectileCapacity()) {
            if (ConfigManager.isDebugMode()) player.sendMessage(Message.raw("Stopped reloading"));
            return;
        }
        @SuppressWarnings("unchecked")
        ScheduledFuture<Void> reloadTask = (ScheduledFuture<Void>) HytaleServer.SCHEDULED_EXECUTOR.schedule(() -> {
            player.getWorld().execute(() -> {
                ammo.incrementLoadedAmount();
                if (ConfigManager.isDebugMode()) player.sendMessage(Message.raw("Reloaded a projectile"));
                if (ammo.getLoadedAmount() < stats.getProjectileCapacity()) {
                    this.setReloadTimer(stats, ammo, player, depth + 1);
                } else if (ConfigManager.isDebugMode()) player.sendMessage(Message.raw("Stopped reloading"));
            });
        }, (long) stats.getReloadTime(), TimeUnit.SECONDS);

        HFF.get().getTaskRegistry().registerTask(reloadTask);

    }
}
