package com.hytalefirearmframework.core;

import java.io.IOException;
import java.io.File;

import com.fasterxml.jackson.databind.ObjectMapper;

public abstract class AbstractAttachment {

    protected String name;
    protected double recoilMultiplier = 1.0;
    protected double spreadMultiplier = 1.0;
    protected double damageMultiplier = 1.0;
    protected double noiseMultiplier = 1.0;

    public double getRecoilMult() {
        return this.recoilMultiplier;
    }

    public double getSpreadMult() {
        return this.spreadMultiplier;
    }

    public double getDamageMult() {
        return this.damageMultiplier;
    }

    public double getNoiseMult() {
        return this.noiseMultiplier;
    }

    public static <T extends AbstractAttachment> T jsonInit(String path, Class<T> type) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(new File(path), type);
    }
}
