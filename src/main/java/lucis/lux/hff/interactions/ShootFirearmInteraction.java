package lucis.lux.hff.interactions;

import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
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
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import lucis.lux.hff.HFF;
import lucis.lux.hff.components.FirearmStatsComponent;
import lucis.lux.hff.interactions.events.OnShoot;
import lucis.lux.hff.util.ComponentRefResult;
import lucis.lux.hff.util.ConfigManager;
import lucis.lux.hff.util.EnsureEntity;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

public class ShootFirearmInteraction extends SimpleInstantInteraction {
    public static final BuilderCodec<ShootFirearmInteraction> CODEC = BuilderCodec.builder(ShootFirearmInteraction.class, ShootFirearmInteraction::new, SimpleInstantInteraction.CODEC).build();

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

        ComponentRefResult<FirearmStatsComponent> result = EnsureEntity.get(interactionContext, FirearmStatsComponent.class);

        FirearmStatsComponent stats = result.component();


        // TODO: custom ProjectileComponent & insert the name here
        ProjectileConfig config = ProjectileConfig.getAssetMap().getAsset("Example_Projectile");

        if (config == null) {
            return;
        }
        TransformComponent transform = commandBuffer.getComponent(ref, TransformComponent.getComponentType());
        if (transform == null) return;

        Direction orientation = transform.getSentTransform().lookOrientation;

        assert orientation != null;

        double x = -Math.sin(orientation.yaw) * Math.cos(orientation.pitch);
        double y = Math.sin(orientation.pitch);
        double z = -Math.cos(orientation.yaw) * Math.cos(orientation.pitch);

        Vector3d direction = new Vector3d(x, y, z).normalize();

        if (ConfigManager.isDebugMode()) {
            HFF.get().getLogger().atInfo().log("Pitch: " + orientation.pitch);
            HFF.get().getLogger().atInfo().log("Yaw: " + orientation.yaw);
            HFF.get().getLogger().atInfo().log("Calculated direction: " + direction);
        }

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
