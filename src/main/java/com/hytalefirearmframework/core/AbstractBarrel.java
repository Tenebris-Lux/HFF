package com.hytalefirearmframework.core;

// TODO: imports...
public abstract class AbstractBarrel extends AbstractAttachment {

    protected boolean overheatEnabled = false;
    protected boolean isBroken = false;

    protected double heatMax = 100;
    protected double heatCurr = 0;
    protected double heatPerShot = 10.0;

    protected double coolOffSpeed = 5.0;
    protected double coolOffDelay = 500.0;

    protected transient long lastShotTime = 0;

    protected AbstractBarrel() {
    }

    public boolean onShoot() {
        if (this.isBroken) {
            return false;
        }

        this.lastShotTime = System.currentTimeMillis();

        if (this.overheatEnabled) {
            this.heatCurr += this.heatPerShot;

            if (this.heatCurr >= this.heatMax) {
                this.heatCurr = this.heatMax;
                return false;
            }
        }
        return true;
    }

    public void updateCooling(double deltaTimeSeconds) {
        if (!this.overheatEnabled || this.heatCurr <= 0) {
            return;
        }

        long timeSinceShot = System.currentTimeMillis() - this.lastShotTime;
        if (timeSinceShot < this.coolOffDelay) {
            return;
        }

        double coolingAmount = coolOffSpeed * deltaTimeSeconds;
        this.heatCurr -= coolingAmount;

        if (this.heatCurr < 0) {
            this.heatCurr = 0;
        }
    }

    public boolean getBroken() {
        return this.isBroken;
    }

    public float getHeatPercentage() {
        return (float) (this.heatCurr / this.heatMax);
    }
}
