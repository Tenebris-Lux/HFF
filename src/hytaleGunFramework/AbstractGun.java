package hytaleGunFramework;

import java.util.Vector;
import org.w3c.dom.events.Event;

public abstract class AbstractGun implements UseFirearm {
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
    protected Vector<Event> reloadSteps;
    protected int reloadCurrStep;
    protected double ammoCapacity;

    public AbstractGun(String init){
        // TODO: initialize gun from JSON
    }

    public void onDurabilityChange(Event e){
        /* should be inherited directly from a Hytale 
        if(this.durability < 0.5){
            durabilityEffect.accept(e);
        }
        */
    }
}


