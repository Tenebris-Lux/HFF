package lucis.lux.hff.interactions;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.event.IEventDispatcher;
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.protocol.Direction;
import com.hypixel.hytale.protocol.InteractionState;
import com.hypixel.hytale.protocol.InteractionType;
import com.hypixel.hytale.protocol.MovementStates;
import com.hypixel.hytale.server.core.HytaleServer;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.entity.InteractionContext;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.entity.movement.MovementStatesComponent;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.modules.interaction.interaction.CooldownHandler;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.SimpleInstantInteraction;
import com.hypixel.hytale.server.core.modules.projectile.ProjectileModule;
import com.hypixel.hytale.server.core.modules.projectile.config.ProjectileConfig;
import com.hypixel.hytale.server.core.universe.Universe;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import lucis.lux.hff.HFF;
import lucis.lux.hff.components.AimComponent;
import lucis.lux.hff.components.ReloadingComponent;
import lucis.lux.hff.data.*;
import lucis.lux.hff.events.FirearmShootEvent;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

import java.util.UUID;

/**
 * The {@code ShootFirearmInteraction} class is a {@link SimpleInstantInteraction} responsible for handling
 * the shooting mechanism of firearms in the game. This interaction is triggered when a player attempts
 * to fire a firearm, initiating the process of spawning projectiles and applying recoil.
 *
 * <p>When triggered, this interaction:</p>
 * <ul>
 *     <li>Stops any ongoing reloading process.</li>
 *     <li>Calculates the direction of the projectile, taking into account spread and aiming.</li>
 *     <li>Spawns projectiles based on the firearm's rate of fire and ammunition availability.</li>
 *     <li>Applies recoil to the player.</li>
 *     <li>Dispatches an {@link FirearmShootEvent} event to notify other systems.</li>
 * </ul>
 *
 * <p>The shooting process considers the following factors:</p>
 * <ul>
 *     <li>Firearm spread, which is reduced when aiming.</li>
 *     <li>Projectile velocity, which can be modified by ammunition effects.</li>
 *     <li>Recoil, which affects the player's view after shooting.</li>
 *     <li>Ammunition consumption, which reduces the loaded ammunition count.</li>
 *     <li>Movement penalty, which increases spread when the player is moving.</li>
 * </ul>
 */
public class ShootFirearmInteraction extends SimpleInstantInteraction {
    /**
     * The {@link BuilderCodec} for serializing and deserializing this interaction.
     */
    public static final BuilderCodec<ShootFirearmInteraction> CODEC = BuilderCodec.builder(ShootFirearmInteraction.class, ShootFirearmInteraction::new, SimpleInstantInteraction.CODEC).build();

    /**
     * Calculates the direction of the projectile based on the player's orientation, firearm spread,
     * aiming state, and movement state.
     *
     * @param stats         The firearm's statistics component.
     * @param aim           The aiming component.
     * @param orientation   The player's look orientation.
     * @param movementState The player's movement state.
     * @return A normalized {@link Vector3d} representing the projectile's direction.
     */
    @NonNullDecl
    private static Vector3d getDirection(FirearmStats stats, AimComponent aim, Direction orientation, MovementStates movementState) {
        double spread = Math.toRadians(stats.spreadBase());
        if (aim != null && aim.isAiming()) spread *= 0.7;
        if (movementState.sprinting || movementState.jumping) spread *= stats.movementPenalty();

        double yaw = orientation.yaw + (Math.random() - 0.5) * 2 * spread;
        double pitch = orientation.pitch + (Math.random() - 0.5) * 2 * spread;

        double x = -Math.sin(yaw) * Math.cos(pitch);
        double y = Math.sin(pitch);
        double z = -Math.cos(yaw) * Math.cos(pitch);

        return new Vector3d(x, y, z).normalize();
    }

