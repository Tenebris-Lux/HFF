package lucis.lux.hff.components;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.Component;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import lucis.lux.hff.components.enums.FireMode;
import lucis.lux.hff.components.enums.FirearmClass;
import lucis.lux.hff.components.enums.FirearmType;

import javax.annotation.Nullable;

public class FirearmStatsComponent implements Component<EntityStore> {

    public static final BuilderCodec<FirearmStatsComponent> CODEC = BuilderCodec.builder(FirearmStatsComponent.class, FirearmStatsComponent::new)
            .append(new KeyedCodec<>("RPM", Codec.DOUBLE), (c, v) -> c.rpm = v, c -> c.rpm)
            .add()
            .append(new KeyedCodec<>("ProjectileVelocity", Codec.DOUBLE), (c, v) -> c.projectileVelocity = v, c -> c.projectileVelocity)
            .add()
            .append(new KeyedCodec<>("ProjectileAmount", Codec.INTEGER), (c, v) -> c.projectileAmount = v, c -> c.projectileAmount)
            .add()
            .append(new KeyedCodec<>("SpreadBase", Codec.DOUBLE), (c, v) -> c.spreadBase = v, c -> c.spreadBase)
            .add()
            .append(new KeyedCodec<>("MovementPenalty", Codec.DOUBLE), (c, v) -> c.movementPenalty = v, c -> c.movementPenalty)
            .add()
            .append(new KeyedCodec<>("MisfireChance", Codec.DOUBLE), (c, v) -> c.misfireChance = v, c -> c.misfireChance)
            .add()
            .append(new KeyedCodec<>("JamChance", Codec.DOUBLE), (c, v) -> c.jamChance = v, c -> c.jamChance)
            .add()
            .append(new KeyedCodec<>("VerticalRecoil", Codec.DOUBLE), (c, v) -> c.verticalRecoil = v, c -> c.verticalRecoil)
            .add()
            .append(new KeyedCodec<>("HorizontalRecoil", Codec.DOUBLE), (c, v) -> c.horizontalRecoil = v, c -> c.horizontalRecoil)
            .add()
            .append(new KeyedCodec<>("Disabled", Codec.BOOLEAN), (c, v) -> c.disabled = v, c -> c.disabled)
            .add()
            .append(new KeyedCodec<>("ReloadTime", Codec.FLOAT), (c, v) -> c.reloadTime = v, c -> c.reloadTime)
            .add()
            .append(new KeyedCodec<>("ProjectileCapacity", Codec.INTEGER), (c, v) -> c.projectileCapacity = v, c -> c.projectileCapacity)
            .add()
            .append(new KeyedCodec<>("AmmoName", Codec.STRING_ARRAY), (c, v) -> c.ammoName = v, c -> c.ammoName)
            .add()
            .build();
    public static final KeyedCodec<FirearmStatsComponent> KEY = new KeyedCodec<>("HFF_FIREARM_COMPONENT", CODEC);
    Ref<EntityStore> playerRef;
    private float reloadTime;
    private double rpm;
    private double projectileVelocity;
    private int projectileAmount;
    private int projectileCapacity;
    private double spreadBase;
    private double movementPenalty;
    private double misfireChance;
    private double jamChance;
    private double verticalRecoil;
    private double horizontalRecoil;
    private FirearmClass firearmClass;
    private FirearmType firearmType;
    private FireMode fireMode;
    private boolean disabled;
    private String[] ammoName;

    public FirearmStatsComponent() {
        this(1f, 1f, 1, 0f, 0f, 0f, 0f, 0f, 0f, false, 1, 1.0f, new String[0]);
    }

    public FirearmStatsComponent(double rpm, double projectileVelocity, int projectileAmount, double spreadBase, double movementPenalty, double misfireChance, double jamChance, double verticalRecoil, double horizontalRecoil, boolean disabled, int projectileCapacity, float reloadTime, String[] ammoName) {
        this.verticalRecoil = verticalRecoil;
        this.spreadBase = spreadBase;
        this.rpm = rpm;
        this.projectileVelocity = projectileVelocity;
        this.projectileAmount = projectileAmount;
        this.movementPenalty = movementPenalty;
        this.misfireChance = misfireChance;
        this.jamChance = jamChance;
        this.horizontalRecoil = horizontalRecoil;
        this.disabled = disabled;
        this.projectileCapacity = projectileCapacity;
        this.reloadTime = reloadTime;
        this.ammoName = ammoName;
    }

