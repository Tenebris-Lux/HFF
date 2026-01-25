package lucis.lux;

import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.component.ResourceType;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.Interaction;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import lucis.lux.components.FirearmStatsComponent;
import lucis.lux.interactions.CheckCooldownInteraction;
import lucis.lux.interactions.ShootFirearmInteraction;
import lucis.lux.systems.FirearmSystem;
import lucis.lux.util.ConfigManager;
import lucis.lux.util.RefKeeper;

import javax.annotation.Nonnull;

public class HFF extends JavaPlugin {

    private static HFF instance;
    private ComponentType<EntityStore, FirearmStatsComponent> firearmStatsComponent;
    private static ResourceType<EntityStore, RefKeeper> refKeeper;

    public HFF(@Nonnull JavaPluginInit init) {
        super(init);
        instance = this;
    }

    @Override
    protected void setup() {
        ConfigManager.loadConfig();


        // TODO: register interactions
        this.getCodecRegistry(Interaction.CODEC).register("hff:shoot_firearm", ShootFirearmInteraction.class, ShootFirearmInteraction.CODEC);
        this.getCodecRegistry(Interaction.CODEC).register("hff:checkCooldown", CheckCooldownInteraction.class, CheckCooldownInteraction.CODEC);

        // TODO: register components
        this.firearmStatsComponent = this.getEntityStoreRegistry().registerComponent(FirearmStatsComponent.class, "FirearmStatsComponent", FirearmStatsComponent.CODEC);
        this.getEntityStoreRegistry().registerSystem(new FirearmSystem(this.firearmStatsComponent));

        // Resources
        refKeeper = this.getEntityStoreRegistry().registerResource(RefKeeper.class, RefKeeper::new);
    }

    public ComponentType<EntityStore, FirearmStatsComponent> getFirearmStatsComponentType() {
        return firearmStatsComponent;
    }

    public static ResourceType<EntityStore, RefKeeper> getRefKeeper() {
        return refKeeper;
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