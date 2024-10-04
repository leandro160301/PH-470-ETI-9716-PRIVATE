package com.jws.jwsapi.core.gpio;

import com.android.jws.JwsManager;

import javax.inject.Inject;

@SuppressWarnings("unused")
public class GpioManager {

    private final GpioInputsService gpioInputsService;
    private final GpioOutputs gpioOutputs;

    @Inject
    public GpioManager(JwsManager jwsManager) {
        gpioInputsService = new GpioInputsService(jwsManager);
        gpioOutputs = new GpioOutputs(jwsManager);
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

    public Integer getOutputValue1() {
        return gpioOutputs.getCurrentOutputValue1();
    }

    public Integer getOutputValue2() {
        return gpioOutputs.getCurrentOutputValue2();
    }

    public Integer getOutputValue3() {
        return gpioOutputs.getCurrentOutputValue3();
    }

    public Integer getOutputValue4() {
        return gpioOutputs.getCurrentOutputValue4();
    }

    public Integer getOutputValue5() {
        return gpioOutputs.getCurrentOutputValue5();
    }

    public Integer getOutputValue6() {
        return gpioOutputs.getCurrentOutputValue6();
    }

    public Integer getOutputValue7() {
        return gpioOutputs.getCurrentOutputValue7();
    }

    public Integer getOutputValue8() {
        return gpioOutputs.getCurrentOutputValue8();
    }

    public void setOutputValueOn1() {
        gpioOutputs.setOutputValueOn1();
    }

    public void setOutputValueOn2() {
        gpioOutputs.setOutputValueOn2();
    }

    public void setOutputValueOn3() {
        gpioOutputs.setOutputValueOn3();
    }

    public void setOutputValueOn4() {
        gpioOutputs.setOutputValueOn4();
    }

    public void setOutputValueOn5() {
        gpioOutputs.setOutputValueOn5();
    }

    public void setOutputValueOn6() {
        gpioOutputs.setOutputValueOn6();
    }

    public void setOutputValueOn7() {
        gpioOutputs.setOutputValueOn7();
    }

    public void setOutputValueOn8() {
        gpioOutputs.setOutputValueOn8();
    }

    public void setOutputValueOff1() {
        gpioOutputs.setOutputValueOff1();
    }

    public void setOutputValueOff2() {
        gpioOutputs.setOutputValueOff2();
    }

    public void setOutputValueOff3() {
        gpioOutputs.setOutputValueOff3();
    }

    public void setOutputValueOff4() {
        gpioOutputs.setOutputValueOff4();
    }

    public void setOutputValueOff5() {
        gpioOutputs.setOutputValueOff5();
    }

    public void setOutputValueOff6() {
        gpioOutputs.setOutputValueOff6();
    }

    public void setOutputValueOff7() {
        gpioOutputs.setOutputValueOff7();
    }

    public void setOutputValueOff8() {
        gpioOutputs.setOutputValueOff8();
    }


}
