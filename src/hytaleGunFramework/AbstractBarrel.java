package hytaleGunFramework;

// TODO: imports...

public abstract class AbstractBarrel {
    protected boolean overheatEnabled = false;
    protected boolean isBroken = false;
    protected double heatMax;
    protected double heatCurr = 0;
    protected double heatPerShot;
    protected double coolOffSpeed;
    protected double coolOffDelay = 0.5;

    public AbstractBarrel(String source){
        // TODO: maybe read data from a JSON file
    }

    public boolean getBroken(){
        return this.isBroken;
    }

    public double getHeat(){
        return this.heatCurr;
    }
}
