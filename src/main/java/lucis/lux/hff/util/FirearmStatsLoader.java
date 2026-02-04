package lucis.lux.hff.util;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lucis.lux.hff.components.FirearmStatsComponent;
import lucis.lux.hff.components.enums.FireMode;
import lucis.lux.hff.components.enums.FirearmClass;
import lucis.lux.hff.components.enums.FirearmType;
import lucis.lux.hff.HFF;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * The {@code FirearmStatsLoader} class is responsible for loading firearm statistics
 * from various sources such as ZIP archives, JAR resources, and the file system.
 * It uses the configuration provided by the {@link ConfigManager} to determine the paths
 * and the order in which to search for the statistics.
 */
public class FirearmStatsLoader {

    /**
     * Attempts to load firearm statistics for the given item ID from available sources.
     * The method tries to load the statistics from a ZIP archive, the file system, or a JAR resource,
     * depending on the configuration.
     *
     * @param itemId The ID of thee item for which to load the statistics.
     * @return A {@link FirearmStatsComponent} instance containing the loaded statistics,
     * or the new instance with default values if no statistics where found.
     */
    public static FirearmStatsComponent loadStatsFromResource(String itemId) {
        String fileName = itemId.replace(':', '_') + ".json";

        FirearmStatsComponent stats;

        if (ConfigManager.isArchiveFirst()) {
            stats = tryLoadFromArchive(ConfigManager.getArchivePath(), ConfigManager.getPathInArchive() + fileName);
            if (stats != null) {
                return stats;
            }
        }

        stats = tryLoadFromFileSystem(ConfigManager.getFilePath(), fileName);
        if (stats != null) {
            return stats;
        }

        if (!ConfigManager.isArchiveFirst()) {
            stats = tryLoadFromArchive(ConfigManager.getArchivePath(), ConfigManager.getPathInArchive() + fileName);
            if (stats != null) {
                return stats;
            }
        }

        HFF.get().getLogger().atSevere().log("Couldn't find Stats for: " + itemId);
        return new FirearmStatsComponent();
    }

    /**
     * Attempts to load the firearm statistics from a ZIP or JAR archive.
     *
     * @param resourcePath The path to the archive.
     * @param entryPath    The path to the entry within the archive.
     * @return A {@link FirearmStatsComponent} instance containing the loaded statistics,
     * or {@code null} if the entry was not found or an error occurred.
     */
    private static FirearmStatsComponent tryLoadFromArchive(String resourcePath, String entryPath) {
        if (resourcePath.endsWith(".zip")) {
            try (ZipFile zipFile = new ZipFile(resourcePath)) {
                ZipEntry entry = zipFile.getEntry(entryPath);
                if (entry == null) {
                    HFF.get().getLogger().atSevere().log("Didn't find entry: " + entryPath);
                    return null;
                }
                try (InputStream is = zipFile.getInputStream(entry)) {
                    InputStreamReader isr = new InputStreamReader(is);
                    BufferedReader reader = new BufferedReader(isr);
                    JsonObject jsonObject = JsonParser.parseReader(reader).getAsJsonObject();

                    FirearmStatsComponent stats = setStatsFromJson(jsonObject);
                    HFF.get().getLogger().atInfo().log("Loaded stats from ZIP: " + entryPath);
                    return stats;
                }
            } catch (Exception e) {
                HFF.get().getLogger().atSevere().log("Error while loading from ZIP: " + e.getMessage());
                return null;
            }
        } else if (resourcePath.endsWith(".jar")) {
            try (InputStream is = FirearmStatsLoader.class.getResourceAsStream("/" + resourcePath)) {
                if (is != null) {
                    JsonObject jsonObject = JsonParser.parseReader(new InputStreamReader(is)).getAsJsonObject();

                    FirearmStatsComponent stats = setStatsFromJson(jsonObject);
                    HFF.get().getLogger().atInfo().log("Loaded stats from JAR: " + resourcePath);
                    return stats;
                }
            } catch (Exception e) {
                HFF.get().getLogger().atSevere().log("Error while loading stats from JAR: " + e.getMessage());
            }
        }
        return null;
    }

