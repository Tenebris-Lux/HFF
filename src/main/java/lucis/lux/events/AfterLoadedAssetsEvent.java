package lucis.lux.events;

import com.hypixel.hytale.assetstore.AssetMap;
import com.hypixel.hytale.assetstore.JsonAsset;
import com.hypixel.hytale.assetstore.event.LoadedAssetsEvent;
import lucis.lux.HFF;
import lucis.lux.util.FirearmStatsLoader;

public class AfterLoadedAssetsEvent {
    public static void onAssetsLoaded(LoadedAssetsEvent<String, JsonAsset<String>, AssetMap<String, JsonAsset<String>>> event) {

        for (Object asset : event.getLoadedAssets().values()) {
            if (asset instanceof JsonAsset<?>) {
                JsonAsset jsonAsset = (JsonAsset) asset;
                String assetId = jsonAsset.getId().toString();

                if (assetId.startsWith("hff") && assetId.contains("Firearm")) {
                    HFF.get().getLogger().atInfo().log("Found Firearm: " + assetId);
                    FirearmStatsLoader.loadStatsFromResource(assetId);
                }
            }
        }
    }

}