    public FirearmStatsComponent(FirearmStatsComponent other) {
        this.horizontalRecoil = other.horizontalRecoil;
        this.jamChance = other.jamChance;
        this.misfireChance = other.misfireChance;
        this.movementPenalty = other.movementPenalty;
        this.projectileAmount = other.projectileAmount;
        this.projectileVelocity = other.projectileVelocity;
        this.rpm = other.rpm;
        this.spreadBase = other.spreadBase;
        this.verticalRecoil = other.verticalRecoil;
        this.projectileCapacity = other.projectileCapacity;
        this.disabled = other.disabled;
        this.reloadTime = other.reloadTime;
        this.ammoName = other.ammoName;
        this.playerRef = other.playerRef;
    }

    @Nullable
    @Override
    public Component<EntityStore> clone() {
        return new FirearmStatsComponent(this);
    }


    public double getRpm() {
        return rpm;
    }

    public void setRpm(double rpm) {
        this.rpm = rpm;
    }

    public double getProjectileVelocity() {
        return projectileVelocity;
    }

    public void setProjectileVelocity(double projectileVelocity) {
        this.projectileVelocity = projectileVelocity;
    }

    public int getProjectileAmount() {
        return projectileAmount;
    }

    public void setProjectileAmount(int projectileAmount) {
        this.projectileAmount = projectileAmount;
    }

    public double getSpreadBase() {
        return spreadBase;
    }

    public void setSpreadBase(double spreadBase) {
        this.spreadBase = spreadBase;
    }

    public double getMovementPenalty() {
        return movementPenalty;
    }

    public void setMovementPenalty(double movementPenalty) {
        this.movementPenalty = movementPenalty;
    }

    public double getMisfireChance() {
        return misfireChance;
    }

    public void setMisfireChance(double misfireChance) {
        this.misfireChance = misfireChance;
    }

    public double getJamChance() {
        return jamChance;
    }

    public void setJamChance(double jamChance) {
        this.jamChance = jamChance;
    }

    public double getVerticalRecoil() {
        return verticalRecoil;
    }

    public void setVerticalRecoil(double verticalRecoil) {
        this.verticalRecoil = verticalRecoil;
    }

    public boolean isDisabled() {
        return disabled;
    }

    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }

    public double getHorizontalRecoil() {
        return horizontalRecoil;
    }

    public void setHorizontalRecoil(double horizontalRecoil) {
        this.horizontalRecoil = horizontalRecoil;
    }

    public FirearmClass getFirearmClass() {
        return firearmClass;
    }

    public void setFirearmClass(FirearmClass firearmClass) {
        this.firearmClass = firearmClass;
    }

    public FirearmType getFirearmType() {
        return firearmType;
    }

    public void setFirearmType(FirearmType firearmType) {
        this.firearmType = firearmType;
    }

    public FireMode getFireMode() {
        return fireMode;
    }

    public void setFireMode(FireMode fireMode) {
        this.fireMode = fireMode;
    }

    public float getCooldown() {
        return (float) (60 / rpm);
    }

    public int getProjectileCapacity() {
        return projectileCapacity;
    }

    public void setProjectileCapacity(int projectileCapacity) {
        this.projectileCapacity = projectileCapacity;
    }

    public float getReloadTime() {
        return reloadTime;
    }

    public void setReloadTime(float reloadTime) {
        this.reloadTime = reloadTime;
    }

    public String[] getAmmoName() {
        return ammoName;
    }

    public void setAmmoName(String[] ammoName) {
        this.ammoName = ammoName;
    }

    public Ref<EntityStore> getPlayerRef() {
        return playerRef;
    }

    public void setPlayerRef(Ref<EntityStore> playerRef) {
        this.playerRef = playerRef;
    }
}
