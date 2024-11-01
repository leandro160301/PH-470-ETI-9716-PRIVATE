package com.jws.jwsapi.core.gpio;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

@HiltViewModel
public class GpioViewModel extends ViewModel {

    private final static int PERIOD = 50;
    private final GpioManager gpioManager;
    private final GpioOutputs gpioOutputs;
    private final MutableLiveData<Integer> gpioValue1 = new MutableLiveData<>();
    private final MutableLiveData<Integer> gpioValue2 = new MutableLiveData<>();
    private final MutableLiveData<Integer> gpioValue3 = new MutableLiveData<>();
    private final MutableLiveData<Integer> gpioValue4 = new MutableLiveData<>();
    private final MutableLiveData<Integer> gpioValue5 = new MutableLiveData<>();
    private final MutableLiveData<Integer> gpioValue6 = new MutableLiveData<>();
    private final MutableLiveData<Integer> gpioValue7 = new MutableLiveData<>();
    private final MutableLiveData<Integer> gpioValue8 = new MutableLiveData<>();
    private Disposable pollingDisposable;

    @Inject
    public GpioViewModel(GpioManager gpioManager, GpioOutputs gpioOutputs) {
        this.gpioManager = gpioManager;
        this.gpioOutputs = gpioOutputs;
        startPolling();
    }

    public void startPolling() {
        pollingDisposable = Observable.interval(PERIOD, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(tick -> updateGpioValues());
    }

    private void updateGpioValues() {
        gpioValue1.setValue(gpioManager.getGpioValue(1));
        gpioValue2.setValue(gpioManager.getGpioValue(2));
        gpioValue3.setValue(gpioManager.getGpioValue(3));
        gpioValue4.setValue(gpioManager.getGpioValue(4));
        gpioValue5.setValue(gpioManager.getGpioValue(5));
        gpioValue6.setValue(gpioManager.getGpioValue(6));
        gpioValue7.setValue(gpioManager.getGpioValue(7));
        gpioValue8.setValue(gpioManager.getGpioValue(8));
    }

    public LiveData<Integer> getGpioValue1() {
        return gpioValue1;
    }

    public LiveData<Integer> getGpioValue2() {
        return gpioValue2;
    }

    public LiveData<Integer> getGpioValue3() {
        return gpioValue3;
    }

    public LiveData<Integer> getGpioValue4() {
        return gpioValue4;
    }

    public LiveData<Integer> getGpioValue5() {
        return gpioValue5;
    }

    public LiveData<Integer> getGpioValue6() {
        return gpioValue6;
    }

    public LiveData<Integer> getGpioValue7() {
        return gpioValue7;
    }

    public LiveData<Integer> getGpioValue8() {
        return gpioValue8;
    }

    public void setOutputValueOn(int gpioIndex) {
        gpioOutputs.setOutputValueOn(gpioIndex);
    }

    public void setOutputValueOff(int gpioIndex) {
        gpioOutputs.setOutputValueOff(gpioIndex);
    }

    public void stopPolling() {
        if (pollingDisposable != null && !pollingDisposable.isDisposed()) {
            pollingDisposable.dispose();
        }
    }


}
