package lucis.lux.hff.components;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.Component;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

public class AmmoComponent implements Component<EntityStore> {

    public static final BuilderCodec<AmmoComponent> CODEC = BuilderCodec.builder(AmmoComponent.class, AmmoComponent::new).append(new KeyedCodec<>("VelocityMod", Codec.FLOAT), (c, v) -> c.velocityMod = v, c -> c.velocityMod).add().append(new KeyedCodec<>("DamageMod", Codec.FLOAT), (c, v) -> c.damageMod = v, c -> c.damageMod).add().append(new KeyedCodec<>("SpreadMod", Codec.FLOAT), (c, v) -> c.spreadMod = v, c -> c.spreadMod).add().append(new KeyedCodec<>("AmmoName", Codec.STRING), (c, v) -> c.ammoName = v, c -> c.ammoName).add().build();
    private float velocityMod;
    private float damageMod;
    private float spreadMod;
    private String ammoName;

    public AmmoComponent() {
        this.damageMod = 1.0f;
        this.spreadMod = 1.0f;
        this.velocityMod = 1.0f;
        this.ammoName = null;
    }

    public AmmoComponent(float damageMod, float spreadMod, float velocityMod, String ammoName) {
        this.velocityMod = velocityMod;
        this.spreadMod = spreadMod;
        this.damageMod = damageMod;
        this.ammoName = ammoName;
    }

    public AmmoComponent(AmmoComponent other) {
        this.damageMod = other.damageMod;
        this.spreadMod = other.spreadMod;
        this.velocityMod = other.velocityMod;
        this.ammoName = other.ammoName;
    }

    @NullableDecl
    @Override
    public Component<EntityStore> clone() {
        return new AmmoComponent(this);
    }


    public float getVelocityMod() {
        return velocityMod;
    }

    public void setVelocityMod(float velocityMod) {
        this.velocityMod = velocityMod;
    }

    public float getDamageMod() {
        return damageMod;
    }

    public void setDamageMod(float damageMod) {
        this.damageMod = damageMod;
    }

    public float getSpreadMod() {
        return spreadMod;
    }

    public void setSpreadMod(float spreadMod) {
        this.spreadMod = spreadMod;
    }

    public String getAmmoName() {
        return ammoName;
    }

    public void setAmmoName(String ammoName) {
        this.ammoName = ammoName;
    }
}
