package lucis.lux.hff.data;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import lucis.lux.hff.HFF;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * The {@code ConfigManager} class is responsible for loading and managing configuration settings
 * for the HFF (Hytale Firearm Framework) plugin. It supports loading configurations from various sources,
 * including ZIP archives, the file system, and the application's JAR resource.
 *
 * <p>This class provides methods to load configurations from different sources and read configuration
 * values into static fields for further use. The configuration is used to determine paths for assets,
 * behaviour flags, and other settings required by the framework.</p>
 *
 * <p>The configuration is loaded in the following order:</p>
 * <ol>
 *     <li>From the running ZIP archive (if applicable)</li>
 *     <li>From the file system (if a configuration file exists).</li>
 *     <li>From the application's JAR resources (as a failback).</li>
 * </ol>
 *
 * <p>If no configuration is found, default values are used.</p>
 */
public class HFFConfig {

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
            .build();

    private boolean debugMode = false;
    private boolean drawTracerLines = false;
    private int maxProjectilesPerTick = 500;
    private float maxProjectileLifespan = 5.0f;
    private float globalDamageMultiplier = 1.0f;
    private float globalRecoilMultiplier = 1.0f;

    public HFFConfig(){}

    public boolean isDebugMode() {
        return debugMode;
    }

    public void setDebugMode(boolean debugMode) {
        this.debugMode = debugMode;
    }

    public boolean isDrawTracerLines() {
        return drawTracerLines;
    }

    public void setDrawTracerLines(boolean drawTracerLines) {
        this.drawTracerLines = drawTracerLines;
    }

    public int getMaxProjectilesPerTick() {
        return maxProjectilesPerTick;
    }

    public void setMaxProjectilesPerTick(int maxProjectilesPerTick) {
        this.maxProjectilesPerTick = maxProjectilesPerTick;
    }

    public float getMaxProjectileLifespan() {
        return maxProjectileLifespan;
    }

    public void setMaxProjectileLifespan(float maxProjectileLifespan) {
        this.maxProjectileLifespan = maxProjectileLifespan;
    }

    public float getGlobalDamageMultiplier() {
        return globalDamageMultiplier;
    }

    public void setGlobalDamageMultiplier(float globalDamageMultiplier) {
        this.globalDamageMultiplier = globalDamageMultiplier;
    }

    public float getGlobalRecoilMultiplier() {
        return globalRecoilMultiplier;
    }

    public void setGlobalRecoilMultiplier(float globalRecoilMultiplier) {
        this.globalRecoilMultiplier = globalRecoilMultiplier;
    }
}
