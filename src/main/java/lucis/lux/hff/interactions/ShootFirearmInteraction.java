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
import lucis.lux.hff.components.DamageComponent;
import lucis.lux.hff.components.ReloadingComponent;
import lucis.lux.hff.data.AmmoData;
import lucis.lux.hff.data.FirearmState;
import lucis.lux.hff.data.FirearmStats;
import lucis.lux.hff.data.registry.Registries;
import lucis.lux.hff.enums.FireMode;
import lucis.lux.hff.events.DryFireEvent;
import lucis.lux.hff.events.ShootEvent;
import lucis.lux.hff.util.StatCalculator;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

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
 *     <li>Dispatches an {@link ShootEvent} event to notify other systems.</li>
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

        player.sendMessage(Message.raw("At ShootFirearmInteraction"));

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
        FirearmState state = Registries.FIREARM_STATES.get(weaponUuid);

        if (state == null) {
            state = new FirearmState();
            Registries.FIREARM_STATES.register(weaponUuid, state);
            if (HFF.get().getConfigData().isDebugMode()) {
                player.sendMessage(Message.raw("Created a state for the item"));
            }
            return;
        }

        FirearmStats baseStats = Registries.FIREARM_STATS.get(item.getItemId());

        if (state.isJammed()) {
            if (HFF.get().getConfigData().isDebugMode()) {
                player.sendMessage(Message.raw("Weapon is jamming! Press 'F' to unjam."));
            }

            // TODO: Play quiet clíck sound
            interactionContext.getState().state = InteractionState.Failed;
            return;
        }

        FireMode activeFireMode = state.getCurrentFireMode(baseStats);

        if (state.isBursting()) {
            player.sendMessage(Message.raw("Weapon bursting"));
            interactionContext.getState().state = InteractionState.Failed;
            return;
        }

        if (baseStats != null) {
            FirearmStats stats = StatCalculator.getModifiedStats(baseStats, state);

            if (stats.jamChance() > 0 && Math.random() < stats.jamChance()) {
                state.setJammed(true);
                Registries.FIREARM_STATES.update(weaponUuid, state);

                if (HFF.get().getConfigData().isDebugMode()) {
                    player.sendMessage(Message.raw("Jam!"));
                }
                //TODO: Play click sound
                interactionContext.getState().state = InteractionState.Failed;
                player.sendMessage(Message.raw("Jam"));
                return;
            }

            if (activeFireMode.equals(FireMode.SEMI_AUTOMATIC)
                    || activeFireMode.equals(FireMode.SINGLE_ACTION)
                    || activeFireMode.equals(FireMode.MANUAL)
                    || activeFireMode.equals(FireMode.SINGLE_SHOT)
                    || activeFireMode.equals(FireMode.DOUBLE_ACTION)
            ) {
                shoot(state, stats, weaponUuid, interactionContext);
                return;
            }

            if (activeFireMode.equals(FireMode.BURST)) {
                int burstAmount = stats.burstRounds();
                long delayBetweenShotsMs = (long) (60000.0 / stats.rpm());

                state.setBursting(true);

                shoot(state, stats, weaponUuid, interactionContext);

                for (int i = 1; i < burstAmount; i++) {
                    UUID finalWeaponUuid = weaponUuid;
                    FirearmState finalState = state;
                    int finalI = i;
                    HytaleServer.SCHEDULED_EXECUTOR.schedule(() -> {
                        shoot(finalState, stats, finalWeaponUuid, interactionContext);

                        if (finalI == burstAmount - 1) {
                            finalState.setBursting(false);
                            Registries.FIREARM_STATES.update(finalWeaponUuid, finalState);
                        }
                    }, delayBetweenShotsMs * i, TimeUnit.MILLISECONDS);
                }

                cooldownHandler.resetCooldown(weaponUuid.toString(), delayBetweenShotsMs * burstAmount, new float[0], true);
            }

            if (activeFireMode.equals(FireMode.AUTOMATIC)) {

                float tickRate = Universe.get().getDefaultWorld().getTps();

                double cooldownMs = 60000.0 / stats.rpm();
                double tickMs = 1000 / tickRate;

                int shotsPerTick = (int) Math.floor(tickMs / cooldownMs);

                if (HFF.get().getConfigData().isDebugMode()) {
                    player.sendMessage(Message.raw("Shots per tick: " + tickMs + '/' + cooldownMs + '=' + shotsPerTick));
                }

                if (shotsPerTick > 0) {
                    for (int i = 0; i < shotsPerTick; i++) {
                        shoot(state, stats, weaponUuid, interactionContext);
                    }
                } else {
                    shoot(state, stats, weaponUuid, interactionContext);
                }
            }
        } else if (HFF.get().getConfigData().isDebugMode()) {
            player.sendMessage(Message.raw("Did not find any stats for the firearm"));
        }

    }

    private void shoot(FirearmState state, FirearmStats stats, UUID weaponUuid, InteractionContext interactionContext) {
        String ammoItemId = state.consumeNextProjectile(stats);
        for (int i = 0; i < stats.projectileAmount(); i++) {
            if (ammoItemId != null) {
                Registries.FIREARM_STATES.update(weaponUuid, state);
                AmmoData ammo = Registries.AMMO_DATA.get(ammoItemId);
                if (ammo != null) {
                    this.spawnProjectile(stats, state, ammo, interactionContext);
                }
            } else {
                IEventDispatcher<DryFireEvent, DryFireEvent> dispatcher = HytaleServer.get().getEventBus().dispatchFor(DryFireEvent.class);

                if (dispatcher.hasListener()) {
                    DryFireEvent event = new DryFireEvent(interactionContext.getEntity(), state);
                    dispatcher.dispatch(event);
                }
                break;
            }
        }
    }

    /**
     * Spawns a projectile and applies recoil to the player.
     * <p>The following steps are performed:</p>
     * <ol>
     *     <li>Retrieves the projectile configuration from the asset map.</li>
     *     <li>Calculates the projectile's direction and velocity.</li>
     *     <li>Spawns the projectile using the {@link ProjectileModule}.</li>
     *     <li>Applies recoil to the player.</li>
     * </ol>
     *
     * @param stats              The firearm's statistics component.
     * @param ammo               The Ammo statistics object.
     * @param interactionContext The context of the interaction.
     */
    private void spawnProjectile(FirearmStats stats, FirearmState state, AmmoData ammo, InteractionContext interactionContext) {
        CommandBuffer<EntityStore> commandBuffer = interactionContext.getCommandBuffer();
        if (commandBuffer == null) {
            interactionContext.getState().state = InteractionState.Failed;
            return;
        }

        Ref<EntityStore> ref = interactionContext.getEntity();

        ProjectileConfig config = ProjectileConfig.getAssetMap().getAsset(ammo.projectileId());

        if (config == null) {
            HFF.get().getLogger().atSevere().log("ProjectileConfig not found for ID: " + ammo.projectileId());
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

        if (HFF.get().getConfigData().isDebugMode()) {
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

        IEventDispatcher<ShootEvent.Post, ShootEvent.Post> dispatcher = HytaleServer.get().getEventBus().dispatchFor(ShootEvent.Post.class);

        if (dispatcher.hasListener()) {
            ShootEvent.Post event = new ShootEvent.Post(ref, state, stats, position, direction);
            dispatcher.dispatch(event);
        }

        if (stats.disabled()) {
            return;
        }

        Ref<EntityStore> projectile = ProjectileModule.get().spawnProjectile(ref, commandBuffer, config, position, direction);
        interactionContext.getCommandBuffer().addComponent(projectile, HFF.get().getDamageComponentType(), new DamageComponent(
                ammo.damage(),
                position.clone(),
                stats.optimalRange(),
                stats.maxRange(),
                stats.minDamageMultiplier(),
                0.1f,
                direction));

        player.addLocationChange(ref, stats.horizontalRecoil(), stats.verticalRecoil(), 0, commandBuffer);
    }
}