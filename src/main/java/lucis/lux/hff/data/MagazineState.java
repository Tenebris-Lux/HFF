package lucis.lux.hff.data;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.ExtraInfo;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.schema.SchemaContext;
import com.hypixel.hytale.codec.schema.config.ArraySchema;
import com.hypixel.hytale.codec.schema.config.Schema;
import com.hypixel.hytale.codec.schema.config.StringSchema;
import org.bson.BsonArray;
import org.bson.BsonString;
import org.bson.BsonValue;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

import java.io.Serializable;
import java.util.LinkedList;

/**
 * The {@code MagazineState} class tracks the extrinsic (dynamic) state of a magazine item.
 * It strictly handles the queue of loaded projectiles (bullets) and ensures that the state
 * survives server restarts through serialization.
 *
 * <p>This class is part of the Flyweight pattern and is used to manage the operational state
 * of a single magazine during gameplay. It allows for efficient addition and removal of projectiles.</p>
 *
 * <p>Example usage:</p>
 * <pre>
 *     MagazineState state = new MagazineState();
 *     state.loadProjectile("9mm_projectile");
 *     String nextProjectile = state.consumeNextProjectile();
 * </pre>
 */
public class MagazineState implements Serializable {

    /**
     * A custom {@link Codec} for serializing and deserializing a {@link LinkedList} of strings.
     * This codec is used to handle the list of loaded projectiles and ensures that the magazine's contents
     * survive server restarts.
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
     * This codec handles the list of loaded projectiles.
     */
    public static final BuilderCodec<MagazineState> CODEC = BuilderCodec.builder(MagazineState.class, MagazineState::new)
            .append(new KeyedCodec<>("LoadedProjectiles", LINKED_LIST_CODEC), (c, v) -> c.loadedProjectiles = v, c -> c.loadedProjectiles)
            .add()
            .build();

    /**
     * A list of projectiles currently loaded into the magazine.
     * The list is implemented as a {@link LinkedList} to allow efficient addition and removal of projectiles.
     */
    private LinkedList<String> loadedProjectiles;

    /**
     * Constructs a new {@code MagazineState} with an empty list of loaded projectiles.
     */
    public MagazineState() {
        this.loadedProjectiles = new LinkedList<>();
    }

    /**
     * Returns the current number of loaded projectiles in the magazine.
     *
     * @return The number of loaded projectiles.
     */
    public int getCurrentAmmoCount() {
        return loadedProjectiles.size();
    }

    /**
     * Loads a projectile into the magazine.
     * The projectile is added to the front of the list of loaded projectiles.
     *
     * @param projectileId The ID of the projectile to load.
     */
    public void loadProjectile(String projectileId) {
        loadedProjectiles.push(projectileId);
    }

    /**
     * Consumes the next projectile from the magazine.
     * The projectile is removed from the front of the list of loaded projectiles.
     *
     * @return The ID of the next projectile, or {@code null} if there are no projectiles loaded.
     */
    public String consumeNextProjectile() {
        return loadedProjectiles.poll();
    }
}
