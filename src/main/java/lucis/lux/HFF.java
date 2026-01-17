package lucis.lux;

import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.Interaction;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import lucis.lux.components.FirearmStatsComponent;
import lucis.lux.interactions.ShootFirearm;
import lucis.lux.systems.FirearmSystem;

import javax.annotation.Nonnull;

public class HFF extends JavaPlugin {

    private static HFF instance;
    private ComponentType<EntityStore, FirearmStatsComponent> firearmStatsComponent;
    private static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();

    public HFF(@Nonnull JavaPluginInit init) {
        super(init);
        instance = this;
    }

    @Override
    protected void setup() {
        // TODO: register components

        this.getCodecRegistry(Interaction.CODEC).register("hff:shoot_firearm", ShootFirearm.class, ShootFirearm.CODEC);

        this.firearmStatsComponent = this.getEntityStoreRegistry().registerComponent(FirearmStatsComponent.class, FirearmStatsComponent::new);
        this.getEntityStoreRegistry().registerSystem(new FirearmSystem(this.firearmStatsComponent));
    }

    public ComponentType<EntityStore, FirearmStatsComponent> getFirearmStatsComponent() {
        return firearmStatsComponent;
    }

    public static HFF get() {
        return instance;
    }

    @Override
    protected void start() {
        super.start();
    }

    @Override
    protected void shutdown() {
        super.shutdown();
    }
}