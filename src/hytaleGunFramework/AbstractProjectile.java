package hytaleGunFramework;

// TODO: imports for Hytale

/**
 * This class describes a projectile, like a bullet or a laser beam.
 */
public abstract class AbstractProjectile {
    protected double baseDamage = 1.0;
    protected double damageFalloff = 10.0;
    // TODO: more attributes?

    public double getDamage(double distance){
        double falloff = distance / damageFalloff;
        return baseDamage / falloff;
    }
}