    /**
     * Called when the interaction is first run. This method initializes the shooting process
     * for the player's held firearm.
     *
     * <p>The following steps are performed:</p>
     * <ol>
     *     <li>Stops any ongoing reloading process.</li>
     *     <li>Ensures the firearm has a UUID and state. If not, a new UUID and state are created.</li>
     *     <li>Calculates the number of shots that can be fired in the current tick based on the firearm's cooldown.</li>
     *     <li>Spawns projectiles and applies recoil for each shot.</li>
     * </ol>
     *
     * @param interactionType    The type of interaction.
     * @param interactionContext The context of the interaction, including references to the player and held item.
     * @param cooldownHandler    The handler for managing cooldowns.
     */
    @Override
    protected void firstRun(@NonNullDecl InteractionType interactionType, @NonNullDecl InteractionContext interactionContext, @NonNullDecl CooldownHandler cooldownHandler) {

        Ref<EntityStore> playerRef = interactionContext.getEntity();

        CommandBuffer<EntityStore> commandBuffer = interactionContext.getCommandBuffer();

        Player player = commandBuffer.getComponent(playerRef, Player.getComponentType());

        ReloadingComponent reloadingComponent = commandBuffer.getComponent(playerRef, HFF.get().getReloadingComponentType());

        if (reloadingComponent != null) {
            reloadingComponent.setReloading(false);
        }

        ItemStack item = interactionContext.getHeldItem();

        UUID weaponUuid = item.getFromMetadataOrNull("HFF_STATE", Codec.UUID_BINARY);
        if (weaponUuid == null) {
            weaponUuid = UUID.randomUUID();
            ItemStack newWeapon = item.withMetadata("HFF_STATE", Codec.UUID_BINARY, weaponUuid);
            player.getInventory().getHotbar().replaceItemStackInSlot(player.getInventory().getActiveHotbarSlot(), item, newWeapon);
            item = newWeapon;
        }
        FirearmState state = FirearmStateManager.getState(weaponUuid);

        if (state == null) {
            state = new FirearmState();
            FirearmStateManager.registerState(weaponUuid, state);
            if (ConfigManager.isDebugMode()) {
                player.sendMessage(Message.raw("Created a state for the item"));
            }
            return;
        }

        FirearmStats stats = FirearmRegistry.get(item.getItemId());

        if (stats != null) {

            float tickRate = Universe.get().getDefaultWorld().getTps();

            double cooldownMs = 60000.0 / stats.rpm();
            double tickMs = 1000 / tickRate;

            int shotsPerTick = (int) Math.floor(tickMs / cooldownMs);

            if (ConfigManager.isDebugMode()) {
                player.sendMessage(Message.raw("Shots per tick: " + tickMs + '/' + cooldownMs + '=' + shotsPerTick));
            }

            if (shotsPerTick > 0) {
                for (int i = 0; i < shotsPerTick; i++) {
                    if (preIsCancelled(player, state, stats, interactionContext)) return;
                    String projectileId = state.consumeNextProjectile();
                    if (projectileId != null) {
                        FirearmStateManager.updateState(weaponUuid, state);
                        this.spawnProjectile(stats, state, projectileId, interactionContext);
                    } else break;
                }
            } else {
                if (preIsCancelled(player, state, stats, interactionContext)) return;
                String projectileId = state.consumeNextProjectile();
                if (projectileId != null) {
                    FirearmStateManager.updateState(weaponUuid, state);
                    this.spawnProjectile(stats, state, projectileId, interactionContext);
                }
            }


        } else if (ConfigManager.isDebugMode()) {
            player.sendMessage(Message.raw("Did not find any stats for the firearm"));
        }

    }

