package lucis.lux.hff.data;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.ExtraInfo;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.schema.SchemaContext;
import com.hypixel.hytale.codec.schema.config.ArraySchema;
import com.hypixel.hytale.codec.schema.config.Schema;
import com.hypixel.hytale.codec.schema.config.StringSchema;
import lucis.lux.hff.enums.AttachmentType;
import org.bson.BsonArray;
import org.bson.BsonString;
import org.bson.BsonValue;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

import java.io.Serializable;
import java.util.EnumMap;
import java.util.LinkedList;
import java.util.Map;

/**
 * The {@code FirearmState} class is used to track the operational state of a firearm, such as the number of
 * loaded projectiles.
 *
 * <p>This class is typically used in the HFF (Hytale Firearm Framework) plugin to manage the state
 * of firearms during gameplay. It allows for tracking which projectiles are loaded.</p>
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
    private static final Codec<LinkedList<String>> LINKED_LIST_CODEC = new Codec<LinkedList<String>>() {
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
     * The {@link BuilderCodec} for serializing and deserializing this class.
     * This codec handles the UUID and the list of loaded projectiles.
     */
    public static final BuilderCodec<FirearmState> CODEC = BuilderCodec.builder(FirearmState.class, FirearmState::new)
            .append(new KeyedCodec<>("LoadedProjectiles", LINKED_LIST_CODEC), (c, v) -> c.loadedProjectiles = v, c -> c.loadedProjectiles)
            .add()
            .build();

    /**
     * A list of projectiles currently loaded into the firearm.
     * The list is implemented as a {@link LinkedList} to allow efficient addition and removal of projectiles.
     */
    private LinkedList<String> loadedProjectiles;

    private final Map<AttachmentType, String> activeAttachments;

    /**
     * Constructs a new {@code FirearmState} with an empty list of loaded projectiles.
     */
    public FirearmState() {
        this.activeAttachments = new EnumMap<>(AttachmentType.class);
        this.loadedProjectiles = new LinkedList<>();
    }

    public void installAttachment(AttachmentType type, String attachmentItemId){
        activeAttachments.put(type, attachmentItemId);
    }

    public void removeAttachment(AttachmentType type){
        activeAttachments.remove(type);
    }

    public Map<AttachmentType, String> getAttachments(){
        return activeAttachments;
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
     * @return The ID of the next projectile, or {@code null} if there are no projectiles loaded.
     */
    public String consumeNextProjectile() {
        return loadedProjectiles.poll();
    }
}
