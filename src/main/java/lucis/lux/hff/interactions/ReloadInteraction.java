package lucis.lux.hff.interactions;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Ref;
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
import lucis.lux.hff.components.ReloadingComponent;
import lucis.lux.hff.data.*;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

import java.util.UUID;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

/**
 * The {@code ReloadInteraction} class is a {@link SimpleInstantInteraction} responsible for handling
 * the reload mechanism of firearms in the game. This interaction is triggered when a player attempts
 * to reload a firearm, initiating a timed sequence that gradually reloads the ammunition.
 *
 * <p>When triggered, this interaction:
 * <ul>
 *     <li>Toggles the reloading state of the firearm.</li>
 *     <li>Schedules a timed task to incrementally reload ammunition, using {@link HytaleServer#SCHEDULED_EXECUTOR}.</li>
 *     <li>Provides feedback to the player in debug mode, indicating the progress of the reloading process.</li>
 * </ul></p>
 *
 * <p>The reloading process is asynchronous and uses scheduler to simulate
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
     * <p>The following steps are performed:</p>
     * <ol>
     *     <li>Retrieves the player and the held item from the interaction context.</li>
     *     <li>Toggles the reloading state of the firearm.</li>
     *     <li>Schedules the timed task to reload a projectile.</li>
     * </ol>
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

        Ref<EntityStore> ref = interactionContext.getEntity();
        Player player = commandBuffer.getComponent(ref, Player.getComponentType());
        ItemStack item = interactionContext.getHeldItem();

        FirearmStats stats = FirearmRegistry.get(item.getItemId());
        if (stats == null) {
            interactionContext.getState().state = InteractionState.Failed;
            return;
        }

        ReloadingComponent reloading = commandBuffer.ensureAndGetComponent(ref, HFF.get().getReloadingComponentType());

        if (reloading == null) {
            return;
        }

        reloading.toggleReloading();
        if (HFF.get().getConfigData().isDebugMode()) player.sendMessage(Message.raw("Started reloading"));
        startReloadTask(player, item, stats, reloading);
    }

    /**
     * Schedules a timed task to incrementally reload the firearm's ammunition.
     * This method is called to start the reloading process and schedules a task that runs at fixed intervals.
     *
     * <p>The following steps are performed:</p>
     * <ol>
     *   <li>Ensures the weapon has a UUID. If not, a new UUID is generated and assigned.</li>
     *   <li>Schedules a task to load projectiles at fixed intervals.</li>
     *   <li>Checks if the reloading process should continue or be cancelled.</li>
     *   <li>Loads projectiles from the player's inventory and updates the firearm's state.</li>
     * </ol>
     *
     * @param player             The player who is reloading the firearm.
     * @param weapon             The firearm item being reloaded.
     * @param stats              The statistics of the firearm.
     * @param reloadingComponent The reloading component of the firearm.
     */
    private void startReloadTask(Player player, ItemStack weapon, FirearmStats stats, ReloadingComponent reloadingComponent) {

        UUID weaponUuid = weapon.getFromMetadataOrNull("HFF_STATE", Codec.UUID_BINARY);
        player.sendMessage(Message.raw("Starting UUID: " + weaponUuid));
        if (weaponUuid == null) {
            weaponUuid = UUID.randomUUID();
            player.sendMessage(Message.raw("Changed UUID: " + weaponUuid));
            ItemStack newWeapon = weapon.withMetadata("HFF_STATE", Codec.UUID_BINARY, weaponUuid);
            player.getInventory().getHotbar().replaceItemStackInSlot(player.getInventory().getActiveHotbarSlot(), weapon, newWeapon);
        }

        AtomicReference<ScheduledFuture<Void>> reloadTaskRef = new AtomicReference<>();
        UUID finalWeaponUuid = weaponUuid;
        @SuppressWarnings("unchecked") final ScheduledFuture<Void> reloadTask = (ScheduledFuture<Void>) HytaleServer.SCHEDULED_EXECUTOR.scheduleAtFixedRate(() -> {
            player.getWorld().execute(() -> {

                ItemStack currentItem = player.getInventory().getItemInHand();
                UUID currentUuid = currentItem.getFromMetadataOrNull("HFF_STATE", Codec.UUID_BINARY);
                if (currentItem == null || !finalWeaponUuid.equals(currentUuid) || !reloadingComponent.isReloading()) {
                    cancelReloadTask(reloadTaskRef.get(), reloadingComponent);
                    return;
                }

                FirearmState state = FirearmStateManager.getState(finalWeaponUuid);

                if (state == null) {
                    state = new FirearmState();
                    FirearmStateManager.registerState(finalWeaponUuid, state);
                }

                String projectileId = getAmmoItemId(player);

                if (projectileId != null) {
                    state.loadProjectile(projectileId);
                    FirearmStateManager.updateState(finalWeaponUuid, state);

                    if (HFF.get().getConfigData().isDebugMode()) {
                        player.sendMessage(Message.raw("Reloaded a projectile"));
                    }
                } else {
                    cancelReloadTask(reloadTaskRef.get(), reloadingComponent);
                    return;
                }

                if (state.getCurrentAmmoCount() >= stats.projectileCapacity()) {
                    cancelReloadTask(reloadTaskRef.get(), reloadingComponent);
                    if (HFF.get().getConfigData().isDebugMode()) {
                        player.sendMessage(Message.raw("Finished reloading"));
                    }
                } else {
                    if (HFF.get().getConfigData().isDebugMode()) {
                        player.sendMessage(Message.raw("Loaded " + state.getCurrentAmmoCount() + '/' + stats.projectileCapacity()));
                    }
                }
            });
        }, (long) stats.reloadTime(), (long) stats.reloadTime(), TimeUnit.SECONDS);

        reloadTaskRef.set(reloadTask);
        HFF.get().getTaskRegistry().registerTask(reloadTask);
    }

    /**
     * Cancels the reloading task and updates the reloading state.
     *
     * @param reloadTask         The reloading task to cancel.
     * @param reloadingComponent The reloading component to update.
     */
    private void cancelReloadTask(ScheduledFuture<Void> reloadTask, ReloadingComponent reloadingComponent) {
        if (reloadTask != null && !reloadTask.isCancelled()) {
            reloadTask.cancel(false);
            reloadingComponent.setReloading(false);
            if (HFF.get().getConfigData().isDebugMode()) {
                HFF.get().getLogger().atInfo().log("Reload task cancelled");
            }
        }
    }

    /**
     * Searches the player's inventory for ammunition and returns the projectile ID of the first found ammunition.
     * The ammunition is removed from the player's inventory once found.
     *
     * <p>The following locations are searched in order:</p>
     * <ol>
     *   <li>Utility slot.</li>
     *   <li>Hotbar slots.</li>
     *   <li>Storage slots.</li>
     * </ol>
     *
     * @param player The player whose inventory is to be searched.
     * @return The projectile ID of the found ammunition, or {@code null} if no ammunition is found.
     */
    private String getAmmoItemId(Player player) {
        ItemStack utility = player.getInventory().getUtilityItem();
        if (utility != null) {
            AmmoData ammo = AmmoRegistry.get(utility.getItemId());

            if (ammo != null) {
                String name = utility.getItemId();
                player.getInventory().getUtility().removeItemStack(new ItemStack(utility.getItemId(), 1));
                return name;
            }
        }

        for (short i = 0; i < 9; i++) {
            ItemStack hotbar = player.getInventory().getHotbar().getItemStack(i);
            if (hotbar != null) {
                AmmoData ammo = AmmoRegistry.get(hotbar.getItemId());
                if (ammo != null) {
                    String name = hotbar.getItemId();
                    player.getInventory().getHotbar().removeItemStack(new ItemStack(hotbar.getItemId(), 1));
                    return name;
                }
            }
        }

        for (short i = 0; i < player.getInventory().getStorage().getCapacity(); i++) {
            ItemStack storageItem = player.getInventory().getStorage().getItemStack(i);
            if (storageItem != null) {
                AmmoData ammo = AmmoRegistry.get(storageItem.getItemId());
                if (ammo != null) {
                    String name = storageItem.getItemId();
                    player.getInventory().getStorage().removeItemStack(new ItemStack(storageItem.getItemId(), 1));
                    return name;
                }
            }
        }

        return null;
    }
}
