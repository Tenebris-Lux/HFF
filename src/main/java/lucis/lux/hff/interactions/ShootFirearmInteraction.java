package lucis.lux.hff.interactions;

import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.event.IEventDispatcher;
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.protocol.Direction;
import com.hypixel.hytale.protocol.InteractionState;
import com.hypixel.hytale.protocol.InteractionType;
import com.hypixel.hytale.server.core.HytaleServer;
import com.hypixel.hytale.server.core.entity.InteractionContext;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.modules.interaction.interaction.CooldownHandler;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.SimpleInstantInteraction;
import com.hypixel.hytale.server.core.modules.projectile.ProjectileModule;
import com.hypixel.hytale.server.core.modules.projectile.config.ProjectileConfig;
import com.hypixel.hytale.server.core.universe.Universe;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import lucis.lux.hff.HFF;
import lucis.lux.hff.components.AimComponent;
import lucis.lux.hff.components.AmmoComponent;
import lucis.lux.hff.components.FirearmStatsComponent;
import lucis.lux.hff.interactions.events.OnShoot;
import lucis.lux.hff.util.ComponentRefResult;
import lucis.lux.hff.util.ConfigManager;
import lucis.lux.hff.util.EnsureEntity;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

/**
 * The {@code ShootFirearmInteraction} class is a {@link SimpleInstantInteraction} responsible for handling
 * the shooting mechanism of firearms in the game. This interaction is triggered when a player attempts
 * to fire a firearm, initiating the process of spawning projectiles and applying recoil.
 *
 * <p>When triggered, this interaction:</p>
 * <ul>
 *     <li>Retrieves the firearm's statistics and ammunition components.</li>
 *     <li>Calculates the direction of the projectile, taking into account spread and aiming.</li>
 *     <li>Spawns projectiles based on the firearm's rate of fire and ammunition availability.</li>
 *     <li>Applies recoil to the player.</li>
 *     <li>Dispatches an {@link OnShoot} event to notify other systems.</li>
 * </ul>
 *
 * <p>The shooting process considers the following factors:</p>
 * <ul>
 *     <li>Firearm spread, which is reduced when aiming.</li>
 *     <li>Projectile velocity, which can be modified by ammunition effects.</li>
 *     <li>Recoil, which affects the player's view after shooting.</li>
 *     <li>Ammunition consumption, which reduces the loaded ammunition count.</li>
 * </ul>
 *
 * <p>This interaction is part of the Entity Component System (ECS) architecture in Hytale
 * and is registered during plugin initialization.</p>
 */
public class ShootFirearmInteraction extends SimpleInstantInteraction {
    /**
     * The {@link BuilderCodec} for serializing and deserializing this interaction.
     */
    public static final BuilderCodec<ShootFirearmInteraction> CODEC = BuilderCodec.builder(ShootFirearmInteraction.class, ShootFirearmInteraction::new, SimpleInstantInteraction.CODEC).build();

    /**
     * Calculates the interaction of the projectile based on the player's orientation, firearm spread
     * and aiming state.
     *
     * @param stats       The firearm's statistics component.
     * @param ammo        The ammunition component.
     * @param aim         The aiming component.
     * @param orientation The player's look orientation.
     * @return A normalized {@link Vector3d} representing the projectile's direction.
     */
    @NonNullDecl
    private static Vector3d getDirection(FirearmStatsComponent stats, AmmoComponent ammo, AimComponent aim, Direction orientation) {
        double spread = Math.toRadians(stats.getSpreadBase() * ammo.getSpreadMod());
        if (aim != null && aim.isAiming()) spread *= 0.7;
        double yaw = orientation.yaw + (Math.random() - 0.5) * 2 * spread;
        double pitch = orientation.pitch + (Math.random() - 0.5) * 2 * spread;

        double x = -Math.sin(yaw) * Math.cos(pitch);
        double y = Math.sin(pitch);
        double z = -Math.cos(yaw) * Math.cos(pitch);

        Vector3d direction = new Vector3d(x, y, z).normalize();
        return direction;
    }

