package com.hytalefirearmframework.core;

public class FireResult {

    public enum State {
        SUCCESS, OUT_OF_AMMO, ON_COOLDOWN, RELOADING, OVERHEATED, JAMMED
    }

    private final State state;
    private final double[] recoil;
    private final double spread;

    private FireResult(State state, double[] recoil, double spread) {
        this.state = state;
        this.spread = spread;
        this.recoil = recoil;
    }

    public static FireResult success(double[] recoil, double spread) {
        return new FireResult(State.SUCCESS, recoil, spread);
    }

    public static FireResult fail(State reason) {
        return new FireResult(reason, new double[]{0, 0}, 0);
    }

    public boolean isSuccess() {
        return this.state == State.SUCCESS;
    }

    public State getState() {
        return this.state;
    }

    public double[] getRecoil() {
        return this.recoil;
    }

    public double getSpread() {
        return this.spread;
    }
}
