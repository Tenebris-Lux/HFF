package com.hytalefirearmframework.complex;

import java.io.File;
import java.io.IOException;

import com.fasterxml.jackson.databind.ObjectMapper;

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

    protected AbstractProjectile(){}

    public static <T extends AbstractProjectile> T jsonInit(String path, Class<T> type) throws IOException{
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(new File(path), type);
    }
}
