package lucis.lux.hff.data;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.ExtraInfo;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.schema.SchemaContext;
import com.hypixel.hytale.codec.schema.config.ArraySchema;
import com.hypixel.hytale.codec.schema.config.Schema;
import com.hypixel.hytale.codec.schema.config.StringSchema;
import lucis.lux.hff.HFF;
import lucis.lux.hff.data.registry.Registries;
import lucis.lux.hff.enums.AttachmentType;
import lucis.lux.hff.enums.FireMode;
import lucis.lux.hff.enums.MagazineType;
import org.bson.BsonArray;
import org.bson.BsonString;
import org.bson.BsonValue;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.Serializable;
import java.util.*;

/**
 * The {@code FirearmState} class acts as the extrinsic (dynamic) data structure in the Flyweight pattern.
 * It tracks the operational, instance-specific state of a single firearm during gameplay, ensuring that
 * base stats remain immutable.
 *
 * <p>This class manages loaded projectiles, active attachments, current fire modes, and mechanical
 * statuses such as jamming or bursting. States are typically mapped to a weapon item's UUID metadata.</p>
 *
 * <p>Example usage:</p>
 * <pre>
 *     FirearmState state = new FirearmState();
 *     state.loadProjectile("9mm_projectile");
 *     String nextProjectile = state.consumeNextProjectile();
 * </pre>
 */
public class FirearmState implements Serializable {

    /**
     * A custom {@link Codec} for serializing and deserializing a {@link LinkedList} of strings.
     * This codec is used to handle the list of loaded projectiles.
     */
    public static final Codec<LinkedList<String>> LINKED_LIST_CODEC = new Codec<LinkedList<String>>() {
        @NullableDecl
        @Override
        public LinkedList<String> decode(BsonValue bsonValue, ExtraInfo extraInfo) {
            if (bsonValue instanceof BsonArray array) {
                LinkedList<String> list = new LinkedList<>();
                for (BsonValue value : array) {
                    list.add(value.asString().getValue());
                }
                return list;
            }
            return new LinkedList<>();
        }

        @Override
        public BsonValue encode(LinkedList<String> strings, ExtraInfo extraInfo) {
            BsonArray array = new BsonArray();
            for (String str : strings) {
                array.add(new BsonString(str));
            }
            return array;
        }

        @NonNullDecl
        @Override
        public Schema toSchema(@NonNullDecl SchemaContext schemaContext) {
            ArraySchema arraySchema = new ArraySchema();
            arraySchema.setItems(new StringSchema());
            return arraySchema;
        }
    };

    /**
     * A custom {@link Codec} for serializing and deserializing a map of attachment types to item IDs.
     * This codec is used to handle the active attachments of a firearm.
     */
    public static final Codec<Map<AttachmentType, String>> ATTACHMENT_MAP_CODEC = new Codec<Map<AttachmentType, String>>() {
        @Nullable
        @Override
        public Map<AttachmentType, String> decode(BsonValue bsonValue, ExtraInfo extraInfo) {
            if (bsonValue instanceof BsonArray array) {
                Map<AttachmentType, String> map = new EnumMap<>(AttachmentType.class);
                for (BsonValue entry : array) {
                    if (entry instanceof BsonArray entryArray && entryArray.size() == 2) {
                        AttachmentType type = AttachmentType.valueOf(entryArray.get(0).asString().getValue());
                        String itemId = entryArray.get(1).asString().getValue();
                        map.put(type, itemId);
                    }
                }
                return map;
            }
            return new EnumMap<>(AttachmentType.class);
        }

        @Override
        public BsonValue encode(Map<AttachmentType, String> attachmentTypeStringMap, ExtraInfo extraInfo) {
            BsonArray array = new BsonArray();
            for (Map.Entry<AttachmentType, String> entry : attachmentTypeStringMap.entrySet()) {
                BsonArray entryArray = new BsonArray();
                entryArray.add(new BsonString(entry.getKey().name()));
                entryArray.add(new BsonString(entry.getValue()));
                array.add(entryArray);
            }
            return array;
        }

        @NotNull
        @Override
        public Schema toSchema(@NotNull SchemaContext schemaContext) {
            ArraySchema arraySchema = new ArraySchema();
            arraySchema.setItems(new ArraySchema());
            return arraySchema;
        }
    };

