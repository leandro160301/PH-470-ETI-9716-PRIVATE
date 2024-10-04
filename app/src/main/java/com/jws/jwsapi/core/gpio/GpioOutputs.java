package com.jws.jwsapi.core.gpio;

import com.android.jws.JwsManager;

import javax.inject.Inject;

public class GpioOutputs {

    private final static int OFF = 1;
    private final JwsManager jwsManager;

    @Inject
    public GpioOutputs(JwsManager jwsManager) {
        this.jwsManager = jwsManager;
    }

    public Integer getCurrentOutputValue1() {
        Integer value = jwsManager.jwsReadExtrnalGpioValue(0);
        return value != null ? value : OFF;
    }

    public Integer getCurrentOutputValue2() {
        Integer value = jwsManager.jwsReadExtrnalGpioValue(1);
        return value != null ? value : OFF;
    }

    public Integer getCurrentOutputValue3() {
        Integer value = jwsManager.jwsReadExtrnalGpioValue(2);
        return value != null ? value : OFF;
    }

    public Integer getCurrentOutputValue4() {
        Integer value = jwsManager.jwsReadExtrnalGpioValue(3);
        return value != null ? value : OFF;
    }

    public Integer getCurrentOutputValue5() {
        Integer value = jwsManager.jwsReadExtrnalGpioValue(4);
        return value != null ? value : OFF;
    }

    public Integer getCurrentOutputValue6() {
        Integer value = jwsManager.jwsReadExtrnalGpioValue(5);
        return value != null ? value : OFF;
    }

    public Integer getCurrentOutputValue7() {
        Integer value = jwsManager.jwsReadExtrnalGpioValue(6);
        return value != null ? value : OFF;
    }

    public Integer getCurrentOutputValue8() {
        Integer value = jwsManager.jwsReadExtrnalGpioValue(7);
        return value != null ? value : OFF;
    }

    public void setOutputValueOn1() {
        jwsManager.jwsSetExtrnalGpioValue(0, true);
    }

    public void setOutputValueOn2() {
        jwsManager.jwsSetExtrnalGpioValue(1, true);
    }

    public void setOutputValueOn3() {
        jwsManager.jwsSetExtrnalGpioValue(2, true);
    }

    public void setOutputValueOn4() {
        jwsManager.jwsSetExtrnalGpioValue(3, true);
    }

    public void setOutputValueOn5() {
        jwsManager.jwsSetExtrnalGpioValue(4, true);
    }

    public void setOutputValueOn6() {
        jwsManager.jwsSetExtrnalGpioValue(5, true);
    }

    public void setOutputValueOn7() {
        jwsManager.jwsSetExtrnalGpioValue(6, true);
    }

    public void setOutputValueOn8() {
        jwsManager.jwsSetExtrnalGpioValue(7, true);
    }

    public void setOutputValueOff1() {
        jwsManager.jwsSetExtrnalGpioValue(0, false);
    }

    public void setOutputValueOff2() {
        jwsManager.jwsSetExtrnalGpioValue(1, false);
    }

    public void setOutputValueOff3() {
        jwsManager.jwsSetExtrnalGpioValue(2, false);
    }

    public void setOutputValueOff4() {
        jwsManager.jwsSetExtrnalGpioValue(3, false);
    }

    public void setOutputValueOff5() {
        jwsManager.jwsSetExtrnalGpioValue(4, false);
    }

    public void setOutputValueOff6() {
        jwsManager.jwsSetExtrnalGpioValue(5, false);
    }

    public void setOutputValueOff7() {
        jwsManager.jwsSetExtrnalGpioValue(6, false);
    }

    public void setOutputValueOff8() {
        jwsManager.jwsSetExtrnalGpioValue(7, false);
    }

}
