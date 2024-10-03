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
    private Disposable pollingDisposable;

    private int currentInputValue1 = OFF;
    private int currentInputValue2 = OFF;
    private int currentInputValue3 = OFF;
    private int currentInputValue4 = OFF;

    private int lastInputValue1 = OFF;
    private int lastInputValue2 = OFF;
    private int lastInputValue3 = OFF;
    private int lastInputValue4 = OFF;

    private final boolean[] waitHigh = new boolean[4];
    private final boolean[] waitLow = new boolean[4];
    private final int[] stableHighCounts = new int[4];
    private final int[] stableLowCounts = new int[4];



    @Inject
    public GpioInputsService(JwsManager jwsManager, GpioHighListener highListener, GpioLowListener lowListener) {
        this.jwsManager = jwsManager;
        this.highListener = highListener;
        this.lowListener = lowListener;
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

    private void checkAndNotify(int inputNumber, int currentInput, int lastInput,
                                Runnable onHighCallback, Runnable onLowCallback) {

        if (currentInput == ON && lastInput == OFF) {
            waitHigh[inputNumber] = true;
        }
        if (waitHigh[inputNumber] && currentInput == OFF) {
            waitHigh[inputNumber] = false;
            stableHighCounts[inputNumber] = 0;
        }
        processStateChange(waitHigh, inputNumber, stableHighCounts, onHighCallback);

        if (currentInput == OFF && lastInput == ON) {
            waitLow[inputNumber] = true;
        }
        if (waitLow[inputNumber] && currentInput == ON) {
            waitLow[inputNumber] = false;
            stableLowCounts[inputNumber] = 0;
        }
        processStateChange(waitLow, inputNumber, stableLowCounts, onLowCallback);
    }

    private void processStateChange(boolean[] wait, int inputNumber, int[] stableCounts, Runnable onCallback) {
        if (wait[inputNumber]) {
            if (stableCounts[inputNumber] >= STABLE_THRESHOLD) {
                onCallback.run();
                stableCounts[inputNumber] = 0;
                wait[inputNumber] = false;
            }
            stableCounts[inputNumber]++;
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