    private boolean preIsCancelled(Player player, FirearmState state, FirearmStats stats, InteractionContext interactionContext) {
        IEventDispatcher<FirearmShootEvent.Pre, FirearmShootEvent.Pre> dispatcher =
                HytaleServer.get().getEventBus().dispatchFor(FirearmShootEvent.Pre.class);

        if (dispatcher.hasListener()) {
            FirearmShootEvent.Pre event = new FirearmShootEvent.Pre(player, state, stats);
            dispatcher.dispatch(event);

            player.sendMessage(Message.raw("Dispatched"));
            if (event.isCancelled()) {
                interactionContext.getState().state = InteractionState.Failed;
                return true;
            }
        }
        return false;
    }

    /**
     * Spawns a projectile and applies recoil to the player.
     * <p>The following steps are performed:</p>
     * <ol>
     *     <li>Retrieves the projectile configuration from the asset map.</li>
     *     <li>Calculates the projectile's direction and velocity.</li>
     *     <li>Dispatches an {@link FirearmShootEvent} event to notify other systems.</li>
     *     <li>Spawns the projectile using the {@link ProjectileModule}.</li>
     *     <li>Applies recoil to the player.</li>
     * </ol>
     *
     * @param stats              The firearm's statistics component.
     * @param projectileId       The ID of the projectile to spawn.
     * @param interactionContext The context of the interaction.
     */
    private void spawnProjectile(FirearmStats stats, FirearmState state, String projectileId, InteractionContext interactionContext) {
        CommandBuffer<EntityStore> commandBuffer = interactionContext.getCommandBuffer();
        if (commandBuffer == null) {
            interactionContext.getState().state = InteractionState.Failed;
            return;
        }

        Ref<EntityStore> ref = interactionContext.getEntity();

        ProjectileConfig config = ProjectileConfig.getAssetMap().getAsset(projectileId);

        if (config == null) {
            HFF.get().getLogger().atSevere().log("ProjectileConfig not found for ID: " + projectileId);
            return;
        }

        TransformComponent transform = commandBuffer.getComponent(ref, TransformComponent.getComponentType());
        if (transform == null) {
            HFF.get().getLogger().atSevere().log("TransformComponent is null");
            return;
        }

        Direction orientation = transform.getSentTransform().lookOrientation;

        if (orientation == null) {
            HFF.get().getLogger().atSevere().log("Orientation is null");
            return;
        }

        Player player = commandBuffer.getComponent(ref, Player.getComponentType());
        if (player == null) {
            HFF.get().getLogger().atSevere().log("Player is null");
            return;
        }
        AimComponent aimComponent = commandBuffer.getComponent(ref, HFF.get().getAimComponentType());

        MovementStatesComponent movementStatesComponent = commandBuffer.getComponent(ref, MovementStatesComponent.getComponentType());
        MovementStates movementStates = movementStatesComponent != null ? movementStatesComponent.getMovementStates() : null;

        Vector3d direction = getDirection(stats, aimComponent, orientation, movementStates);

        if (ConfigManager.isDebugMode()) {
            HFF.get().getLogger().atInfo().log(
                    "Pitch: " + orientation.pitch +
                            "\nYaw: " + orientation.yaw +
                            "\nCalculated direction: " + direction +
                            "\nwith base spread " + stats.spreadBase() + "°" +
                            "\n " + System.currentTimeMillis());
        }

        double baseVelocity = config.getLaunchForce();

        direction = direction.scale(baseVelocity);

        Vector3d position = transform.getPosition().clone();
        position.y += 1.6;

        IEventDispatcher<FirearmShootEvent.Post, FirearmShootEvent.Post> dispatcher = HytaleServer.get().getEventBus().dispatchFor(FirearmShootEvent.Post.class);

        if (dispatcher.hasListener()) {
            FirearmShootEvent.Post event = new FirearmShootEvent.Post(player, state, stats, position, direction);
            dispatcher.dispatch(event);
        }

        if (stats.disabled()) return;

        ProjectileModule.get().spawnProjectile(ref, commandBuffer, config, position, direction);

        player.addLocationChange(ref, stats.horizontalRecoil(), stats.verticalRecoil(), 0, commandBuffer);
    }
}
