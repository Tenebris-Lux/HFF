package lucis.lux.components;

import com.hypixel.hytale.component.Component;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

import javax.annotation.Nullable;

public class FirearmStatsComponent implements Component<EntityStore> {

    private double intervalMs;
    private double projectileVelocity;
    private int projectileAmount;
    private double spreadBase;
    private double movementPenalty;
    private double misfireChance;
    private double jamChance;
    private double verticalRecoil;
    private double horizontalRecoil;

    private double elapsedTime;

    public FirearmStatsComponent() {
        this(500f, 10f, 1, 1f, 0f, 0f, 0f, 0.5f, 0.5f);
    }

    public FirearmStatsComponent(double intervalMs, double projectileVelocity, int projectileAmount, double spreadBase, double movementPenalty, double misfireChance, double jamChance, double verticalRecoil, double horizontalRecoil) {
        this.verticalRecoil = verticalRecoil;
        this.spreadBase = spreadBase;
        this.intervalMs = intervalMs;
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
        this.intervalMs = other.intervalMs;
        this.spreadBase = other.spreadBase;
        this.verticalRecoil = other.verticalRecoil;
        this.elapsedTime = other.elapsedTime;
    }

    @Nullable
    @Override
    public Component<EntityStore> clone() {
        return new FirearmStatsComponent(this);
    }


    public double getIntervalMs() {
        return intervalMs;
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
        return elapsedTime >= intervalMs;
    }

    public void setIntervalMs(double intervalMs) {
        this.intervalMs = intervalMs;
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
