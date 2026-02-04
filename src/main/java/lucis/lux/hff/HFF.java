package lucis.lux.hff;

import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.component.ResourceType;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.Interaction;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import lucis.lux.hff.components.AimComponent;
import lucis.lux.hff.components.FirearmStatsComponent;
import lucis.lux.hff.interactions.ToggleAimInteraction;
import lucis.lux.hff.interactions.CheckCooldownInteraction;
import lucis.lux.hff.interactions.ShootFirearmInteraction;
import lucis.lux.hff.systems.AimSystem;
import lucis.lux.hff.systems.FirearmSystem;
import lucis.lux.hff.util.ConfigManager;
import lucis.lux.hff.util.RefKeeper;

import javax.annotation.Nonnull;

public class HFF extends JavaPlugin {

    private static HFF instance;
    private ComponentType<EntityStore, FirearmStatsComponent> firearmStatsComponentType;
    private ComponentType<EntityStore, AimComponent> aimComponentComponentType;
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
        this.getCodecRegistry(Interaction.CODEC).register("hff:toggle_aim", ToggleAimInteraction.class, ToggleAimInteraction.CODEC);

        // TODO: register components
        this.firearmStatsComponentType = this.getEntityStoreRegistry().registerComponent(FirearmStatsComponent.class, "FirearmStatsComponent", FirearmStatsComponent.CODEC);
        this.getEntityStoreRegistry().registerSystem(new FirearmSystem(this.firearmStatsComponentType));

        this.aimComponentComponentType = this.getEntityStoreRegistry().registerComponent(AimComponent.class, "AimComponent", AimComponent.CODEC);
        this.getEntityStoreRegistry().registerSystem(new AimSystem(this.aimComponentComponentType));
        // Resources
        refKeeper = this.getEntityStoreRegistry().registerResource(RefKeeper.class, RefKeeper::new);
    }

    public ComponentType<EntityStore, FirearmStatsComponent> getFirearmStatsComponentType() {
        return firearmStatsComponentType;
    }

    public ComponentType<EntityStore, AimComponent> getAimComponentType() {
        return aimComponentComponentType;
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