package lucis.lux.hff.data;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lucis.lux.hff.HFF;
import lucis.lux.hff.enums.FireMode;
import lucis.lux.hff.enums.FirearmClass;
import lucis.lux.hff.enums.FirearmType;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.*;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * The {@code HFFAssetPackGenerator} class is responsible for generating asset packs for the HFF (Hytale Firearm Framework) plugin.
 * This class scans JAR and ZIP files in specified directories for asset files, processes them, and generates a new asset pack
 * that is compatible with Hytale. It also registers firearm and ammunition data in the respective registries.
 *
 * <p>This class performs the following tasks:</p>
 * <ul>
 *     <li>Creates a new ZIP file system for the output asset pack.</li>
 *     <li>Scans specified directories for JAR and ZIP files containing assets.</li>
 *     <li>Processes each asset file, removing HFF-specific fields to ensure compatibility with Hytale.</li>
 *     <li>Copies asset files (models, textures, icons) into the new asset pack.</li>
 *     <li>Registers firearm and ammunition data in the {@link FirearmRegistry} and {@link AmmoRegistry}.</li>
 * </ul>
 *
 * <p>This class is typically used at runtime to generate asset packs from mod files.</p>
 */
public class HFFAssetPackGenerator {

    /**
     * Generates an asset pack by scanning the specified directories for JAR and ZIP files containing assets.
     * The generated asset pack is saved to the specified output path.
     *
     * @param outputPackPath The path where the generated asset pack will be saved.
     * @param modsDir        The directory to scan for JAT and ZIP files containing assets.
     */
    public static void generateAssetPack(String outputPackPath, String modsDir) {
        try {
            Path outputPath = Paths.get(outputPackPath);
            Path parentDir = outputPath.getParent();
            if (parentDir != null && !Files.exists(parentDir)) {
                Files.createDirectories(parentDir);
            }

            if (Files.exists(outputPath)) {
                Files.delete(outputPath);
            }

            try (FileSystem zipFs = createNewZip(outputPackPath)) {
                scanModsDirectory(zipFs, Paths.get("../../Mods/").toString());
                scanModsDirectory(zipFs, modsDir);
            }
        } catch (Exception e) {
            HFF.get().getLogger().atSevere().log("Error creating asset pack: " + e.getMessage());
        }
    }

    /**
     * Scans the specified directory for JAR and ZIP files and processes each file found.
     *
     * @param zipFs   The file system of the output ZIP file.
     * @param modsDir The directory to scan for JAT and ZIP files.
     */
    private static void scanModsDirectory(FileSystem zipFs, String modsDir) {
        Path modsPath = Paths.get(modsDir);
        if (Files.exists(modsPath) && Files.isDirectory(modsPath)) {
            try (DirectoryStream<Path> stream = Files.newDirectoryStream(modsPath, "*.{jar,zip}")) {
                for (Path jarPath : stream) {
                    HFF.get().getLogger().atInfo().log("At " + jarPath.toAbsolutePath());
                    scanJarForAssets(zipFs, jarPath.toString());
                }
            } catch (Exception e) {
                HFF.get().getLogger().atSevere().log("Error scanning mod folder: " + e.getMessage());
            }
        } else {
            HFF.get().getLogger().atSevere().log("Did mot find mods folder: " + modsDir);
        }
    }

    /**
     * Scan the specified JAR file for asset files and processes each asset found.
     *
     * @param zipFs   The file system of the output ZIP file.
     * @param jarPath The path to the JAR file to scan.
     */
    private static void scanJarForAssets(FileSystem zipFs, String jarPath) {
        try (ZipFile jarFile = new ZipFile(jarPath)) {
            Enumeration<? extends ZipEntry> entries = jarFile.entries();
            while (entries.hasMoreElements()) {
                ZipEntry entry = entries.nextElement();
                if (entry.getName().contains("Items/") && entry.getName().endsWith(".json")) {
                    String entryPath = entry.getName();
                    String itemName = entryPath.substring(entryPath.lastIndexOf('/') + 1, entryPath.lastIndexOf('.'));
                    processAssetFromJar(zipFs, jarFile, entryPath, itemName);
                }
            }
        } catch (Exception e) {
            HFF.get().getLogger().atSevere().log("Error scanning JAR " + jarPath + ": " + e.getMessage());
        }
    }

