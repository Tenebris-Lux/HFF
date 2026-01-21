package lucis.lux.util;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lucis.lux.HFF;
import lucis.lux.components.FirearmStatsComponent;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FirearmStatsLoader {

    public static FirearmStatsComponent loadStatsFromResource(String itemId) {
        return loadStatsFromResource(itemId, "HFF/");
    }

    public static FirearmStatsComponent loadStatsFromResource(String itemId, String basePath) {
        String fileName = itemId.replace(':', '_') + ".json";
        Path filePath = Paths.get(basePath, fileName);

        InputStream inputStream = FirearmStatsLoader.class.getResourceAsStream("/" + filePath);

        if (inputStream == null) {
            HFF.get().getLogger().atSevere().log("Resource not found: " + filePath);
            return new FirearmStatsComponent();
        }

        try (Reader reader = new InputStreamReader(inputStream)) {
            JsonObject jsonObject = JsonParser.parseReader(reader).getAsJsonObject();
            FirearmStatsComponent stats = new FirearmStatsComponent();

            if (jsonObject.has("HorizontalRecoil"))
                stats.setHorizontalRecoil(jsonObject.get("HorizontalRecoil").getAsDouble());
            if (jsonObject.has("JamChance"))
                stats.setJamChance(jsonObject.get("JamChance").getAsDouble());
            if (jsonObject.has("MisfireChance"))
                stats.setMisfireChance(jsonObject.get("MisfireChance").getAsDouble());
            if (jsonObject.has("MovementPenalty"))
                stats.setMovementPenalty(jsonObject.get("MovementPenalty").getAsDouble());
            if (jsonObject.has("ProjectileAmount"))
                stats.setProjectileAmount(jsonObject.get("ProjectileAmount").getAsInt());
            if (jsonObject.has("ProjectileVelocity"))
                stats.setProjectileVelocity(jsonObject.get("ProjectileVelocity").getAsDouble());
            if (jsonObject.has("RPM"))
                stats.setRpm(jsonObject.get("RPM").getAsDouble());
            if (jsonObject.has("SpreadBase"))
                stats.setSpreadBase(jsonObject.get("SpreadBase").getAsDouble());
            if (jsonObject.has("VerticalRecoil"))
                stats.setVerticalRecoil(jsonObject.get("VerticalRecoil").getAsDouble());

            HFF.get().getLogger().atInfo().log("Loaded FirearmStats successfully from " + fileName);
            return stats;
        } catch (Exception e) {
            HFF.get().getLogger().atSevere().log("Error while loading FirearmStats from " + fileName + ": " + e.getMessage());
            return new FirearmStatsComponent();
        }
    }
}
