package com.hytalefirearmframework.complex;

import java.io.File;
import java.io.IOException;
import java.util.List;
import org.w3c.dom.events.Event;

import com.fasterxml.jackson.databind.ObjectMapper;

public abstract class AbstractGun  /* extends HytaleRangedWeapon */ implements UseFirearm {
    protected FirearmCategory category;
    protected boolean twoHanded = true;
    protected double attackDelay = 1.0;
    protected AbstractProjectile projectile;
    protected double projectileVelocity = 20.0;
    protected int projectileAmount = 1;
    protected double spreadBase;
    protected double movementPenalty;
    protected IgnitionType ignitionType;
    protected double misfireChance = 0;
    protected double jamChance = 0;
    protected AbstractBarrel barrel;
    //protected Effect durabilityEffect = (e) -> System.out.println("Implement this");
    protected ReloadMethod reloadMethod;
    protected double reloadTime;
    protected List<Event> reloadSteps;
    protected int reloadCurrStep;
    protected double ammoCapacity;

    protected AbstractGun(){}

    public static <T extends AbstractGun> T jsonInit(String path, Class<T> type) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(new File(path), type);
    }

    public void onDurabilityChange(Event e){
        /* should be inherited directly from a Hytale 
        if(this.durability < 0.5){
            durabilityEffect.accept(e);
        }
        */
    }

    public void setProjectile(AbstractProjectile projectile){
        this.projectile = projectile;
    }

    public void setBarrel(AbstractBarrel barrel){
        if(barrel != null){
        this.barrel = barrel;
    }}
}


