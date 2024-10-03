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

    private final JwsManager jwsManager;
    private final GpioHighListener highListener;
    private final GpioLowListener lowListener;
    private final GpioInputState[] gpioStates = new GpioInputState[4];
    private Disposable pollingDisposable;
    private Integer currentInputValue1 = OFF;
    private Integer currentInputValue2 = OFF;
    private Integer currentInputValue3 = OFF;
    private Integer currentInputValue4 = OFF;
    private int lastInputValue1 = OFF;
    private int lastInputValue2 = OFF;
    private int lastInputValue3 = OFF;
    private int lastInputValue4 = OFF;

    @Inject
    public GpioInputsService(JwsManager jwsManager, GpioHighListener highListener, GpioLowListener lowListener) {
        this.jwsManager = jwsManager;
        this.highListener = highListener;
        this.lowListener = lowListener;
        for (int i = 0; i < 4; i++) {
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
        currentInputValue1 = jwsManager.jwsReadExtrnalGpioValue(0);
        currentInputValue2 = jwsManager.jwsReadExtrnalGpioValue(1);
        currentInputValue3 = jwsManager.jwsReadExtrnalGpioValue(2);
        currentInputValue4 = jwsManager.jwsReadExtrnalGpioValue(3);

        if (!isDataValid()) return;

        checkAndNotify(0, currentInputValue1, lastInputValue1,
                highListener::onInput1High, lowListener::onInput1Low);
        checkAndNotify(1, currentInputValue2, lastInputValue2,
                highListener::onInput2High, lowListener::onInput2Low);
        checkAndNotify(2, currentInputValue3, lastInputValue3,
                highListener::onInput3High, lowListener::onInput3Low);
        checkAndNotify(3, currentInputValue4, lastInputValue4,
                highListener::onInput4High, lowListener::onInput4Low);

        lastInputValue1 = currentInputValue1;
        lastInputValue2 = currentInputValue2;
        lastInputValue3 = currentInputValue3;
        lastInputValue4 = currentInputValue4;

    }

    private boolean isDataValid() {
        return currentInputValue1 != null && currentInputValue2 != null && currentInputValue3 != null && currentInputValue4 != null;
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
        return currentInputValue1;
    }

    public int getCurrentInputValue2() {
        return currentInputValue2;
    }

    public int getCurrentInputValue3() {
        return currentInputValue3;
    }

    public int getCurrentInputValue4() {
        return currentInputValue4;
    }

}
