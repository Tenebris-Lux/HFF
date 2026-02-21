package lucis.lux.hff;

import com.hypixel.hytale.common.plugin.AuthorInfo;
import com.hypixel.hytale.common.plugin.PluginManifest;
import com.hypixel.hytale.common.semver.Semver;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.component.ResourceType;
import com.hypixel.hytale.server.core.asset.AssetModule;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.Interaction;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import lucis.lux.hff.components.AimComponent;
import lucis.lux.hff.components.AmmoComponent;
import lucis.lux.hff.components.FirearmStatsComponent;
import lucis.lux.hff.data.ConfigManager;
import lucis.lux.hff.data.HFFAssetPackGenerator;
import lucis.lux.hff.interactions.CheckCooldownInteraction;
import lucis.lux.hff.interactions.ReloadInteraction;
import lucis.lux.hff.interactions.ShootFirearmInteraction;
import lucis.lux.hff.interactions.ToggleAimInteraction;
import lucis.lux.hff.storage.RefKeeper;
import lucis.lux.hff.systems.AimSystem;
import lucis.lux.hff.systems.FirearmSystem;
import lucis.lux.hff.systems.ReloadSystem;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

/**
 * The {@code HFF} class is the main plugin class for the hytale Firearm Framework (HFF) plugin.
 * This plugin provides a modular and extensible framework for implementing firearms, crossbows, and other
 * ranged weapons in Hytale.
 *
 * <p>This class is responsible for:</p>
 * <ul>
 *     <li>Registering all necessary components, systems, and interactions for the framework.</li>
 *     <li>Loading and managing configuration  files via {@link ConfigManager}.</li>
 *     <li>Providing access to component types and resources for other parts of the plugin.</li>
 *     <li>Acting as a singleton instance for global access to the plugin's functionality.</li>
 * </ul>
 *
 * <p>The HFF plugin supports the following features:</p>
 * <ul>
 *     <li>Modular weapon system with customizable firearm behaviour.</li>
 *     <li>ECS-based architecture for efficient and scalable gameplay mechanics.</li>
 *     <li>Customizable attachments, physics, and ballistics for realistic weapon simulation.</li>
 *     <li>Support for animations, events, and visual effects.</li>
 * </ul>
 *
 * <p>This plugin is designed to be easily extendable, allowing developers to add new weapon type,
 * behaviours, and mechanics without modifying the core framework.</p>
 *
 * @see JavaPlugin
 * @see ConfigManager
 * @see FirearmStatsComponent
 * @see AimComponent
 * @see AmmoComponent
 * @see FirearmSystem
 * @see AimSystem
 * @see ReloadSystem
 */
public class HFF extends JavaPlugin {

    /**
     * Singleton instance of the HFF plugin.
     */
    private static HFF instance;
    /**
     * Resource type for managing entity references.
     */
    private static ResourceType<EntityStore, RefKeeper> refKeeper;

    /**
     * Component type for firearm statistics.
     */
    private ComponentType<EntityStore, FirearmStatsComponent> firearmStatsComponentType;
    /**
     * Component type for aiming mechanics.
     */
    private ComponentType<EntityStore, AimComponent> aimComponentComponentType;
    /**
     * Component type for ammunition management.
     */
    private ComponentType<EntityStore, AmmoComponent> ammoComponentComponentType;

    /**
     * Constructs a new instance of the HFF plugin.
     *
     * @param init The initialization data for the plugin.
     */
    public HFF(@Nonnull JavaPluginInit init) {
        super(init);
        instance = this;
    }

    /**
     * Returns the resource type for managing entity references.
     *
     * @return The resource type for {@link RefKeeper}
     */
    public static ResourceType<EntityStore, RefKeeper> getRefKeeper() {
        return refKeeper;
    }

    /**
     * Returns the singleton instance of the HFF plugin.
     *
     * @return The HFF plugin instance.
     */
    public static HFF get() {
        return instance;
    }

