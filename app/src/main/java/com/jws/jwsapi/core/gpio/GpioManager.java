package com.jws.jwsapi.core.gpio;

import static com.jws.jwsapi.core.gpio.GpioConstants.OFF;

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

    public Integer getGpioValue(int index) {
        switch (index) {
            case 1:
                return gpioInputsService.getCurrentInputValue1();
            case 2:
                return gpioInputsService.getCurrentInputValue2();
            case 3:
                return gpioInputsService.getCurrentInputValue3();
            case 4:
                return gpioInputsService.getCurrentInputValue4();
            case 5:
                return gpioInputsService.getCurrentInputValue5();
            case 6:
                return gpioInputsService.getCurrentInputValue6();
            case 7:
                return gpioInputsService.getCurrentInputValue7();
            case 8:
                return gpioInputsService.getCurrentInputValue8();
            default:
                return OFF;
        }
    }

}
