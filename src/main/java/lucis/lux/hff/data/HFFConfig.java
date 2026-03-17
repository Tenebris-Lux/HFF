package lucis.lux.hff.data;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;

/**
 * The {@code HFFConfig} class is responsible for loading and managing configuration settings
 * for the HFF (Hytale Firearm Framework) plugin. It supports loading configurations from various sources,
 * including ZIP archives, the file system, and the application's JAR resource.
 *
 * <p>This class provides methods to load configurations from different sources and read configuration
 * values into static fields for further use. The configuration is used to determine paths for assets,
 * behaviour flags, and other settings required by the framework.</p>
 *
 * <p>If no configuration is found, default values are used.</p>
 */
public class HFFConfig {

    /**
     * The {@link BuilderCodec} for serializing and deserializing this configuration.
     * This codec handles all configuration fields and their default values.
     */
    public static final BuilderCodec<HFFConfig> CODEC = BuilderCodec.builder(HFFConfig.class, HFFConfig::new)
            .append(new KeyedCodec<>("DebugMode", Codec.BOOLEAN), (c, v) -> c.debugMode = v, c -> c.debugMode)
            .add()
            .append(new KeyedCodec<>("DrawTracerLines", Codec.BOOLEAN), (c, v) -> c.drawTracerLines = v, c -> c.drawTracerLines)
            .add()
            .append(new KeyedCodec<>("MaxProjectilesPerTick", Codec.INTEGER), (c, v) -> c.maxProjectilesPerTick = v, c -> c.maxProjectilesPerTick)
            .add()
            .append(new KeyedCodec<>("MaxProjectileLifespan", Codec.FLOAT), (c, v) -> c.maxProjectileLifespan = v, c -> c.maxProjectileLifespan)
            .add()
            .append(new KeyedCodec<>("GlobalDamageMultiplier", Codec.FLOAT), (c, v) -> c.globalDamageMultiplier = v, c -> c.globalDamageMultiplier)
            .add()
            .append(new KeyedCodec<>("GlobalRecoilMultiplier", Codec.FLOAT), (c, v) -> c.globalRecoilMultiplier = v, c -> c.globalRecoilMultiplier)
            .add()
            .append(new KeyedCodec<>("HardcoreMagazineSystem", Codec.BOOLEAN), (c, v) -> c.hardcoreMagazineSystem = v, c -> c.hardcoreMagazineSystem)
            .add()
            .build();

    /**
     * Indicates whether debug mode is enabled. In debug mode, additional logging and visual feedback are provided.
     */
    private boolean debugMode = false;

    /**
     * Indicates whether tracer lines should be drawn for projectiles.
     */
    private boolean drawTracerLines = false;

    /**
     * Indicates whether the hardcore magazine system is enabled. In this system, magazines are managed separately from the firearm.
     */
    private boolean hardcoreMagazineSystem = false;

    /**
     * The maximum number of projectiles that can be processed per tick.
     */
    private int maxProjectilesPerTick = 500;

    /**
     * The maximum lifespan of a projectile in seconds.
     */
    private float maxProjectileLifespan = 5.0f;

    /**
     * A global multiplier for damage dealt by projectiles.
     */
    private float globalDamageMultiplier = 1.0f;

    /**
     * A global multiplier for recoil applied to players.
     */
    private float globalRecoilMultiplier = 1.0f;

    /**
     * Constructs a new {@code HFFConfig} with default values.
     */
    public HFFConfig() {
    }

    /**
     * Returns whether debug mode is enabled.
     *
     * @return {@code true} if debug mode is enabled, {@code false} otherwise.
     */
    public boolean isDebugMode() {
        return debugMode;
    }

    /**
     * Sets whether debug mode is enabled.
     *
     * @param debugMode {@code true} to enable debug mode, {@code false} to disable it.
     */
    public void setDebugMode(boolean debugMode) {
        this.debugMode = debugMode;
    }

    /**
     * Returns whether tracer lines should be drawn for projectiles.
     *
     * @return {@code true} if tracer lines should be drawn, {@code false} otherwise.
     */
    public boolean isDrawTracerLines() {
        return drawTracerLines;
    }

    /**
     * Sets whether tracer lines should be drawn for projectiles.
     *
     * @param drawTracerLines {@code true} to draw tracer lines, {@code false} otherwise.
     */
    public void setDrawTracerLines(boolean drawTracerLines) {
        this.drawTracerLines = drawTracerLines;
    }

    /**
     * Returns the maximum number of projectiles that can be processed per tick.
     *
     * @return The maximum number of projectiles per tick.
     */
    public int getMaxProjectilesPerTick() {
        return maxProjectilesPerTick;
    }

    /**
     * Sets the maximum number of projectiles that can be processed per tick.
     *
     * @param maxProjectilesPerTick The maximum number of projectiles per tick.
     */
    public void setMaxProjectilesPerTick(int maxProjectilesPerTick) {
        this.maxProjectilesPerTick = maxProjectilesPerTick;
    }

    /**
     * Returns the maximum lifespan of a projectile in seconds.
     *
     * @return The maximum lifespan of a projectile.
     */
    public float getMaxProjectileLifespan() {
        return maxProjectileLifespan;
    }

    /**
     * Sets the maximum lifespan of a projectile in seconds.
     *
     * @param maxProjectileLifespan The maximum lifespan of a projectile.
     */
    public void setMaxProjectileLifespan(float maxProjectileLifespan) {
        this.maxProjectileLifespan = maxProjectileLifespan;
    }

    /**
     * Returns the global multiplier for damage dealt by projectiles.
     *
     * @return The global damage multiplier.
     */
    public float getGlobalDamageMultiplier() {
        return globalDamageMultiplier;
    }

    /**
     * Sets the global multiplier for damage dealt by projectiles.
     *
     * @param globalDamageMultiplier The global damage multiplier.
     */
    public void setGlobalDamageMultiplier(float globalDamageMultiplier) {
        this.globalDamageMultiplier = globalDamageMultiplier;
    }

    /**
     * Returns the global multiplier for recoil applied to players.
     *
     * @return The global recoil multiplier.
     */
    public float getGlobalRecoilMultiplier() {
        return globalRecoilMultiplier;
    }

    /**
     * Sets the global multiplier for recoil applied to players.
     *
     * @param globalRecoilMultiplier The global recoil multiplier.
     */
    public void setGlobalRecoilMultiplier(float globalRecoilMultiplier) {
        this.globalRecoilMultiplier = globalRecoilMultiplier;
    }

    /**
     * Returns whether the hardcore magazine system is enabled.
     *
     * @return {@code true} if the hardcore magazine system is enabled, {@code false} otherwise.
     */
    public boolean isHardcoreMagazineSystem() {
        return hardcoreMagazineSystem;
    }

    /**
     * Sets whether the hardcore magazine system is enabled.
     *
     * @param hardcoreMagazineSystem {@code true} to enable the hardcore magazine system, {@code false} to disable it.
     */
    public void setHardcoreMagazineSystem(boolean hardcoreMagazineSystem) {
        this.hardcoreMagazineSystem = hardcoreMagazineSystem;
    }
}