    /**
     * Sets up the plugin by registering components, systems, and interactions.
     * This method is called during plugin initialization and is responsible for:
     * <ul>
     *     <li>Loading the plugin configuration.</li>
     *     <li>Registering all interactions (e.g., shooting, reloading, aiming).</li>
     *     <li>Registering all components (e.g., firearm stats, aiming, ammunition).</li>
     *     <li>Registering all systems (e.g., firearm logic, aiming logic, reloading logic).</li>
     *     <li>Registering resources for managing entity references.</li>
     *     <li>Generating Hytale-compatible assets.</li>
     * </ul>
     */
    @Override
    protected void setup() {
        // Load the plugin configuration
        ConfigManager.loadConfig();

        // Register interactions
        this.getCodecRegistry(Interaction.CODEC).register("hff:shoot_firearm", ShootFirearmInteraction.class, ShootFirearmInteraction.CODEC);
        this.getCodecRegistry(Interaction.CODEC).register("hff:check_cooldown", CheckCooldownInteraction.class, CheckCooldownInteraction.CODEC);
        this.getCodecRegistry(Interaction.CODEC).register("hff:toggle_aim", ToggleAimInteraction.class, ToggleAimInteraction.CODEC);
        this.getCodecRegistry(Interaction.CODEC).register("hff:reload", ReloadInteraction.class, ReloadInteraction.CODEC);

        // Register components
        this.firearmStatsComponentType = this.getEntityStoreRegistry().registerComponent(FirearmStatsComponent.class, "FirearmStatsComponent", FirearmStatsComponent.CODEC);
        this.getEntityStoreRegistry().registerSystem(new FirearmSystem(this.firearmStatsComponentType));

        this.aimComponentComponentType = this.getEntityStoreRegistry().registerComponent(AimComponent.class, "AimComponent", AimComponent.CODEC);
        this.getEntityStoreRegistry().registerSystem(new AimSystem(this.aimComponentComponentType));

        this.ammoComponentComponentType = this.getEntityStoreRegistry().registerComponent(AmmoComponent.class, "AmmoComponent", AmmoComponent.CODEC);
        this.getEntityStoreRegistry().registerSystem(new ReloadSystem(this.ammoComponentComponentType));

        // Register resources
        refKeeper = this.getEntityStoreRegistry().registerResource(RefKeeper.class, RefKeeper::new);

        // Generate Hytale-compatible assets
        try {
            Path modsDir = Paths.get("mods/");
            Path zipPath = modsDir.resolve("HFF_Assets.zip");
            HFFAssetPackGenerator.generateAssetPack(zipPath.toString(), modsDir.toString());

            if (!Files.exists(zipPath)) {
                throw new IOException("Asset pack has not been created");
            }

            PluginManifest manifest = createManifestForHFFPack();
            if (manifest != null) {
                AssetModule.get().registerPack("hff_assets", zipPath.toAbsolutePath(), manifest, false);
                getLogger().atInfo().log("Registered asset pack: " + zipPath.toAbsolutePath());
            }
        } catch (Exception e) {
            getLogger().atSevere().log("Error generating assets: " + e);
        }
    }

    /**
     * Creates a plugin manifest for the HFF asset pack.
     *
     * @return The plugin manifest for the HFF asset pack.
     */
    private PluginManifest createManifestForHFFPack() {
        PluginManifest manifest = new PluginManifest();
        manifest.setName("HFF Assets");
        manifest.setGroup("HFF");
        manifest.setDescription("Custom assets for the Hytale Firearm Framework");
        AuthorInfo me = new AuthorInfo();
        me.setName("tenebrisLux");
        me.setEmail("tenebrislux.code@gmail.com");
        manifest.setAuthors(List.of(new AuthorInfo[]{me}));
        manifest.setVersion(Semver.fromString("2026.02.18-f3b8fff95"));

        return manifest;
    }

    /**
     * Returns the component type for firearm statistics.
     *
     * @return The component type for {@link FirearmStatsComponent}.
     */
    public ComponentType<EntityStore, FirearmStatsComponent> getFirearmStatsComponentType() {
        return firearmStatsComponentType;
    }

    /**
     * Returns the component type for aiming mechanics.
     *
     * @return The component type for {@link AimComponent}.
     */
    public ComponentType<EntityStore, AimComponent> getAimComponentType() {
        return aimComponentComponentType;
    }

    /**
     * Returns the component type for ammunition management.
     *
     * @return The component type for {@link AmmoComponent}.
     */
    public ComponentType<EntityStore, AmmoComponent> getAmmoComponentType() {
        return ammoComponentComponentType;
    }

    /**
     * Called when the plugin is started.
     * This method can be used to perform additional initialization tasks.
     */
    @Override
    protected void start() {
        super.start();
    }

    /**
     * Called when the plugin is shut down.
     * This method can be used to perform clean-up tasks.
     */
    @Override
    protected void shutdown() {
        super.shutdown();
    }
}