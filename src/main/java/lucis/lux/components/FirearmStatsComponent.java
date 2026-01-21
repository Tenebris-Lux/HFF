package lucis.lux.components;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.Component;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

import javax.annotation.Nullable;

public class FirearmStatsComponent implements Component<EntityStore> {

    private double rpm;
    private double projectileVelocity;
    private int projectileAmount;
    private double spreadBase;
    private double movementPenalty;
    private double misfireChance;
    private double jamChance;
    private double verticalRecoil;
    private double horizontalRecoil;

    private double elapsedTime = 0.0;

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
            .build();

    public FirearmStatsComponent() {
        this(1, 10f, 1, 1f, 0f, 0f, 0f, 0.5f, 0.5f);
    }

    public FirearmStatsComponent(double rpm, double projectileVelocity, int projectileAmount, double spreadBase, double movementPenalty, double misfireChance, double jamChance, double verticalRecoil, double horizontalRecoil) {
        this.verticalRecoil = verticalRecoil;
        this.spreadBase = spreadBase;
        this.rpm = rpm;
        this.projectileVelocity = projectileVelocity;
        this.projectileAmount = projectileAmount;
        this.movementPenalty = movementPenalty;
        this.misfireChance = misfireChance;
        this.jamChance = jamChance;
        this.horizontalRecoil = horizontalRecoil;

        this.elapsedTime = 0.0;
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
        this.elapsedTime = other.elapsedTime;
    }

    @Nullable
    @Override
    public Component<EntityStore> clone() {
        return new FirearmStatsComponent(this);
    }


    public double getRpm() {
        return rpm;
    }

    public double getProjectileVelocity() {
        return projectileVelocity;
    }

    public int getProjectileAmount() {
        return projectileAmount;
    }

    public double getSpreadBase() {
        return spreadBase;
    }

    public double getMovementPenalty() {
        return movementPenalty;
    }

    public double getMisfireChance() {
        return misfireChance;
    }

    public double getJamChance() {
        return jamChance;
    }

    public double getVerticalRecoil() {
        return verticalRecoil;
    }

    public double getHorizontalRecoil() {
        return horizontalRecoil;
    }

    public double getElapsedTime() {
        return elapsedTime;
    }

    public boolean isTimeElapsed() {
        return elapsedTime >= 1 / rpm ;
    }

    public void setRpm(double rpm) {
        this.rpm = rpm;
    }

    public void setProjectileVelocity(double projectileVelocity) {
        this.projectileVelocity = projectileVelocity;
    }

    public void setProjectileAmount(int projectileAmount) {
        this.projectileAmount = projectileAmount;
    }

    public void setSpreadBase(double spreadBase) {
        this.spreadBase = spreadBase;
    }

    public void setMovementPenalty(double movementPenalty) {
        this.movementPenalty = movementPenalty;
    }

    public void setMisfireChance(double misfireChance) {
        this.misfireChance = misfireChance;
    }

    public void setJamChance(double jamChance) {
        this.jamChance = jamChance;
    }

    public void setVerticalRecoil(double verticalRecoil) {
        this.verticalRecoil = verticalRecoil;
    }

    public void setHorizontalRecoil(double horizontalRecoil) {
        this.horizontalRecoil = horizontalRecoil;
    }

    public void increaseElapsedTime(float dt) {
        this.elapsedTime += dt;
    }

    public void resetElapsedTime() {
        this.elapsedTime = 0.0;
    }
}
