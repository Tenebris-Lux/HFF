package lucis.lux.hff.interactions;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.event.IEventDispatcher;
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
import lucis.lux.hff.data.AmmoData;
import lucis.lux.hff.data.FirearmState;
import lucis.lux.hff.data.FirearmStats;
import lucis.lux.hff.data.MagazineData;
import lucis.lux.hff.data.registry.Registries;
import lucis.lux.hff.enums.MagazineType;
import lucis.lux.hff.events.ReloadEvent;
import lucis.lux.hff.util.StatCalculator;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

import java.util.UUID;

/**
 * The {@code ReloadInteraction} class is a {@link SimpleInstantInteraction} responsible for handling
 * the reload mechanism of firearms in the game. This interaction is triggered when a player attempts
 * to reload a firearm, initiating a sequence that reloads the ammunition.
 *
 * <p>When triggered, this interaction:</p>
 * <ul>
 *   <li>Dispatches a {@link ReloadEvent.Pre} event to notify other systems about the reloading process.</li>
 *   <li>Toggles the reloading state of the firearm.</li>
 *   <li>Handles the reloading process, either by loading individual projectiles or by inserting a magazine.</li>
 *   <li>Provides feedback to the player in debug mode, indicating the progress of the reloading process.</li>
 *   <li>Dispatches a {@link ReloadEvent.Post} event to notify other systems about the completion of the reloading process.</li>
 * </ul>
 *
 * <p>The reloading process is synchronous and handles the reloading in a single step, either by loading
 * projectiles directly or by swapping magazines.</p>
 *
 * <p>This interaction supports both internal and external magazine systems.</p>
 */
public class ReloadInteraction extends SimpleInstantInteraction {

    /**
     * The {@link BuilderCodec} for serializing and deserializing this interaction.
     */
    public static final BuilderCodec<ReloadInteraction> CODEC = BuilderCodec.builder(
            ReloadInteraction.class,
            ReloadInteraction::new,
            SimpleInstantInteraction.CODEC
    ).build();