    /**
     * Processes an asset file from a JAR file, generating a Hytale-compatible asset file and copying referenced assets.
     *
     * @param zipFs     The file system of the output ZIP file.
     * @param jarFile   The JAT file containing the asset.
     * @param entryPath The path to the asset file within the JAR.
     * @param itemName  The name of the item associated with the asset.
     */
    private static void processAssetFromJar(FileSystem zipFs, ZipFile jarFile, String entryPath, String itemName) {
        try {
            JsonObject combinedAsset;
            try (InputStream is = jarFile.getInputStream(jarFile.getEntry(entryPath))) {
                combinedAsset = JsonParser.parseReader(new InputStreamReader(is)).getAsJsonObject();
            }

            JsonObject hytaleAsset = removeHFFFields(combinedAsset);

            Path assetPathInZip = zipFs.getPath("/Server/Item/Items/" + itemName + ".json");
            Files.createDirectories(assetPathInZip.getParent());
            Files.writeString(assetPathInZip, hytaleAsset.toString());

            copyAssetFromJar(zipFs, jarFile, combinedAsset.get("Model").getAsString(), "Common/" + combinedAsset.get("Model").getAsString());
            copyAssetFromJar(zipFs, jarFile, combinedAsset.get("Texture").getAsString(), "Common/" + combinedAsset.get("Texture").getAsString());
            copyAssetFromJar(zipFs, jarFile, combinedAsset.get("Icon").getAsString(), "Common/" + combinedAsset.get("Icon").getAsString());

            registerHFFData(itemName, combinedAsset);

            HFF.get().getLogger().atInfo().log("Created asset for: " + itemName);
        } catch (Exception e) {
            HFF.get().getLogger().atSevere().log("Error processing " + itemName + ": " + e.getMessage());
        }
    }

    /**
     * Registers firearm and ammunition data from the asset file in the respective registries.
     *
     * @param itemName      The name of the item associated with the asset.
     * @param combinedAsset The combined asset data.
     */
    private static void registerHFFData(String itemName, JsonObject combinedAsset) {
        if (combinedAsset.has("Components")) {
            JsonObject components = combinedAsset.getAsJsonObject("Components");
            if (components.has("hff:firearm_stats")) {
                JsonObject statsJson = components.getAsJsonObject("hff:firearm:stats");
                FirearmStats stats = loadFirearmStats(statsJson);
                FirearmRegistry.register(itemName, stats);
            }

            if (components.has("hff:ammo")) {
                JsonObject ammoJson = components.getAsJsonObject("hff:ammo");
                AmmoData data = loadAmmoData(ammoJson);
                AmmoRegistry.register(itemName, data);
            }
        }
    }

