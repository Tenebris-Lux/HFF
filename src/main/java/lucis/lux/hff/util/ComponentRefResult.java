package lucis.lux.hff.util;

import com.hypixel.hytale.component.Component;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

public record ComponentRefResult<K extends Component<EntityStore>>
        (K component, Ref<EntityStore> ref, boolean newlyCreated, boolean disabled) {
}
