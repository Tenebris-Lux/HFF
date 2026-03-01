package lucis.lux.hff.components;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.Component;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

/**
 * The {@code ReloadingComponent} class is a component that represents the reloading state of a firearm.
 * This component is used to track whether a firearm is currently in the process of reloading.
 *
 * <p>This component is typically used in conjunction with the {@link lucis.lux.hff.systems.ReloadSystem}
 * to manage the reloading process of firearms in the HFF (Hytale Firearm Framework) plugin.</p>
 *
 * <p>Example usage:</p>
 * <pre>
 *     ReloadingComponent reloadingComponent = new ReloadingComponent();
 *     reloadingComponent.setReloading(true);
 *     boolean isReloading = reloadingComponent.isReloading();
 * </pre>
 */
public class ReloadingComponent implements Component<EntityStore> {

    /**
     * The {@link BuilderCodec} for serializing and deserializing this component.
     */
    public static final BuilderCodec<ReloadingComponent> CODEC = BuilderCodec.builder(ReloadingComponent.class, ReloadingComponent::new)
            .append(new KeyedCodec<>("IsReloading", Codec.BOOLEAN), (c, v) -> c.isReloading = v, c -> c.isReloading)
            .add()
            .build();

    /**
     * Indicates whether the firearm is currently reloading.
     */
    private boolean isReloading;

    /**
     * Constructs a new {@code ReloadingComponent} with the default reloading state set to false.
     */
    public ReloadingComponent() {
        this.isReloading = false;
    }

    /**
     * Constructs a new {@code ReloadingComponent} with the specified reloading state.
     *
     * @param isReloading The initial reloading state.
     */
    public ReloadingComponent(boolean isReloading) {
        this.isReloading = isReloading;
    }

    /**
     * Constructs a new {@code ReloadingComponent} by copying the state from another component.
     *
     * @param other The component to copy.
     */
    public ReloadingComponent(ReloadingComponent other) {
        this.isReloading = other.isReloading;
    }

    /**
     * Creates a copy of this component.
     *
     * @return A copy of this component.
     */
    @NullableDecl
    @Override
    public Component<EntityStore> clone() {
        return new ReloadingComponent(this);
    }

    /**
     * Returns whether the firearm is currently reloading.
     *
     * @return {@code true} if the firearm is reloading, {@code false} otherwise.
     */
    public boolean isReloading() {
        return isReloading;
    }

    /**
     * Sets the reloading state of the firearm.
     *
     * @param reloading The new reloading state.
     */
    public void setReloading(boolean reloading) {
        isReloading = reloading;
    }

    /**
     * Toggles the reloading state of the firearm.
     */
    public void toggleReloading() {
        this.isReloading = !isReloading;
    }
}
