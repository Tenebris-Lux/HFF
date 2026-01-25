package lucis.lux.util;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lucis.lux.HFF;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class ConfigManager {
    private static String filePath = "Server/HFF/";
    private static String archivePath = "items/";
    private static String pathInArchive = "item/";
    private static boolean archiveFirst = true;

    private static Path getCurrentArchivePath() {
        try {
            String path = ConfigManager.class.getProtectionDomain().getCodeSource().getLocation().getPath();
            return Paths.get(path).toAbsolutePath();
        } catch (Exception e) {
            HFF.get().getLogger().atSevere().log("Couldn't find the path of the archive running this: " + e.getMessage());
            return null;
        }
    }

    public static void loadConfig() {
        try {
            Path configPath = getCurrentArchivePath();

            if (configPath != null && loadConfigFromZip(configPath)) {
                return;
            }
            configPath = Paths.get("hff_config.json").toAbsolutePath();


            if (Files.exists(configPath)) {
                loadConfigFromFileSystem(configPath);
                return;
            }
            // inside the users JAR
            loadConfigFromJar();
        } catch (Exception e) {
            HFF.get().getLogger().atSevere().log("Error when loading config: " + e.getMessage());
        }
    }

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

    private static void loadConfigFromFileSystem(Path configPath) throws Exception {
        JsonObject config = JsonParser.parseReader(Files.newBufferedReader(configPath)).getAsJsonObject();

        readConfig(config);

        HFF.get().getLogger().atInfo().log("Loaded config from file system: " + configPath.toAbsolutePath());
    }

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

    private static void readConfig(JsonObject config) {
        if (config.has("filePath")) filePath = config.get("filePath").getAsString();
        if (config.has("archivePath")) archivePath = config.get("archivePath").getAsString();
        if (config.has("pathInArchive")) pathInArchive = config.get("pathInArchive").getAsString();
        if (config.has("archiveFirst")) archiveFirst = config.get("archiveFirst").getAsBoolean();
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
