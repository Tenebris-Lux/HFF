package com.hytalefirearmframework.core;

import java.io.File;
import java.io.IOException;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hytalefirearmframework.core.enums.*;
import java.util.concurrent.ConcurrentHashMap;

public abstract class AbstractGun /* extends HytaleRangedWeapon */ implements UseFirearm {

    // misc settings
    protected FirearmCategory category;
    protected boolean twoHanded = true;

    // projectile settings
    protected double rpm = 1.0;
    protected AbstractProjectile projectile;
    protected double projectileVelocity = 20.0;
    protected int projectileAmount = 1;

    // spread settings
    protected double spreadBase;
    protected double movementPenalty;

    //ignition settings
    protected IgnitionType ignitionType;
    protected double misfireChance = 0;
    protected double jamChance = 0;

    // attachments
    protected ConcurrentHashMap<String, AbstractAttachment> attachments = new ConcurrentHashMap<String, AbstractAttachment>();

    // durability
    //protected Effect durabilityEffect = (e) -> System.out.println("Implement this");
    // reload Settings
    protected ReloadMethod reloadMethod;
    // one Action: replace magazine or load one clip/projectile
    protected double reloadTime;
    protected int clipSize = 0;

    protected List<IFirearmEventHandler> reloadSteps;
    protected int reloadCurrStep;
    protected double maxAmmo;

    // recoil settings
    protected double verticalRecoil = 2.0;
    protected double horizontalRecoil = 0.5;

    // firemode settings
    protected FireMode fireMode = FireMode.SEMI_AUTO;

    // temp fields
    protected long lastShotTime = 0;
    protected double currAmmo;
    protected boolean isReloading = false;
    protected long reloadStartTimestamp;

    // handler
    protected IFirearmEventHandler eventHandler;

    ///// Methods /////

    protected AbstractGun() {
    }

    public static <T extends AbstractGun> T jsonInit(String path, Class<T> type) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(new File(path), type);
    }

    public void onDurabilityChange(IFirearmEventHandler e) {
        /* should be inherited directly from Hytale 
        if(this.durability < 0.5){
            durabilityEffect.accept(e);
        }
         */
    }

    @Override
    public FireResult trigger() {
        if (this.reloadCurrStep != 0) {
            return FireResult.fail(FireResult.State.RELOADING);
        }
        if (System.currentTimeMillis() - this.lastShotTime < (60000.0 / this.rpm)) {
            return FireResult.fail(FireResult.State.ON_COOLDOWN);
        }
        if (this.isReloading && (this.reloadMethod == ReloadMethod.INTERNAL_SINGLE || this.reloadMethod == ReloadMethod.INTERNAL_CLIP)) {

            if (this.currAmmo > 0) {
                this.isReloading = false;
            } else {
                return FireResult.fail(FireResult.State.RELOADING);
            }
        } else if (this.isReloading) {
            return FireResult.fail(FireResult.State.RELOADING);
        }
        if (this.currAmmo <= 0) {
            return FireResult.fail(FireResult.State.OUT_OF_AMMO);
        }
        if (this.attachments.containsKey("barrel")) {
            AbstractBarrel barrel = (AbstractBarrel) this.attachments.get("barrel");
            if (barrel.getBroken()) {
                return FireResult.fail(FireResult.State.JAMMED);
            }

            boolean barrelOK = barrel.onShoot();
            if (!barrelOK) {
                return FireResult.fail(FireResult.State.OVERHEATED);
            }
        }

        this.lastShotTime = System.currentTimeMillis();
        this.currAmmo--;

        return FireResult.success(this.calcRecoil(), this.calcCurrentSpread(Stance.STANDING));
    }

    public void onGameTick(double deltaTime) {
        if (this.attachments.containsKey("barrel")) {
            ((AbstractBarrel) this.attachments.get("barrel")).updateCooling(deltaTime);
        }
        if (this.isReloading) {
            long now = System.currentTimeMillis();
            long timePassed = now - this.reloadStartTimestamp;

            switch (this.reloadMethod) {
                case MAGAZINE ->
                    handleMagazineReload(timePassed);
                 //case ROD, ROPE ->
                   //  handleMuzzleReload(timePassed);
                case INTERNAL_CLIP ->
                    handleClipReload(timePassed);
                case INTERNAL_SINGLE ->
                    handleSingleReload(timePassed);
                case OTHER ->
                    handleOtherReload();
            }
        }
    }

    public boolean startReload() {
        if (this.currAmmo >= this.maxAmmo) {
            return false;
        }
        if (this.isReloading) {
            return false;
        }

        this.isReloading = true;
        this.reloadStartTimestamp = System.currentTimeMillis();

        // start animations, etc.
        return true;
    }

    public void setProjectile(AbstractProjectile projectile) {
        this.projectile = projectile;
    }

    public void changeAttachment(String slot, AbstractAttachment attachment) {
        if (this.attachments != null) {
            this.attachments.put(slot, attachment);
        } else {
            this.attachments = new ConcurrentHashMap<String, AbstractAttachment>();
            this.attachments.put(slot, attachment);
        }
    }

    // will get Stance from Hytale itself
    public double calcCurrentSpread(Stance currStance) {
        double finalSpread = this.spreadBase;

        if (currStance == Stance.RUNNING || currStance == Stance.JUMPING) {
            finalSpread += this.movementPenalty;
        } else if (currStance == Stance.CROUCHING) {
            finalSpread *= 0.7;
        }

        return Math.max(0.0, finalSpread);
    }

    public double[] calcRecoil() {
        double vRecoil = this.verticalRecoil;
        double hRecoil = (Math.random() - 0.5) * 2 * this.horizontalRecoil;

        return new double[]{vRecoil, hRecoil};
    }

    public boolean isAuto() {
        return this.fireMode == FireMode.FULL_AUTO;
    }

    private void handleMagazineReload(long timePassed) {
        if (timePassed >= (this.reloadTime * 1000)) {
            this.currAmmo = this.maxAmmo;
            this.isReloading = false;
        }
    }

    private void handleSingleReload(long timePassed) {
        if (timePassed >= (this.reloadTime * 1000)) {
            this.currAmmo++;

            this.reloadStartTimestamp = System.currentTimeMillis();

            if (this.currAmmo >= this.maxAmmo) {
                this.isReloading = false;
            }
        }
    }

    // will not compile without Hytale
    /*private void handleMuzzleReload(long timePassed) {

        if (this.reloadSteps.get(this.reloadCurrStep).hasFinished()) {

            this.reloadCurrStep++;

            if (this.reloadSteps.size() < this.reloadCurrStep) {
                this.reloadCurrStep = 0;
                this.currAmmo = 1;
                this.isReloading = false;
                return;
            }

            this.reloadSteps.get(this.reloadCurrStep).start();
        }
    }*/
    private void handleClipReload(long timePassed) {
        if (timePassed >= (this.reloadTime * 1000)) {

            this.currAmmo += Math.min(this.clipSize, (int) (this.maxAmmo - this.currAmmo));
            this.reloadStartTimestamp = System.currentTimeMillis();

            if (this.currAmmo >= this.maxAmmo) {
                this.currAmmo = this.maxAmmo;
                this.isReloading = false;
            }
        }
    }

    private void handleOtherReload() {
        throw new UnsupportedOperationException("You have to overwrite this method to implement your own reload method.");
    }
}