    /**
     * Called when the interaction is first run. This method initializes the shooting process
     * for the player's held firearm.
     *
     * <p>The following steps are performed:</p>
     * <ol>
     *     <li>Retrieves the firearm's statistics and ammunition components.</li>
     *     <li>Stops any ongoing reloading process.</li>
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

        ComponentRefResult<FirearmStatsComponent> statsResult = EnsureEntity.get(interactionContext, FirearmStatsComponent.class, HFF.get().getFirearmStatsComponentType(), interactionContext.getHeldItem().getItemId());
        FirearmStatsComponent stats = statsResult.component();

        ComponentRefResult<AmmoComponent> ammoResult = EnsureEntity.get(interactionContext, AmmoComponent.class, HFF.get().getAmmoComponentType(), "Example_Projectile");
        AmmoComponent ammo = ammoResult.component();

        ammo.setReloading(false);

        float tickRate = Universe.get().getDefaultWorld().getTps();

        double cooldownMs = 1000 * stats.getCooldown();
        double tickMs = 1000 / tickRate;

        int shotsPerTick = (int) Math.floor(tickMs / cooldownMs);

        if (shotsPerTick > 0) {
            for (int i = 0; i < shotsPerTick; i++) {
                if (ammo.useAmmo())
                    this.spawnProjectile(stats, ammo, interactionContext, "Example_Projectile");
            }
        } else {
            if (ammo.useAmmo())
                this.spawnProjectile(stats, ammo, interactionContext, "Example_Projectile");
        }


    }

    /**
     * Spawns a projectile and applies recoil to the player.
     * <p>The following steps are performed:</p>
     * <ol>
     *     <li>Retrieves the projectile configuration from the asset map.</li>
     *     <li>Calculates the projectile's direction and velocity.</li>
     *     <li>Dispatches an {@link OnShoot} event to notify other systems.</li>
     *     <li>Spawns the projectile using the {@link ProjectileModule}.</li>
     *     <li>Applies recoil to the player.</li>
     * </ol>
     *
     * @param stats              The firearm's statistics component.
     * @param ammo               The ammunition component.
     * @param interactionContext The context of the interaction.
     * @param configName         The name of the projectile's configuration.
     */
    private void spawnProjectile(FirearmStatsComponent stats, AmmoComponent ammo, InteractionContext interactionContext, String configName) {
        CommandBuffer<EntityStore> commandBuffer = interactionContext.getCommandBuffer();
        if (commandBuffer == null) {
            interactionContext.getState().state = InteractionState.Failed;
            return;
        }

        Ref<EntityStore> ref = interactionContext.getEntity();

        // TODO: custom ProjectileComponent & insert the name here
        ProjectileConfig config = ProjectileConfig.getAssetMap().getAsset(configName);

        if (config == null) {
            return;
        }

        TransformComponent transform = commandBuffer.getComponent(ref, TransformComponent.getComponentType());
        if (transform == null) return;

        Direction orientation = transform.getSentTransform().lookOrientation;

        assert orientation != null;

        Player player = commandBuffer.getComponent(ref, Player.getComponentType());
        AimComponent aimComponent = commandBuffer.getComponent(ref, HFF.get().getAimComponentType());

        Vector3d direction = getDirection(stats, ammo, aimComponent, orientation);

        if (ConfigManager.isDebugMode()) {
            HFF.get().getLogger().atInfo().log(
                    "Pitch: " + orientation.pitch +
                            "\nYaw: " + orientation.yaw +
                            "\nCalculated direction: " + direction +
                            "\nwith spread " + stats.getSpreadBase() + "° * " + ammo.getSpreadMod() +
                            "\n " + System.currentTimeMillis());
        }

        double baseVelocity = config.getLaunchForce();
        double modifiedVelocity = baseVelocity * ammo.getVelocityMod();

        direction = direction.scale(modifiedVelocity);

        Vector3d position = transform.getPosition().clone();
        position.y += 1.6;

        IEventDispatcher<OnShoot, OnShoot> dispatcher = HytaleServer.get().getEventBus().dispatchFor(OnShoot.class);

        if (dispatcher.hasListener()) {
            OnShoot event = new OnShoot("data");
            dispatcher.dispatch(event);
        }

        if (stats.isDisabled()) return;

        ProjectileModule.get().spawnProjectile(ref, commandBuffer, config, position, direction);

        player.addLocationChange(ref, stats.getHorizontalRecoil(), stats.getVerticalRecoil(), 0, commandBuffer);
    }
}
