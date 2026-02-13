package lucis.lux.hff.util;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lucis.lux.hff.HFF;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * The {@code ComponentStatsLoader} class is responsible for loading component statistics
 * (such as firearm statistics) from various sources such as ZIP archives, JAR resources,
 * and the file system. It uses the configuration provided by the {@link ConfigManager}
 * to determine the paths and the order in which to search for the statistics.
 *
 * <p>This class supports loading statistics dor any component type, provided the component
 * class sis specified during instantiation. It is designed to be flexible and extensible,
 * allowing for easy integration with different types of components and statistics.</p>
 *
 * <p>The loading process follows this order:</p>
 * <ol>
 *     <li>From ZIP archives (if configured to check archives first).</li>
 *     <li>From the file system.</li>
 *     <li>From JAR resources (if configured to check archives last).</li>
 * </ol>
 *
 * <p>If no statistics are found, a new component instance with default values is returned.</p>
 *
 * @param <T> The type of the component whose statistics are to be loaded.
 */
public class ComponentStatsLoader<T> {

    /**
     * The class of the component whose statistics are to be loaded.
     */
    private final Class<T> componentClass;

    /**
     * Constructs a new {@code ComponentStatsLoader} for the specified component class.
     *
     * @param componentClass The class of the component whose statistics are to be loaded.
     */
    public ComponentStatsLoader(Class<T> componentClass) {
        this.componentClass = componentClass;
    }

    /**
     * Attempts to load component statistics for the given item ID from available sources.
     * The method tries to load the statistics from a ZIP archive, the file system, or a JAR resource,
     * depending on the configuration.
     *
     * @param itemId The ID of the item for which to load the statistics.
     * @return An instance of the component containing the loaded statistics,
     * or a new instance with default values if no statistics where found.
     */
    public T loadStatsFromResource(String itemId) {
        String fileName = itemId.replace(':', '_') + ".json";

        T component;

        if (ConfigManager.isArchiveFirst()) {
            component = tryLoadFromArchive(ConfigManager.getArchivePath(), ConfigManager.getPathInArchive() + fileName);
            if (component != null) {
                return component;
            }
        }

        component = tryLoadFromFileSystem(ConfigManager.getFilePath(), fileName);
        if (component != null) {
            return component;
        }

        if (!ConfigManager.isArchiveFirst()) {
            component = tryLoadFromArchive(ConfigManager.getArchivePath(), ConfigManager.getPathInArchive() + fileName);
            if (component != null) {
                return component;
            }
        }

        HFF.get().getLogger().atSevere().log("Couldn't find Stats for: " + itemId);
        return createDefaultComponent();
    }

    /**
     * Attempts to load the component statistics from a ZIP or JAR archive.
     *
     * @param resourcePath The path to the archive.
     * @param entryPath    The path to the entry within the archive.
     * @return An instance of the component containing the loaded statistics,
     * or {@code null} if the entry was not found or an error occurred.
     */
    private T tryLoadFromArchive(String resourcePath, String entryPath) {
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

                    T component = createDefaultComponent();
                    setStatsFromJson(component, jsonObject);
                    HFF.get().getLogger().atInfo().log("Loaded stats from ZIP: " + entryPath);
                    return component;
                }
            } catch (Exception e) {
                HFF.get().getLogger().atSevere().log("Error while loading from ZIP: " + e.getMessage());
                return null;
            }
        } else if (resourcePath.endsWith(".jar")) {
            try (InputStream is = ComponentStatsLoader.class.getResourceAsStream("/" + resourcePath)) {
                if (is != null) {
                    JsonObject jsonObject = JsonParser.parseReader(new InputStreamReader(is)).getAsJsonObject();

                    T component = createDefaultComponent();
                    setStatsFromJson(component, jsonObject);
                    HFF.get().getLogger().atInfo().log("Loaded stats from JAR: " + resourcePath);
                    return component;
                }
            } catch (Exception e) {
                HFF.get().getLogger().atSevere().log("Error while loading stats from JAR: " + e.getMessage());
            }
        }
        return null;
    }

    /**
     * Attempts to load component statistics from the file system.
     *
     * @param basePath The base path where the statistics file is located.
     * @param fileName The name of the statistics file.
     * @return An instance of the component containing the loaded statistics,
     * or {@code null} if the file was not found or an error occurred.
     */
    private T tryLoadFromFileSystem(String basePath, String fileName) {

        Path filePath = Paths.get(basePath, fileName);
        HFF.get().getLogger().atInfo().log("Searching for Stats-File: " + filePath.toAbsolutePath());

        if (!Files.exists(filePath)) {
            HFF.get().getLogger().atSevere().log("Did not find Stats-File: " + filePath.toAbsolutePath());
            return null;
        }

        try (Reader reader = Files.newBufferedReader(filePath)) {
            JsonObject jsonObject = JsonParser.parseReader(reader).getAsJsonObject();

            T component = createDefaultComponent();
            setStatsFromJson(component, jsonObject);

            HFF.get().getLogger().atInfo().log("Loaded stats from File system: " + filePath.toAbsolutePath());
            return component;
        } catch (Exception e) {
            HFF.get().getLogger().atSevere().log("Error while reading stats from file system: " + e.getMessage());
            return null;
        }
    }

    /**
     * Creates a new instance of the component with default values.
     *
     * @return A new instance of the component.
     */
    private T createDefaultComponent() {
        try {
            return componentClass.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            HFF.get().getLogger().atSevere().log("Error creating default component: " + e.getMessage());
            return null;
        }
    }

    /**
     * Sets the statistics from a JSON object to the given component.
     *
     * @param component  The component to set the statistics on.
     * @param jsonObject The JSON object containing the statistics.
     */
    private void setStatsFromJson(T component, JsonObject jsonObject) {

        try {
            for (Field field : componentClass.getDeclaredFields()) {
                String fieldName = field.getName();
                if (jsonObject.has(fieldName)) {
                    field.setAccessible(true);
                    Class<?> fieldType = field.getType();

                    if (fieldType == double.class) {
                        field.setDouble(component, jsonObject.get(fieldName).getAsDouble());
                    } else if (fieldType == int.class) {
                        field.setInt(component, jsonObject.get(fieldName).getAsInt());
                    } else if (fieldType == boolean.class) {
                        field.setBoolean(component, jsonObject.get(fieldName).getAsBoolean());
                    } else if (fieldType.isEnum()) {
                        String enumValue = jsonObject.get(fieldName).getAsString();
                        Object enumConstant = getEnumConstant(fieldType, enumValue);
                        if (enumConstant != null) {
                            field.set(component, enumConstant);
                        }
                    }
                }
            }
        } catch (Exception e) {
            HFF.get().getLogger().atSevere().log("Error setting stats from JSON: " + e.getMessage());
        }
    }

    /**
     * Retrieves an enum constant from the specified enum class with the given name.
     *
     * @param enumClass The class of the enum.
     * @param enumValue The name of the enum constant.
     * @return The enum constant, or {@code null} if the constant was not found.
     */
    private Object getEnumConstant(Class<?> enumClass, String enumValue) {
        try {
            return Enum.valueOf((Class<Enum>) enumClass, enumValue);
        } catch (Exception e) {
            HFF.get().getLogger().atSevere().log("Error getting enum constant: " + e.getMessage());
            return null;
        }
    }


}
