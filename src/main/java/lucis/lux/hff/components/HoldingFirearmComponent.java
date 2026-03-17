package lucis.lux.hff.components;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.Component;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

/**
 * The {@code HoldingFirearmComponent} class is a component that tracks whether an entity (typically a player)
 * is currently holding a firearm and stores the UUID of the held weapon.
 *
 * <p>This component is part of the Entity Component System (ECS) architecture in Hytale and is used to manage
 * the state of firearms held by entities. It supports serialization and deserialization via a {@link BuilderCodec}.</p>
 *
 * <p>Example usage:</p>
 * <pre>
 *     // Create a new component
 *     HoldingFirearmComponent component = new HoldingFirearmComponent(true, weaponUuid);
 *
 *     // Check if the entity is holding a firearm
 *     boolean isHolding = component.isHoldingFirearm();
 *
 *     // Get the UUID of the held weapon
 *     UUID weaponUuid = component.getWeaponUuid();
 * </pre>
 */
public class HoldingFirearmComponent implements Component<EntityStore> {

    /**
     * The {@link BuilderCodec} for serializing and deserializing this component.
     * This codec handles the fields {@code isHoldingFirearm} and {@code weaponUuid}.
     */
    public static final BuilderCodec<HoldingFirearmComponent> CODEC = BuilderCodec.builder(
                    HoldingFirearmComponent.class,
                    HoldingFirearmComponent::new
            )
            .append(
                    new KeyedCodec<>("IsHoldingFirearm", Codec.BOOLEAN),
                    (c, v) -> c.isHoldingFirearm = v,
                    c -> c.isHoldingFirearm
            )
            .add()
            .append(
                    new KeyedCodec<>("WeaponUuid", Codec.UUID_BINARY),
                    (c, v) -> c.weaponUuid = v,
                    c -> c.weaponUuid
            )
            .add()
            .build();

    /**
     * Indicates whether the entity is currently holding a firearm.
     */
    private boolean isHoldingFirearm = false;

    /**
     * The UUID of the weapon currently held by the entity.
     */
    private UUID weaponUuid = null;

    /**
     * Constructs a new {@code HoldingFirearmComponent} with default values.
     * The entity is not holding a firearm, and the weapon UUID is {@code null}.
     */
    public HoldingFirearmComponent() {
    }

    /**
     * Constructs a new {@code HoldingFirearmComponent} with the specified values.
     *
     * @param isHoldingFirearm {@code true} if the entity is holding a firearm, {@code false} otherwise.
     * @param weaponUuid       The UUID of the weapon currently held by the entity.
     */
    public HoldingFirearmComponent(boolean isHoldingFirearm, UUID weaponUuid) {
        this.isHoldingFirearm = isHoldingFirearm;
        this.weaponUuid = weaponUuid;
    }

    /**
     * Constructs a new {@code HoldingFirearmComponent} by copying the state from another component.
     *
     * @param other The component to copy.
     */
    public HoldingFirearmComponent(HoldingFirearmComponent other) {
        this.isHoldingFirearm = other.isHoldingFirearm;
        this.weaponUuid = other.weaponUuid;
    }

    /**
     * Creates a copy of this component.
     *
     * @return A copy of this component.
     */
    @Nullable
    @Override
    public Component<EntityStore> clone() {
        return new HoldingFirearmComponent(this);
    }

    /**
     * Returns whether the entity is currently holding a firearm.
     *
     * @return {@code true} if the entity is holding a firearm, {@code false} otherwise.
     */
    public boolean isHoldingFirearm() {
        return isHoldingFirearm;
    }

    /**
     * Sets whether the entity is holding a firearm.
     *
     * @param holdingFirearm {@code true} if the entity is holding a firearm, {@code false} otherwise.
     */
    public void setHoldingFirearm(boolean holdingFirearm) {
        isHoldingFirearm = holdingFirearm;
    }

    /**
     * Returns the UUID of the weapon currently held by the entity.
     *
     * @return The UUID of the held weapon, or {@code null} if no weapon is held.
     */
    public UUID getWeaponUuid() {
        return weaponUuid;
    }

    /**
     * Sets the UUID of the weapon currently held by the entity.
     *
     * @param weaponUuid The UUID of the weapon to set.
     */
    public void setWeaponUuid(UUID weaponUuid) {
        this.weaponUuid = weaponUuid;
    }
}
