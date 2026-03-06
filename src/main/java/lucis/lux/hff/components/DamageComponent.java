package lucis.lux.hff.components;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.Component;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

public class DamageComponent implements Component<EntityStore> {

    public static final BuilderCodec<DamageComponent> CODEC = BuilderCodec.builder(DamageComponent.class, DamageComponent::new)
            .append(new KeyedCodec<>("Damage", Codec.FLOAT), (c, v) -> c.damage = v, c -> c.damage)
            .add()
            .build();

    private float damage;

    public DamageComponent(){
        this.damage = 1;
    }

    public DamageComponent(float damage){
        this.damage = damage;
    }

    @NullableDecl
    @Override
    public Component<EntityStore> clone() {
        return new DamageComponent(this.damage);
    }

    public float getDamage() {
        return damage;
    }

    public void setDamage(float damage) {
        this.damage = damage;
    }
}
