package lucis.lux.util;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lucis.lux.HFF;
import lucis.lux.components.FirearmStatsComponent;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FirearmStatsLoader {

    public static FirearmStatsComponent loadStatsFromResource(String itemId) {
        String fileName = itemId.replace(':', '_') + ".json";

        FirearmStatsComponent stats;

        if (ConfigManager.isUseJarResources()) {
            stats = tryLoadFromJar(ConfigManager.getFallbackPath() + fileName);
            if (stats != null) {
                return stats;
            }
        }

        stats = tryLoadFromFileSystem(ConfigManager.getStatsPath(), fileName);
        if (stats != null) {
            return stats;
        }

        HFF.get().getLogger().atSevere().log("Couldn't find Stats for: " + itemId);
        return new FirearmStatsComponent();
    }

    private static FirearmStatsComponent tryLoadFromJar(String resourcePath) {

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
        return null;
    }

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

    private static FirearmStatsComponent setStatsFromJson(JsonObject jsonObject) {

        FirearmStatsComponent stats = new FirearmStatsComponent();

        stats.setRpm(getDoubleOrDefault(jsonObject, "RPM", 500.0));
        stats.setProjectileVelocity(getDoubleOrDefault(jsonObject, "ProjectileVelocity", 10.0));
        stats.setProjectileAmount(getIntOrDefault(jsonObject, "ProjectileAmount", 1));
        stats.setSpreadBase(getDoubleOrDefault(jsonObject, "SpreadBase", 0.1));
        stats.setMovementPenalty(getDoubleOrDefault(jsonObject, "MovementPenalty", 0.5));
        stats.setMisfireChance(getDoubleOrDefault(jsonObject, "MisfireChance", 0.01));
        stats.setJamChance(getDoubleOrDefault(jsonObject, "JamChance", 0.005));
        stats.setVerticalRecoil(getDoubleOrDefault(jsonObject, "VerticalRecoil", 0.5));
        stats.setHorizontalRecoil(getDoubleOrDefault(jsonObject, "HorizontalRecoil", 0.5));

        return stats;
    }

    private static double getDoubleOrDefault(JsonObject jsonObject, String key, double defaultValue) {
        return jsonObject.has(key) ? jsonObject.get(key).getAsDouble() : defaultValue;
    }

    private static int getIntOrDefault(JsonObject jsonObject, String key, int defaultValue) {
        return jsonObject.has(key) ? jsonObject.get(key).getAsInt() : defaultValue;
    }


}
