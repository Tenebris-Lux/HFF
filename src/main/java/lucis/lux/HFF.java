package lucis.lux;

import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.Interaction;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import lucis.lux.components.FirearmStatsComponent;
import lucis.lux.interactions.CheckCooldownInteraction;
import lucis.lux.interactions.ShootFirearmInteraction;
import lucis.lux.systems.FirearmSystem;
import lucis.lux.util.ConfigManager;

import javax.annotation.Nonnull;

public class HFF extends JavaPlugin {

    private static HFF instance;
    private ComponentType<EntityStore, FirearmStatsComponent> firearmStatsComponent;

    public HFF(@Nonnull JavaPluginInit init) {
        super(init);
        instance = this;
    }

    @Override
    protected void setup() {
        ConfigManager.loadConfig();

        // TODO: register components

        this.getCodecRegistry(Interaction.CODEC).register("hff:shoot_firearm", ShootFirearmInteraction.class, ShootFirearmInteraction.CODEC);
        this.getCodecRegistry(Interaction.CODEC).register("hff:checkCooldown", CheckCooldownInteraction.class, CheckCooldownInteraction.CODEC);

        this.firearmStatsComponent = this.getEntityStoreRegistry().registerComponent(FirearmStatsComponent.class, "Firearm", FirearmStatsComponent.CODEC);
        this.getEntityStoreRegistry().registerSystem(new FirearmSystem(this.firearmStatsComponent));

        // this.getEventRegistry().registerGlobal(AfterLoadedAssetsEvent.class, AfterLoadedAssetsEvent::onAssetsLoaded);
    }

    public ComponentType<EntityStore, FirearmStatsComponent> getFirearmStatsComponentType() {
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