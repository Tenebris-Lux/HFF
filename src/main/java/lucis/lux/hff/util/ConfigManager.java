package lucis.lux.hff.util;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
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
public class ConfigManager {
    /**
     * Default path for configuration files.
     */
    private static String filePath = "Server/HFF/";
    /**
     * Default path for archives
     */
    private static String archivePath = "items/";
    /**
     * Default path inside archives.
     */
    private static String pathInArchive = "item/";
    /**
     * Flag to determine if archives should be checked first for configuration.
     */
    private static boolean archiveFirst = true;
    /**
     * Flag to determine if additional debug messages should be shown.
     */
    private static boolean debugMode = false;

    /**
     * Retrieves the path of the currently running archive (JAR or ZIP).
     *
     * @return The absolute path of the running archive, or {@code null} if an error occurs.
     */
    private static Path getCurrentArchivePath() {
        try {
            String path = ConfigManager.class.getProtectionDomain().getCodeSource().getLocation().getPath();
            return Paths.get(path).toAbsolutePath();
        } catch (Exception e) {
            HFF.get().getLogger().atSevere().log("Couldn't find the path of the archive running this: " + e.getMessage());
            return null;
        }
    }

    /**
     * Loads the configuration from available sources. The method attempts to load the configuration
     * from a ZIP archive, the file system, or the application's JAR resources, in that order.
     *
     * <p>If the configuration is successfully loaded from any source, the method returns early.
     * If no configuration is found, default values are retained.</p>
     */
    public static void loadConfig() {
        try {
            Path configPath = getCurrentArchivePath();

            // Try to load from ZIP archive
            if (configPath != null && loadConfigFromZip(configPath)) {
                return;
            }
            configPath = Paths.get("hff_config.json").toAbsolutePath();

            // Try to load from file system
            if (Files.exists(configPath)) {
                loadConfigFromFileSystem(configPath);
                return;
            }

            // Fallback: Load from JAR resources
            loadConfigFromJar();
        } catch (Exception e) {
            HFF.get().getLogger().atSevere().log("Error when loading config: " + e.getMessage());
        }
    }

    /**
     * Attempts to load the configuration from a ZIP archive.
     *
     * @param configPath The path to the ZIP archive.
     * @return {@code true} if the configuration was successfully loaded, otherwise {@code false}.
     * @throws Exception If an error occurs while reading the ZIP archive.
     */
    private static boolean loadConfigFromZip(Path configPath) throws Exception {
        if (configPath.endsWith(".zip")) {
            try (ZipFile zipFile = new ZipFile(configPath.toString())) {
                ZipEntry configEntry = zipFile.getEntry("hff_config.json");
                if (configEntry != null) {
                    try (InputStream is = zipFile.getInputStream(configEntry)) {
                        JsonObject config = JsonParser.parseReader(new InputStreamReader(is)).getAsJsonObject();

                        readConfig(config);

                        HFF.get().getLogger().atInfo().log("Loaded config from user zip file: " + configPath);
                        return true;
                    }
                } else {
                    HFF.get().getLogger().atWarning().log("Did not find config in user zip file: " + configPath);
                    return false;
                }
            }
        }
        return false;
    }

    /**
     * Loads the configuration from the file system.
     *
     * @param configPath The path to the configuration file.
     * @throws Exception If an error occurs while reading the configuration file.
     */
    private static void loadConfigFromFileSystem(Path configPath) throws Exception {
        JsonObject config = JsonParser.parseReader(Files.newBufferedReader(configPath)).getAsJsonObject();

        readConfig(config);

        HFF.get().getLogger().atInfo().log("Loaded config from file system: " + configPath.toAbsolutePath());
    }

    /**
     * Loads the configuration from the application's JAR resources.
     *
     * @throws Exception If an error occurred while reading the configuration from the JAR.
     */
    private static void loadConfigFromJar() throws Exception {
        try (InputStream is = ConfigManager.class.getResourceAsStream("hff_config.json")) {
            if (is != null) {
                JsonObject config = JsonParser.parseReader(new InputStreamReader(is)).getAsJsonObject();

                readConfig(config);

                HFF.get().getLogger().atInfo().log("Loaded config from jar.");
            } else {
                throw new Exception("Found no config inside jar.");
            }
        }
    }

    /**
     * Reads the configuration values from a JSON object and updates the static fields.
     *
     * @param config The JSON object containing the configuration values.
     */
    private static void readConfig(JsonObject config) {
        if (config.has("filePath")) filePath = config.get("filePath").getAsString();
        if (config.has("archivePath")) archivePath = config.get("archivePath").getAsString();
        if (config.has("pathInArchive")) pathInArchive = config.get("pathInArchive").getAsString();
        if (config.has("archiveFirst")) archiveFirst = config.get("archiveFirst").getAsBoolean();
        if (config.has("debugMode")) debugMode = config.get("debugMode").getAsBoolean();
    }

    /**
     * Returns the current file path for configurations.
     *
     * @return The current file path.
     */
    public static String getFilePath() {
        return filePath;
    }

    /**
     * Returns the current archive path for configurations.
     *
     * @return The current archive path.
     */
    public static String getArchivePath() {
        return archivePath;
    }

    /**
     * Returns the current path inside archives for configurations.
     *
     * @return The current path inside archives.
     */
    public static String getPathInArchive() {
        return pathInArchive;
    }

    /**
     * Returns whether archives should be checked first for configuration.
     *
     * @return {@code true} if archives should be checked first, otherwise {@code false}.
     */
    public static boolean isArchiveFirst() {
        return archiveFirst;
    }

    /**
     * Returns whether debug mode is enabled.
     *
     * @return {@code true} if debug mode is enabled, otherwise {@code false}.
     */
    public static boolean isDebugMode() {
        return debugMode;
    }
}
