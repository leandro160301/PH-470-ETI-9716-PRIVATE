package com.jws.jwsapi.core.gpio;

public class GpioInputState {
    private boolean waitHigh;
    private boolean waitLow;
    private int stableHighCount;
    private int stableLowCount;

    public GpioInputState() {
        this.waitHigh = false;
        this.waitLow = false;
        this.stableHighCount = 0;
        this.stableLowCount = 0;
    }

    public boolean isWaitHigh() {
        return waitHigh;
    }

    public void setOnWaitHigh() {
        this.waitHigh = true;
    }

    public boolean isWaitLow() {
        return waitLow;
    }

    public void setOnWaitLow() {
        this.waitLow = true;
    }

    public void resetWaitLow() {
        this.waitLow = false;
    }

    public void resetWaitHigh() {
        this.waitHigh = false;
    }

    public int getStableHighCount() {
        return stableHighCount;
    }

    public void incrementStableHighCount() {
        this.stableHighCount++;
    }

    public void resetStableHighCount() {
        this.stableHighCount = 0;
    }

    public int getStableLowCount() {
        return stableLowCount;
    }

    public void incrementStableLowCount() {
        this.stableLowCount++;
    }

    public void resetStableLowCount() {
        this.stableLowCount = 0;
    }
}