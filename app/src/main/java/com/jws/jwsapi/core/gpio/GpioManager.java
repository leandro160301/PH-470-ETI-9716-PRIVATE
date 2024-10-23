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
                return gpioInputsService.getCurrentValue(0);
            case 2:
                return gpioInputsService.getCurrentValue(1);
            case 3:
                return gpioInputsService.getCurrentValue(2);
            case 4:
                return gpioInputsService.getCurrentValue(3);
            case 5:
                return gpioInputsService.getCurrentValue(4);
            case 6:
                return gpioInputsService.getCurrentValue(5);
            case 7:
                return gpioInputsService.getCurrentValue(6);
            case 8:
                return gpioInputsService.getCurrentValue(7);
            default:
                return OFF;
        }
    }

}
