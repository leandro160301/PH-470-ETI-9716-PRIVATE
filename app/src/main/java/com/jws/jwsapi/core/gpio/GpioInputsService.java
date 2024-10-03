package com.jws.jwsapi.core.gpio;

import com.android.jws.JwsManager;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

@SuppressWarnings("unused")
public class GpioInputsService {

    private final static int OFF = 1;
    private final static int ON = 0;
    private final static int PERIOD = 1; // 1 ms polling
    private final static int STABLE_THRESHOLD = 100; // 100 ms
    private final static int INPUT_LENGHT = 4;

    private final JwsManager jwsManager;
    private final GpioHighListener highListener;
    private final GpioLowListener lowListener;
    private final GpioInputState[] gpioStates = new GpioInputState[INPUT_LENGHT];
    private final Integer[] currentInputValues = {OFF, OFF, OFF, OFF};
    private final int[] lastInputValues = {OFF, OFF, OFF, OFF};
    private Disposable pollingDisposable;

    @Inject
    public GpioInputsService(JwsManager jwsManager, GpioHighListener highListener, GpioLowListener lowListener) {
        this.jwsManager = jwsManager;
        this.highListener = highListener;
        this.lowListener = lowListener;
        for (int i = 0; i < INPUT_LENGHT; i++) {
            gpioStates[i] = new GpioInputState();
        }
        startPolling();
    }
    
    public void startPolling() {
        pollingDisposable = Observable.interval(PERIOD, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.io())
                .subscribe(tick -> updateGpioData());
    }

    private void updateGpioData() {
        for (int i = 0; i < INPUT_LENGHT; i++) {
            int index = i;
            currentInputValues[index] = jwsManager.jwsReadExtrnalGpioValue(index);
            if (currentInputValues[index] != null) {
                checkAndNotify(index, currentInputValues[index], lastInputValues[index],
                        () -> highListener.onInputHigh(index), () -> lowListener.onInputLow(index));
                lastInputValues[index] = currentInputValues[index];
            }
        }

    }

    private void checkAndNotify(int inputNumber, int currentInput, int lastInput,
                                Runnable onHighCallback, Runnable onLowCallback) {

        GpioInputState gpioState = gpioStates[inputNumber];

        if (currentInput == ON && lastInput == OFF) {
            gpioState.setOnWaitHigh();
        }
        if (gpioState.isWaitHigh() && currentInput == OFF) {
            gpioState.resetWaitHigh();
            gpioState.resetStableHighCount();
        }
        if (gpioState.isWaitHigh()) {
            processStateChange(gpioState, gpioState.getStableHighCount(), gpioState::incrementStableHighCount,
                    gpioState::resetStableHighCount, onHighCallback, gpioState::resetWaitHigh);
        }


        if (currentInput == OFF && lastInput == ON) {
            gpioState.setOnWaitLow();
        }
        if (gpioState.isWaitLow() && currentInput == ON) {
            gpioState.resetWaitLow();
            gpioState.resetStableLowCount();
        }
        if (gpioState.isWaitLow()) {
            processStateChange(gpioState, gpioState.getStableLowCount(), gpioState::incrementStableLowCount,
                    gpioState::resetStableLowCount, onLowCallback, gpioState::resetWaitLow);
        }

    }

    private void processStateChange(GpioInputState gpioState,
                                    int stableCount, Runnable incrementCount,
                                    Runnable resetCount, Runnable onCallback, Runnable resetWait) {
        if (stableCount >= STABLE_THRESHOLD) {
            onCallback.run();
            resetCount.run();
            resetWait.run();
        } else {
            incrementCount.run();
        }
    }

    public void stopPolling() {
        if (pollingDisposable != null && !pollingDisposable.isDisposed()) {
            pollingDisposable.dispose();
        }
    }

    public int getCurrentInputValue1() {
        return currentInputValues[0];
    }

    public int getCurrentInputValue2() {
        return currentInputValues[1];
    }

    public int getCurrentInputValue3() {
        return currentInputValues[2];
    }

    public int getCurrentInputValue4() {
        return currentInputValues[3];
    }

}