    /**
     * The {@link BuilderCodec} for serializing and deserializing this class.
     * This codec handles the UUID, loaded projectiles, active attachments, magazine state, and mechanical statuses.
     */
    public static final BuilderCodec<FirearmState> CODEC = BuilderCodec.builder(FirearmState.class, FirearmState::new)
            .append(new KeyedCodec<>("LoadedProjectiles", LINKED_LIST_CODEC), (c, v) -> c.loadedProjectiles = v, c -> c.loadedProjectiles)
            .add()
            .append(new KeyedCodec<>("ActiveAttachments", ATTACHMENT_MAP_CODEC), (c, v) -> c.activeAttachments = v, c -> c.activeAttachments)
            .add()
            .append(new KeyedCodec<>("InsertedMagazineUuid", Codec.UUID_BINARY),
                    (c, v) -> c.insertedMagazineUuid = v,
                    c -> c.insertedMagazineUuid)
            .add()
            .append(new KeyedCodec<>("InsertedMagazineName", Codec.STRING),
                    (c, v) -> c.insertedMagazineName = v,
                    c -> c.insertedMagazineName)
            .add()
            .append(new KeyedCodec<>("IsJammed", Codec.BOOLEAN),
                    (c, v) -> c.isJammed = v,
                    c -> c.isJammed)
            .add()
            .append(new KeyedCodec<>("CurrentFireMode", Codec.STRING),
                    (c, v) -> c.currentFireMode = v != null ? FireMode.valueOf(v) : null,
                    c -> c.currentFireMode != null ? c.currentFireMode.name() : null)
            .add()
            .append(new KeyedCodec<>("IsBursting", Codec.BOOLEAN),
                    (c, v) -> c.isBursting = v,
                    c -> c.isBursting)
            .add()
            .build();

    /**
     * Maps available attachment slots to the currently installed attachment item IDs.
     */
    private Map<AttachmentType, String> activeAttachments;

    /**
     * A list of projectiles currently loaded into the firearm.
     * The list is implemented as a {@link LinkedList} to allow efficient addition and removal of projectiles.
     */
    private LinkedList<String> loadedProjectiles;

    /**
     * The UUID of the currently inserted magazine, if applicable.
     */
    private UUID insertedMagazineUuid = null;

    /**
     * The name of the currently inserted magazine, if applicable.
     */
    private String insertedMagazineName = null;

    /**
     * Indicates whether the firearm has suffered a malfunction and requires clearing.
     */
    private boolean isJammed = false;

    /**
     * The currently selected fire mode (e.g., switched via SELECT_FIRE).
     * If null, the weapon's default fire mode should be used.
     */
    private FireMode currentFireMode = null;

    /**
     * Indicates whether the firearm is currently executing an automated burst sequence.
     * Used to prevent players from interrupting or stacking burst fire inputs.
     */
    private boolean isBursting = false;

    /**
     * Constructs a new {@code FirearmState} with an empty list of loaded projectiles and attachments.
     */
    public FirearmState() {
        this.activeAttachments = new EnumMap<>(AttachmentType.class);
        this.loadedProjectiles = new LinkedList<>();
    }

    /**
     * Returns the UUID of the currently inserted magazine.
     *
     * @return The UUID of the inserted magazine, or {@code null} if no magazine is inserted.
     */
    public UUID getInsertedMagazineUuid() {
        return insertedMagazineUuid;
    }

    /**
     * Sets the UUID of the currently inserted magazine.
     *
     * @param insertedMagazineUuid The UUID of the magazine to insert.
     */
    public void setInsertedMagazineUuid(UUID insertedMagazineUuid) {
        this.insertedMagazineUuid = insertedMagazineUuid;
    }

