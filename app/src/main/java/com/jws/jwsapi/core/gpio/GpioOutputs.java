package com.jws.jwsapi.core.gpio;

import com.android.jws.JwsManager;

import javax.inject.Inject;

public class GpioOutputs {

    private final JwsManager jwsManager;

    @Inject
    public GpioOutputs(JwsManager jwsManager) {
        this.jwsManager = jwsManager;
    }

    public void setOutputValue(int gpioIndex, boolean value) {
        jwsManager.jwsSetExtrnalGpioValue(gpioIndex, value);
    }

    public void setOutputValueOn(int gpioIndex) {
        setOutputValue(gpioIndex, true);
    }

    public void setOutputValueOff(int gpioIndex) {
        setOutputValue(gpioIndex, false);
    }

}
