package lucis.lux.hff.components;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.Component;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

/**
 * The {@code AimComponent} class represents the aiming state of an entity, such as a player.
 * It tracks whether the entity is currently aiming down sights (ADS) or not, which can be used
 * to modify gameplay mechanics like camera behaviour, weapon spread, or animations.
 *
 * <p>This component is typically attached to entities that can aim, such as players holding firearms.
 * The aiming state can be toggled and queried to adjust gameplay logic accordingly.</p>
 *
 * <p>Example use cases include:</p>
 * <ul>
 *     <li>Reducing weapon spread or increasing accuracy while aiming.</li>
 *     <li>Triggering animations or visual effects when entering or exiting aim mode.</li>
 * </ul>
 */
public class AimComponent implements Component<EntityStore> {

    /**
     * The {@link BuilderCodec} for serializing and deserializing the {@code AimComponent}.
     * This codec handles the "IsAiming" field, which stores the aiming state as a boolean.
     */
    public static final BuilderCodec<AimComponent> CODEC = BuilderCodec
            .builder(AimComponent.class, AimComponent::new)
            .append(new KeyedCodec<>("IsAiming", Codec.BOOLEAN), (c, v) -> c.isAiming = v, c -> c.isAiming)
            .add()
            .build();
    /**
     * Indicates whether the entity is currently aiming.
     */
    private boolean isAiming;

    /**
     * Constructs a new {@code AimComponent} with the default aiming state set to {@code false}.
     */
    public AimComponent() {
        this.isAiming = false;
    }

    /**
     * Constructs a new {@code AimComponent} with the specified aiming state.
     *
     * @param isAiming The initial aiming state of the component.
     */
    public AimComponent(boolean isAiming) {
        this.isAiming = isAiming;
    }

    /**
     * Returns wheter the entity is currently aiming.
     *
     * @return {@code true} if the entity is aiming, {@code false} otherwise.
     */
    public boolean isAiming() {
        return isAiming;
    }

    /**
     * Sets the aiming state of the entity.
     *
     * @param aiming The new aiming state.
     */
    public void setAiming(boolean aiming) {
        this.isAiming = aiming;
    }

    /**
     * Creates and returns a copy of this component.
     *
     * @return A new {@code AimComponent} with the same aiming state as this component.
     */
    @NullableDecl
    @Override
    public Component<EntityStore> clone() {
        return new AimComponent(isAiming);
    }
}
