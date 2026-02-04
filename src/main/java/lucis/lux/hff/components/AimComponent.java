package lucis.lux.hff.components;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.Component;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

public class AimComponent implements Component<EntityStore> {


    private boolean isAiming;

    public static final BuilderCodec<AimComponent> CODEC = BuilderCodec
            .builder(AimComponent.class, AimComponent::new)
            .append(new KeyedCodec<>("IsAiming", Codec.BOOLEAN), (c, v) -> c.isAiming = v, c -> c.isAiming)
            .add()
            .build();

    public AimComponent() {
        this.isAiming = false;
    }

    public AimComponent(boolean isAiming) {
        this.isAiming = isAiming;
    }

    public boolean isAiming() {
        return isAiming;
    }

    public void setAiming(boolean aiming) {
        this.isAiming = aiming;
    }

    @NullableDecl
    @Override
    public Component<EntityStore> clone() {
        return new AimComponent(isAiming);
    }
}
