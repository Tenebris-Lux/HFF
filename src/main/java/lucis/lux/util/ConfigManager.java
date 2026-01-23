package lucis.lux.util;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lucis.lux.HFF;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ConfigManager {
    private static String statsPath = "Server/HFF/";
    private static String fallbackPath = "items/";
    private static boolean useJarResources = true;

    public static void loadConfig() {
        try {
            Path configPath = Paths.get("hff_config.json");

            if (Files.exists(configPath)) {
                loadConfigFromFileSystem(configPath);
                return;
            }

            loadConfigFromJar();
        } catch (Exception e) {
            HFF.get().getLogger().atSevere().log("Error when loading config: " + e.getMessage());
        }
    }

    private static void loadConfigFromFileSystem(Path configPath) throws Exception {
        JsonObject config = JsonParser.parseReader(Files.newBufferedReader(configPath)).getAsJsonObject();

        if (config.has("stats_path"))
            statsPath = config.get("stats_path").getAsString();
        if (config.has("fallback_path"))
            fallbackPath = config.get("fallback_path").getAsString();
        if (config.has("use_jar_resources"))
            useJarResources = config.get("use_jar_resources").getAsBoolean();

        HFF.get().getLogger().atInfo().log("Loaded config from file system: " + configPath.toAbsolutePath());
    }

    private static void loadConfigFromJar() throws Exception {
        try (InputStream is = ConfigManager.class.getResourceAsStream("/config.json")) {
            if (is != null) {
                JsonObject config = JsonParser.parseReader(new InputStreamReader(is)).getAsJsonObject();

                if (config.has("stats_path"))
                    statsPath = config.get("stats_path").getAsString();
                if (config.has("fallback_path"))
                    fallbackPath = config.get("fallback_path").getAsString();
                if (config.has("use_jar_resources"))
                    useJarResources = config.get("use_jar_resources").getAsBoolean();

                HFF.get().getLogger().atInfo().log("Loaded config from jar.");
            } else {
                throw new Exception("Found no config inside jar.");
            }
        }
    }

    public static String getStatsPath() {
        return statsPath;
    }

    public static String getFallbackPath() {
        return fallbackPath;
    }

    public static boolean isUseJarResources() {
        return useJarResources;
    }
}
