package com.jws.jwsapi.core.gpio;

import com.android.jws.JwsManager;

import javax.inject.Inject;

@SuppressWarnings("unused")
public class GpioManager {

    private final GpioInputsService gpioInputsService;

    @Inject
    public GpioManager(JwsManager jwsManager) {
        gpioInputsService = new GpioInputsService(jwsManager);
    }

    public void setHighListener(GpioHighListener gpioHighListener) {
        this.gpioInputsService.setHighListener(gpioHighListener);
    }

    public void setLowListener(GpioLowListener gpioLowListener) {
        this.gpioInputsService.setLowListener(gpioLowListener);
    }

    public Integer getInputValue1() {
        return gpioInputsService.getCurrentInputValue1();
    }

    public Integer getInputValue2() {
        return gpioInputsService.getCurrentInputValue2();
    }

    public Integer getInputValue3() {
        return gpioInputsService.getCurrentInputValue3();
    }

    public Integer getInputValue4() {
        return gpioInputsService.getCurrentInputValue4();
    }

    public Integer getInputValue5() {
        return gpioInputsService.getCurrentInputValue5();
    }

    public Integer getInputValue6() {
        return gpioInputsService.getCurrentInputValue6();
    }

    public Integer getInputValue7() {
        return gpioInputsService.getCurrentInputValue7();
    }

    public Integer getInputValue8() {
        return gpioInputsService.getCurrentInputValue8();
    }
}
