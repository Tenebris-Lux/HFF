package com.hytalefirearmframework.complex;

import java.io.File;
import java.io.IOException;

import com.fasterxml.jackson.databind.ObjectMapper;

// TODO: imports...

public abstract class AbstractBarrel {
    protected boolean overheatEnabled = false;
    protected boolean isBroken = false;
    protected double heatMax;
    protected double heatCurr = 0;
    protected double heatPerShot;
    protected double coolOffSpeed;
    protected double coolOffDelay = 0.5;

    protected AbstractBarrel(){}

    public static <T extends AbstractBarrel>T jsonInit(String path, Class<T> type) throws IOException{
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(new File(path), type);
    }

    public boolean getBroken(){
        return this.isBroken;
    }

    public double getHeat(){
        return this.heatCurr;
    }
}
