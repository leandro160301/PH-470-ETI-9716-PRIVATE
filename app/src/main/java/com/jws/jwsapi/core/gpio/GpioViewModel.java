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
    private final MutableLiveData<Integer> inputValue1 = new MutableLiveData<>();
    private final MutableLiveData<Integer> inputValue2 = new MutableLiveData<>();
    private final MutableLiveData<Integer> inputValue3 = new MutableLiveData<>();
    private final MutableLiveData<Integer> inputValue4 = new MutableLiveData<>();
    private final MutableLiveData<Integer> inputValue5 = new MutableLiveData<>();
    private final MutableLiveData<Integer> inputValue6 = new MutableLiveData<>();
    private final MutableLiveData<Integer> inputValue7 = new MutableLiveData<>();
    private final MutableLiveData<Integer> inputValue8 = new MutableLiveData<>();
    private Disposable pollingDisposable;

    @Inject
    public GpioViewModel(GpioManager gpioManager) {
        this.gpioManager = gpioManager;
        startPolling();
    }

    public void startPolling() {
        pollingDisposable = Observable.interval(PERIOD, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(tick -> fetchGpioValues());
    }


    private void fetchGpioValues() {
        inputValue1.setValue(gpioManager.getInputValue1());
        inputValue2.setValue(gpioManager.getInputValue2());
        inputValue3.setValue(gpioManager.getInputValue3());
        inputValue4.setValue(gpioManager.getInputValue4());
        inputValue5.setValue(gpioManager.getInputValue5());
        inputValue6.setValue(gpioManager.getInputValue6());
        inputValue7.setValue(gpioManager.getInputValue7());
        inputValue8.setValue(gpioManager.getInputValue8());
    }

    public LiveData<Integer> getInputValue1() {
        return inputValue1;
    }

    public LiveData<Integer> getInputValue2() {
        return inputValue2;
    }

    public LiveData<Integer> getInputValue3() {
        return inputValue3;
    }

    public LiveData<Integer> getInputValue4() {
        return inputValue4;
    }

    public LiveData<Integer> getInputValue5() {
        return inputValue5;
    }

    public LiveData<Integer> getInputValue6() {
        return inputValue6;
    }

    public LiveData<Integer> getInputValue7() {
        return inputValue7;
    }

    public LiveData<Integer> getInputValue8() {
        return inputValue8;
    }

    public void setOutputValueOn1() {
        gpioManager.setOutputValueOn1();
    }

    public void setOutputValueOn2() {
        gpioManager.setOutputValueOn2();
    }

    public void setOutputValueOn3() {
        gpioManager.setOutputValueOn3();
    }

    public void setOutputValueOn4() {
        gpioManager.setOutputValueOn4();
    }

    public void setOutputValueOn5() {
        gpioManager.setOutputValueOn5();
    }

    public void setOutputValueOn6() {
        gpioManager.setOutputValueOn6();
    }

    public void setOutputValueOn7() {
        gpioManager.setOutputValueOn7();
    }

    public void setOutputValueOn8() {
        gpioManager.setOutputValueOn8();
    }

    public void setOutputValueOff1() {
        gpioManager.setOutputValueOff1();
    }

    public void setOutputValueOff2() {
        gpioManager.setOutputValueOff2();
    }

    public void setOutputValueOff3() {
        gpioManager.setOutputValueOff3();
    }

    public void setOutputValueOff4() {
        gpioManager.setOutputValueOff4();
    }

    public void setOutputValueOff5() {
        gpioManager.setOutputValueOff5();
    }

    public void setOutputValueOff6() {
        gpioManager.setOutputValueOff6();
    }

    public void setOutputValueOff7() {
        gpioManager.setOutputValueOff7();
    }

    public void setOutputValueOff8() {
        gpioManager.setOutputValueOff8();
    }

    public void stopPolling() {
        if (pollingDisposable != null && !pollingDisposable.isDisposed()) {
            pollingDisposable.dispose();
        }
    }


}