    /**
     * Attempts to load firearm statistics from the file system.
     *
     * @param basePath The base path where the statistics file is located.
     * @param fileName The name of the statistics file.
     * @return A {@link FirearmStatsComponent} instance containing the loaded statistics,
     * or {@code null} if the file was not found or an error occurred.
     */
    private static FirearmStatsComponent tryLoadFromFileSystem(String basePath, String fileName) {

        Path filePath = Paths.get(basePath, fileName);
        HFF.get().getLogger().atInfo().log("Searching for Stats-File: " + filePath.toAbsolutePath());

        if (!Files.exists(filePath)) {
            HFF.get().getLogger().atSevere().log("Did not find Stats-File: " + filePath.toAbsolutePath());
            return null;
        }

        try (Reader reader = Files.newBufferedReader(filePath)) {
            JsonObject jsonObject = JsonParser.parseReader(reader).getAsJsonObject();

            FirearmStatsComponent stats = setStatsFromJson(jsonObject);

            HFF.get().getLogger().atInfo().log("Loaded stats from File system: " + filePath.toAbsolutePath());
            return stats;
        } catch (Exception e) {
            HFF.get().getLogger().atSevere().log("Error while reading stats from file system: " + e.getMessage());
            return null;
        }
    }

    /**
     * Sets the firearm statistics from a JSON object.
     *
     * @param jsonObject The JSON object containing the statistics.
     * @return A {@link FirearmStatsComponent} instance with the statistics set.
     */
    private static FirearmStatsComponent setStatsFromJson(JsonObject jsonObject) {

        FirearmStatsComponent stats = new FirearmStatsComponent();

        stats.setRpm(getDoubleOrDefault(jsonObject, "RPM"));
        stats.setProjectileVelocity(getDoubleOrDefault(jsonObject, "ProjectileVelocity"));
        stats.setProjectileAmount(getIntOrDefault(jsonObject, "ProjectileAmount"));
        stats.setSpreadBase(getDoubleOrDefault(jsonObject, "SpreadBase"));
        stats.setMovementPenalty(getDoubleOrDefault(jsonObject, "MovementPenalty"));
        stats.setMisfireChance(getDoubleOrDefault(jsonObject, "MisfireChance"));
        stats.setJamChance(getDoubleOrDefault(jsonObject, "JamChance"));
        stats.setVerticalRecoil(getDoubleOrDefault(jsonObject, "VerticalRecoil"));
        stats.setHorizontalRecoil(getDoubleOrDefault(jsonObject, "HorizontalRecoil"));
        stats.setDisabled(getBoolOrDefault(jsonObject, "Disabled"));
        stats.setFirearmClass((FirearmClass) getEnumOrDefault(jsonObject, "Class"));
        stats.setFirearmType((FirearmType) getEnumOrDefault(jsonObject, "Type"));
        stats.setFireMode((FireMode) getEnumOrDefault(jsonObject, "Mode"));

        return stats;
    }

    /**
     * Retrieves a double value from a JSON object, or returns a default value if the key is not present.
     *
     * @param jsonObject The JSON object to retrieve the value from.
     * @param key        The key of the value to retrieve.
     * @return The double value associated with the key, or -1.0 if the key is not present.
     */
    private static double getDoubleOrDefault(JsonObject jsonObject, String key) {
        return jsonObject.has(key) ? jsonObject.get(key).getAsDouble() : -1.0;
    }

    /**
     * Retrieves an integer value from a JSON object, or returns a default value if the key is not present.
     *
     * @param jsonObject The JSON object to retrieve the value from.
     * @param key        The key of the value to retrieve.
     * @return The integer value associated with the key, or -1 if the key is not present.
     */
    private static int getIntOrDefault(JsonObject jsonObject, String key) {
        return jsonObject.has(key) ? jsonObject.get(key).getAsInt() : -1;
    }

    /**
     * Retrieves a boolean value from a JSON object, or returns a default value if the key is not present.
     *
     * @param jsonObject The JSON object to retrieve the value from.
     * @param key        The key of the value to retrieve.
     * @return The boolean value associated with the key, or {@code false} if the key is not present.
     */
    private static boolean getBoolOrDefault(JsonObject jsonObject, String key) {
        return jsonObject.has(key) && jsonObject.get(key).getAsBoolean();
    }

    /**
     * Retrieves a enum value from a JSON object, or returns a default value if the key is not present.
     *
     * @param jsonObject The JSON object to retrieve the value from.
     * @param key        The key of the value to retrieve.
     * @return The enum value associated with the key, or {@code null} if the key is not present.
     */
    private static Object getEnumOrDefault(JsonObject jsonObject, String key) {
        if (jsonObject.has(key)) {
            return switch (key) {
                case "Class" -> FirearmClass.valueOf(jsonObject.get(key).getAsString());
                case "Type" -> FirearmType.valueOf(jsonObject.get(key).getAsString());
                case "Mode" -> FireMode.valueOf(jsonObject.get(key).getAsString());
                default -> null;
            };
        }
        return switch (key) {
            case "Class" -> FirearmClass.OTHER;
            case "Type" -> FirearmType.OTHER;
            case "Mode" -> FireMode.OTHER;
            default -> null;
        };
    }


}
