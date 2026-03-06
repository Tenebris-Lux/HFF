package lucis.lux.hff.storage;

import lucis.lux.hff.HFF;
import lucis.lux.hff.data.FirearmState;
import lucis.lux.hff.data.FirearmStateManager;

import java.io.*;
import java.util.Map;
import java.util.UUID;

/**
 * The {@code FirearmStateStorage} class is responsible for saving and loading the state of firearms
 * to and from a file. This class provides methods to serialize and deserialize the state map of firearms,
 * allowing the states to persist between game sessions.
 *
 * <p>This class performs the following tasks:</p>
 * <ul>
 *   <li>Saves the current state of all firearms to a file.</li>
 *   <li>Loads the state of all firearms from a file.</li>
 * </ul>
 *
 * <p>The state of firearms is stored in a binary file, which can be used to restore the state of firearms
 * when the game is restarted.</p>
 *
 * <p>Example usage:</p>
 * <pre>
 *     // Save the current state of all firearms
 *     FirearmStateStorage.saveStates();
 *
 *     // Load the state of all firearms
 *     FirearmStateStorage.loadStates();
 * </pre>
 *
 * @see FirearmState
 * @see FirearmStateManager
 */
public class FirearmStateStorage {

    /**
     * The file where the state of firearms is stored.
     */
    private static final File STATE_FILE = new File("mods/lucis.lux_HFF/hff_states.dat");

    /**
     * Saves the current state of all firearms to the file.
     *
     * <p>This method serializes the state map of firearms and writes it to the file.</p>
     */
    public static void saveStates() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(STATE_FILE))) {
            oos.writeObject(FirearmStateManager.getStateMap());
        } catch (IOException e) {
            HFF.get().getLogger().atSevere().log("Failed to save firearm states: " + e.getMessage());
        }
    }

    /**
     * Loads the state of all firearms from the file.
     *
     * <p>This method deserializes the state map of firearms from the file and updates the state manager.</p>
     *
     * <p>If the file does not exist, this method does nothing.</p>
     */
    public static void loadStates() {
        if (!STATE_FILE.exists()) {
            return;
        }
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(STATE_FILE))) {
            Map<UUID, FirearmState> stateMap = (Map<UUID, FirearmState>) ois.readObject();
            stateMap.forEach((FirearmStateManager::updateState));
        } catch (IOException | ClassNotFoundException e) {
            HFF.get().getLogger().atSevere().log("Failed to load firearm states: " + e.getMessage());
        }
    }
}