    /**
     * Called when the interaction is first run. This method initializes the reloading process
     * for the player's held firearm.
     *
     * <p>The following steps are performed:</p>
     * <ol>
     *   <li>Retrieves the player and the held item from the interaction context.</li>
     *   <li>Checks if the firearm is jammed and handles unjamming if necessary.</li>
     *   <li>Dispatches a {@link ReloadEvent.Pre} event to notify other systems about the reloading process.</li>
     *   <li>Toggles the reloading state of the firearm.</li>
     *   <li>Handles the reloading process based on the magazine type (internal or external).</li>
     *   <li>Dispatches a {@link ReloadEvent.Post} event to notify other systems about the completion of the reloading process.</li>
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

        FirearmStats stats = Registries.FIREARM_STATS.get(item.getItemId());
        if (stats == null) {
            interactionContext.getState().state = InteractionState.Failed;
            return;
        }

        UUID weaponUuid = item.getFromMetadataOrNull("HFF_STATE", Codec.UUID_BINARY);
        if (weaponUuid == null) {
            weaponUuid = UUID.randomUUID();
            player.sendMessage(Message.raw("Changed UUID: " + weaponUuid));
            ItemStack newWeapon = item.withMetadata("HFF_STATE", Codec.UUID_BINARY, weaponUuid);
            player.getInventory().getHotbar().replaceItemStackInSlot(player.getInventory().getActiveHotbarSlot(), item, newWeapon);
        }
        FirearmState state = Registries.FIREARM_STATES.get(weaponUuid);

        stats = StatCalculator.getModifiedStats(stats, state);

        if (state.isJammed()) {
            state.setJammed(false);
            state.consumeNextProjectile(stats);

            Registries.FIREARM_STATES.update(weaponUuid, state);

            if (HFF.get().getConfigData().isDebugMode()) {
                player.sendMessage(Message.raw("Weapon unjammed"));
            }
            return;
        }

        ReloadingComponent reloading = commandBuffer.ensureAndGetComponent(ref, HFF.get().getReloadingComponentType());

        if (reloading == null) {
            return;
        }

        IEventDispatcher<ReloadEvent.Pre, ReloadEvent.Pre> preDispatcher = HytaleServer.get().getEventBus().dispatchFor(ReloadEvent.Pre.class);
        if (preDispatcher.hasListener()) {
            ReloadEvent.Pre preEvent = new ReloadEvent.Pre(player, item, stats);
            preDispatcher.dispatch(preEvent);

            if (preEvent.isCancelled()) {
                interactionContext.getState().state = InteractionState.Failed;
                return;
            }

            stats = preEvent.getStats();
        }

        reloading.toggleReloading();
        if (HFF.get().getConfigData().isDebugMode()) player.sendMessage(Message.raw("Started reloading"));

        boolean isHardcore = HFF.get().getConfigData().isHardcoreMagazineSystem();
        boolean isExternalMag = MagazineType.EXTERNAL.equals(stats.magazineType());

        boolean reloadSuccess = false;

        if (isHardcore && isExternalMag) {
            reloadSuccess = handleMagazineReload(player, state, stats, weaponUuid);
        } else {
            reloadSuccess = handleInternalReload(player, state, stats, weaponUuid);
        }

        if (reloadSuccess) {
            Registries.FIREARM_STATES.update(weaponUuid, state);
            cooldownHandler.getCooldown(weaponUuid.toString(), stats.reloadTime(), new float[0], true, false);
        }

        IEventDispatcher<ReloadEvent.Post, ReloadEvent.Post> postDispatcher = HytaleServer.get().getEventBus().dispatchFor(ReloadEvent.Post.class);
        if (postDispatcher.hasListener()) {
            ReloadEvent.Post postEvent = new ReloadEvent.Post(player, item, stats, reloadSuccess);
            postDispatcher.dispatch(postEvent);
        }
    }

    /**
     * Handles the reloading process for firearms with an external magazine system.
     *
     * <p>The following steps are performed:</p>
     * <ol>
     *   <li>Checks if the player has a valid magazine in the utility slot.</li>
     *   <li>Validates the magazine's calibre against the firearm's calibre.</li>
     *   <li>Inserts the magazine into the firearm.</li>
     * </ol>
     *
     * @param player     The player who is reloading the firearm.
     * @param state      The state of the firearm.
     * @param stats      The statistics of the firearm.
     * @param weaponUuid The UUID of the firearm.
     * @return {@code true} if the magazine was successfully inserted, {@code false} otherwise.
     */
    private boolean handleMagazineReload(Player player, FirearmState state, FirearmStats stats, UUID weaponUuid) {
        ItemStack utilityItem = player.getInventory().getUtilityItem();

        if (utilityItem == null) {
            player.sendMessage(Message.raw("You need a magazine in your left hand to reload."));
            return false;
        }

        MagazineData magazine = Registries.MAGAZINE_DATA.get(utilityItem.getItemId());
        UUID utilityUuid = utilityItem.getFromMetadataOrNull("HFF_STATE", Codec.UUID_BINARY);

        if (magazine == null) {
            player.sendMessage(Message.raw("That is not a valid magazine."));
            return false;
        }

        if (!magazine.calibre().equals(stats.calibre())) {
            player.sendMessage(Message.raw("Wrong calibre! This weapon needs " + stats.calibre() + "."));
            return false;
        }

        if (state.getInsertedMagazineUuid() != null) {
            ItemStack returnedMagazine = new ItemStack(state.getInsertedMagazineName());
            returnedMagazine = returnedMagazine.withMetadata("HFF_STATE", Codec.UUID_BINARY, state.getInsertedMagazineUuid());
            player.getInventory().getStorage().addItemStack(returnedMagazine);
        }

        state.setInsertedMagazineUuid(utilityUuid);
        state.setInsertedMagazineName(utilityItem.getItemId());
        Registries.FIREARM_STATES.update(weaponUuid, state);
        player.getInventory().getUtility().removeItemStack(utilityItem);
        return true;
    }

    /**
     * Handles the reloading process for firearms with an internal magazine system.
     *
     * <p>The following steps are performed:</p>
     * <ol>
     *   <li>Checks if the firearm is already fully loaded.</li>
     *   <li>Checks if the player has valid ammunition in the utility slot.</li>
     *   <li>Loads the ammunition into the firearm.</li>
     * </ol>
     *
     * @param player     The player who is reloading the firearm.
     * @param state      The state of the firearm.
     * @param stats      The statistics of the firearm.
     * @param weaponUuid The UUID of the firearm.
     * @return {@code true} if the ammunition was successfully loaded, {@code false} otherwise.
     */
    private boolean handleInternalReload(Player player, FirearmState state, FirearmStats stats, UUID weaponUuid) {
        int missingBullets = stats.projectileCapacity() - state.getCurrentAmmoCount();
        if (missingBullets <= 0) {
            player.sendMessage(Message.raw("Weapon is already fully loaded."));
            return false;
        }

        ItemStack utility = player.getInventory().getUtilityItem();

        if (utility == null) {
            player.sendMessage(Message.raw("You need ammo in your left hand to reload."));
            return false;
        }
        AmmoData ammo = Registries.AMMO_DATA.get(utility.getItemId());

        if (ammo != null) {
            if (ammo.calibre().equals(stats.calibre())) {
                String name = utility.getItemId();

                state.loadProjectile(name);
                Registries.FIREARM_STATES.update(weaponUuid, state);

                player.getInventory().getUtility().removeItemStackFromSlot(player.getInventory().getActiveUtilitySlot(), 1);

                return true;
            } else {
                player.sendMessage(Message.raw("Wrong ammo calibre."));
            }
        }

        return false;
    }
}
