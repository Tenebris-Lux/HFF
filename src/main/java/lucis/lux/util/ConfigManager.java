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
    private static String filePath = "Server/HFF/";
    private static String archivePath = "items/";
    private static String pathInArchive = "item/";
    private static boolean archiveFirst = true;

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

        if (config.has("filePath"))
            filePath = config.get("filePath").getAsString();
        if (config.has("archivePath"))
            archivePath = config.get("archivePath").getAsString();
        if (config.has("pathInArchive"))
            pathInArchive = config.get("pathInArchive").getAsString();
        if (config.has("archiveFirst"))
            archiveFirst = config.get("archiveFirst").getAsBoolean();

        HFF.get().getLogger().atInfo().log("Loaded config from file system: " + configPath.toAbsolutePath());
    }

    private static void loadConfigFromJar() throws Exception {
        try (InputStream is = ConfigManager.class.getResourceAsStream("/hff_config.json")) {
            if (is != null) {
                JsonObject config = JsonParser.parseReader(new InputStreamReader(is)).getAsJsonObject();

                if (config.has("filePath"))
                    filePath = config.get("filePath").getAsString();
                if (config.has("archivePath"))
                    archivePath = config.get("archivePath").getAsString();
                if (config.has("pathInArchive"))
                    pathInArchive = config.get("pathInArchive").getAsString();
                if (config.has("archiveFirst"))
                    archiveFirst = config.get("archiveFirst").getAsBoolean();

                HFF.get().getLogger().atInfo().log("Loaded config from jar.");
            } else {
                throw new Exception("Found no config inside jar.");
            }
        }
    }

    public static String getFilePath() {
        return filePath;
    }

    public static String getArchivePath() {
        return archivePath;
    }

    public static String getPathInArchive() {
        return pathInArchive;
    }

    public static boolean isArchiveFirst() {
        return archiveFirst;
    }
}
