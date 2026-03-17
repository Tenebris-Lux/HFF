package lucis.lux.hff.storage;

import lucis.lux.hff.HFF;
import lucis.lux.hff.data.FirearmState;
import lucis.lux.hff.data.MagazineState;
import lucis.lux.hff.data.registry.Registries;

import java.io.*;
import java.util.Arrays;
import java.util.Map;
import java.util.UUID;

/**
 * The {@code HFFStateStorage} class is responsible for saving and loading the state of firearms and
 * magazines to and from a file. This class provides methods to serialize and deserialize the state map
 * of firearms and magazines, allowing the states to persist between game sessions.
 *
 * <p>This class performs the following tasks:</p>
 * <ul>
 *   <li>Saves the current state of all firearms and magazines to a file.</li>
 *   <li>Loads the state of all firearms and magazines from a file.</li>
 * </ul>
 *
 * <p>The states are stored in a binary file, which can be used to restore the state of firearms
 * when the game is restarted.</p>
 *
 * <p>Example usage:</p>
 * <pre>
 *     // Save the current state of all firearms
 *     HFFStateStorage.saveStates();
 *
 *     // Load the state of all firearms
 *     HFFStateStorage.loadStates();
 * </pre>
 *
 * @see FirearmState
 * @see MagazineState
 * @see Registries
 */
public class HFFStateStorage {

    /**
     * The file where the state of firearms and magazines is stored.
     * The file is located in the "mods/lucis.lux_HFF/" directory.
     */
    private static final File STATE_FILE = new File("mods/lucis.lux_HFF/hff_states.dat");

    /**
     * Saves the current state of all firearms and magazines to the file.
     *
     * <p>The following steps are performed:</p>
     * <ol>
     *   <li>Creates the parent directory for the state file if it does not exist.</li>
     *   <li>Serializes the state maps of firearms and magazines.</li>
     *   <li>Writes the serialized data to the state file.</li>
     * </ol>
     *
     * <p>If an error occurs during the process, it is logged.</p>
     */
    public static void saveStates() {
        if (STATE_FILE.getParentFile() != null && !STATE_FILE.getParentFile().exists()) {
            STATE_FILE.getParentFile().mkdirs();
        }
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(STATE_FILE))) {
            oos.writeObject(Registries.FIREARM_STATES.copy());
            oos.writeObject(Registries.MAGAZINE_STATES.copy());
        } catch (IOException e) {
            HFF.get().getLogger().atSevere().log("Failed to save HFF states: " + Arrays.toString(e.getStackTrace()));
        }
    }

    /**
     * Loads the state of all firearms and magazines from the file.
     *
     * <p>The following steps are performed:</p>
     * <ol>
     *   <li>Checks if the state file exists. If not, the method returns immediately.</li>
     *   <li>Deserializes the state maps of firearms and magazines from the file.</li>
     *   <li>Updates the state registries with the loaded states.</li>
     * </ol>
     *
     * <p>If an error occurs during the process, it is logged.</p>
     */
    public static void loadStates() {
        if (!STATE_FILE.exists()) {
            return;
        }
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(STATE_FILE))) {
            Map<UUID, FirearmState> stateMap = (Map<UUID, FirearmState>) ois.readObject();
            stateMap.forEach((Registries.FIREARM_STATES::update));

            Map<UUID, MagazineState> magazineStateMap = (Map<UUID, MagazineState>) ois.readObject();
            magazineStateMap.forEach(Registries.MAGAZINE_STATES::update);
        } catch (IOException | ClassNotFoundException e) {
            HFF.get().getLogger().atSevere().log("Failed to load firearm states: " + e.getMessage());
        }
    }
}