    /**
     * Loads firearm statistics from a JSON object and creates a {@link FirearmStats} object.
     *
     * @param statsJson The JSON object containing the firearm statistics.
     * @return A {@link FirearmStats} object created from the JSON data.
     */
    private static FirearmStats loadFirearmStats(JsonObject statsJson) {
        FirearmStats.Builder builder = FirearmStats.builder();
        if (statsJson.has("reloadTime")) builder.reloadTime(statsJson.get("reloadTime").getAsFloat());
        if (statsJson.has("rpm")) builder.rpm(statsJson.get("rpm").getAsFloat());
        if (statsJson.has("projectileVelocity"))
            builder.projectileVelocity(statsJson.get("projectileVelocity").getAsFloat());
        if (statsJson.has("projectileAmount")) builder.projectileAmount(statsJson.get("projectileAmount").getAsInt());
        if (statsJson.has("projectileCapacity"))
            builder.projectileCapacity(statsJson.get("projectileCapacity").getAsInt());
        if (statsJson.has("spreadBase")) builder.spreadBase(statsJson.get("spreadBase").getAsFloat());
        if (statsJson.has("movementPenalty")) builder.movementPenalty(statsJson.get("movementPenalty").getAsFloat());
        if (statsJson.has("misfireChance")) builder.misfireChance(statsJson.get("misfireChance").getAsFloat());
        if (statsJson.has("jamChance")) builder.jamChance(statsJson.get("jamChance").getAsFloat());
        if (statsJson.has("verticalRecoil")) builder.verticalRecoil(statsJson.get("verticalRecoil").getAsFloat());
        if (statsJson.has("horizontalRecoil")) builder.horizontalRecoil(statsJson.get("horizontalRecoil").getAsFloat());
        if (statsJson.has("firearmClass"))
            builder.firearmClass(FirearmClass.valueOf(statsJson.get("firearmClass").getAsString()));
        if (statsJson.has("firearmType"))
            builder.firearmType(FirearmType.valueOf(statsJson.get("firearmType").getAsString()));
        if (statsJson.has("fireMode")) builder.fireMode(FireMode.valueOf(statsJson.get("fireMode").getAsString()));
        if (statsJson.has("disabled")) builder.disabled(statsJson.get("disabled").getAsBoolean());
        if (statsJson.has("calibre")) builder.calibre(statsJson.get("calibre").getAsString());
        return builder.build();
    }

    /**
     * Loads ammunition data from a JSON object and creates an {@link AmmoData} object.
     *
     * @param ammoJson The JSON object containing the ammunition data.
     * @return An {@link AmmoData} object created from the JSON data.
     */
    private static AmmoData loadAmmoData(JsonObject ammoJson) {
        AmmoData.Builder builder = AmmoData.builder();
        if (ammoJson.has("reloadTime")) builder.calibre(ammoJson.get("calibre").getAsString());
        if (ammoJson.has("projectileId")) builder.projectileId(ammoJson.get("projectileId").getAsString());
        if (ammoJson.has("damage")) builder.damage(ammoJson.get("calibre").getAsFloat());
        return builder.build();
    }

    /**
     * Copies an asset file from a JAR file to the output ZIP file system.
     *
     * @param zipFs      The file system of the output ZIP file.
     * @param jarFile    The JAR file containing the asset.
     * @param entryPath  The path to the asset file within the JAR.
     * @param targetPath The target path for the asset file in the output ZIP file.
     * @throws IOException If an error occurs while copying the asset file.
     */
    private static void copyAssetFromJar(FileSystem zipFs, ZipFile jarFile, String entryPath, String targetPath) throws IOException {
        ZipEntry assetEntry = jarFile.getEntry(entryPath);
        if (assetEntry != null) {
            Path target = zipFs.getPath(targetPath);
            Files.createDirectories(target.getParent());
            try (InputStream is = jarFile.getInputStream(assetEntry)) {
                Files.copy(is, target, StandardCopyOption.REPLACE_EXISTING);
            }
        } else {
            HFF.get().getLogger().atSevere().log("Did not find asset file in JAR: " + entryPath);
        }
    }

    /**
     * Removes HFF-specific fields from an asset JSON object to ensure compatibility with Hytale.
     *
     * @param asset The asset JSON object.
     * @return A new JSON object with HFF-specific fields removed.
     */
    private static JsonObject removeHFFFields(JsonObject asset) {
        JsonObject cleaned = asset.deepCopy();
        if (cleaned.has("Components")) {
            cleaned.remove("Components");
        }
        return cleaned;
    }

    /**
     * Creates a new ZIP file system for the output asset pack.
     *
     * @param zipPath The path to the output ZIP file.
     * @return A new file system for the ZIP file.
     * @throws IOException        If an I/O error occurs.
     * @throws URISyntaxException If the URI syntax is incorrect.
     */
    private static FileSystem createNewZip(String zipPath) throws IOException, URISyntaxException {
        Path zipFile = Paths.get(zipPath);
        URI uri = URI.create("jar:" + zipFile.toUri());
        Map<String, String> env = new HashMap<>();
        env.put("create", "true");
        return FileSystems.newFileSystem(uri, env);
    }
}
