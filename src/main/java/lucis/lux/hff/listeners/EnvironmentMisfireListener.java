package lucis.lux.hff.listeners;

import com.hypixel.hytale.builtin.weather.components.WeatherTracker;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.asset.type.weather.config.Weather;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.entity.movement.MovementStatesComponent;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import lucis.lux.hff.data.FirearmStats;
import lucis.lux.hff.enums.FirearmClass;
import lucis.lux.hff.events.ShootEvent;

/**
 * The {@code EnvironmentMisfireListener} class is a listener that reacts to firearm shooting events
 * and adjusts the misfire chance based on environmental conditions such as rain and submersion.
 * This listener is designed to work with the HFF (Hytale Firearm Framework) plugin and is registered
 * to handle {@link ShootEvent.Pre} events.
 *
 * <p>This listener performs the following tasks:</p>
 * <ul>
 *   <li>Retrieves the firearm's statistics and the player's current world.</li>
 *   <li>Checks if the player is submerged (swimming) or in rainy weather.</li>
 *   <li>Adjusts the misfire chance for Flintlock firearms based on these conditions.</li>
 *   <li>Cancels the shooting event if a misfire occurs.</li>
 * </ul>
 *
 * <p>This listener is particularly useful for Flintlock firearms, which are more prone to misfires
 * in wet conditions.</p>
 *
 * <p>Example usage:</p>
 * <pre>
 *     // Register the listener for ShootEvent.Pre events
 *     getEventRegistry().register(ShootEvent.Pre.class, EnvironmentMisfireListener::onWeaponFire);
 * </pre>
 */
public class EnvironmentMisfireListener {

    /**
     * Handles the {@link ShootEvent.Pre} event by adjusting the misfire chance based on environmental conditions.
     * This method cancels the shooting event if a misfire occurs.
     *
     * <p>The following steps are performed:</p>
     * <ol>
     *   <li>Retrieves the firearm's statistics and the player's current world.</li>
     *   <li>Checks if the player is submerged (swimming) or in rainy weather.</li>
     *   <li>Adjusts the misfire chance for Flintlock firearms based on these conditions.</li>
     *   <li>Cancels the shooting event if a misfire occurs.</li>
     * </ol>
     *
     * @param event The {@link ShootEvent.Pre} event to handle.
     */
    public static void onWeaponFire(ShootEvent.Pre event) {
        FirearmStats stats = event.getStats();

        Ref<EntityStore> ref = event.getPlayerRef();
        World currentWorld = ref.getStore().getComponent(ref, Player.getComponentType()).getWorld();


        Store<EntityStore> store = ref.getStore();

        MovementStatesComponent movement = store.getComponent(ref, MovementStatesComponent.getComponentType());

        boolean isSubmerged = movement.getMovementStates().swimming;


        WeatherTracker weather = store.getComponent(ref, WeatherTracker.getComponentType());
        boolean isRaining = Weather.getAssetMap().getAsset(weather.getWeatherIndex()).getId().equals("Rain");

        float currentMisfireChance = stats.misfireChance();

        if (stats.firearmClass() == FirearmClass.FLINTLOCK) {
            if (isRaining) currentMisfireChance += 0.4f;
            if (isSubmerged) currentMisfireChance = 1.0f;
        }

        if (Math.random() < currentMisfireChance) {
            event.setCancelled(true);
        }
    }
}