    /**
     * Retrieves the currently active fire mode. If the player hasn't manually switched modes,
     * the provided default mode is returned.
     *
     * @param defaultMode The base fire mode defined in the firearm's intrinsic stats.
     * @return The active {@link FireMode}.
     */
    public FireMode getCurrentFireMode(FirearmStats defaultMode) {
        return currentFireMode != null ? currentFireMode : defaultMode.fireMode();
    }

    /**
     * Sets the active fire mode for this specific firearm instance.
     *
     * @param mode The new {@link FireMode} to set.
     */
    public void setCurrentFireMode(FireMode mode) {
        this.currentFireMode = mode;
    }

    /**
     * Checks if the firearm is currently firing a burst sequence.
     *
     * @return {@code true} if a burst is in progress, {@code false} otherwise.
     */
    public boolean isBursting() {
        return isBursting;
    }

    /**
     * Locks or unlocks the firearm's burst firing state.
     *
     * @param bursting {@code true} to lock the weapon during a burst, {@code false} to unlock.
     */
    public void setBursting(boolean bursting) {
        this.isBursting = bursting;
    }

    /**
     * Checks if the firearm is currently jammed and incapable of firing.
     *
     * @return {@code true} if the weapon is jammed.
     */
    public boolean isJammed() {
        return isJammed;
    }

    /**
     * Sets the mechanical jam state of the firearm.
     *
     * @param jammed {@code true} to cause a jam, {@code false} to clear it.
     */
    public void setJammed(boolean jammed) {
        this.isJammed = jammed;
    }

    /**
     * Installs an attachment into the specified slot, overwriting any existing attachment
     * in that slot.
     *
     * @param type             The slot type (e.g., MUZZLE, OPTIC).
     * @param attachmentItemId The item ID of the attachment.
     */
    public void installAttachment(AttachmentType type, String attachmentItemId) {
        activeAttachments.put(type, attachmentItemId);
    }

    /**
     * Removes an attachment from the specified slot.
     *
     * @param type The slot type to clear.
     */
    public void removeAttachment(AttachmentType type) {
        activeAttachments.remove(type);
    }

    /**
     * Retrieves an unmodifiable view of the currently installed attachments.
     *
     * @return A map of attachment slot to their installed item IDs.
     */
    public Map<AttachmentType, String> getAttachments() {
        return new HashMap<>(activeAttachments);
    }

    /**
     * Returns the current number of loaded projectiles in the firearm.
     *
     * @return The number of loaded projectiles.
     */
    public int getCurrentAmmoCount() {
        return loadedProjectiles.size();
    }

    /**
     * Loads a projectile into the firearm.
     * The projectile is added to the front of the list of loaded projectiles.
     *
     * @param projectileId The ID of the projectile to load.
     */
    public void loadProjectile(String projectileId) {
        loadedProjectiles.push(projectileId);
    }

    /**
     * Consumes the next projectile from the firearm.
     * The projectile is removed from the front of the list of loaded projectiles.
     *
     * @param stats The firearm statistics to check for magazine type.
     * @return The ID of the next projectile, or {@code null} if there are no projectiles loaded.
     */
    public String consumeNextProjectile(FirearmStats stats) {
        if (HFF.get().getConfigData().isHardcoreMagazineSystem() && stats.magazineType().equals(MagazineType.EXTERNAL)) {
            if (insertedMagazineUuid != null) {
                MagazineState mag = Registries.MAGAZINE_STATES.get(insertedMagazineUuid);
                return mag != null ? mag.consumeNextProjectile() : null;
            }
            return null;
        }
        return loadedProjectiles.poll();
    }

    /**
     * Returns the name of the currently inserted magazine.
     *
     * @return The name of the inserted magazine, or {@code null} if no magazine is inserted.
     */
    public String getInsertedMagazineName() {
        return insertedMagazineName;
    }

    /**
     * Sets the name of the currently inserted magazine.
     *
     * @param insertedMagazineName The name of the magazine to insert.
     */
    public void setInsertedMagazineName(String insertedMagazineName) {
        this.insertedMagazineName = insertedMagazineName;
    }
}
