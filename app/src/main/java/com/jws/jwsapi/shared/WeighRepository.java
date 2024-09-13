package com.jws.jwsapi.shared;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import javax.inject.Singleton;

@Singleton
public class WeighRepository {
    private final MutableLiveData<String> net = new MutableLiveData<>();
    private final MutableLiveData<String> gross = new MutableLiveData<>();
    private final MutableLiveData<String> unit = new MutableLiveData<>();
    private final MutableLiveData<String> tare = new MutableLiveData<>();
    private final MutableLiveData<Boolean> stable = new MutableLiveData<>();

    public LiveData<String> getNet() {
        return net;
    }

    public LiveData<String> getGross() {
        return gross;
    }

    public LiveData<String> getUnit() {
        return unit;
    }

    public LiveData<String> getTare() {
        return tare;
    }

    public LiveData<Boolean> getStable() {
        return stable;
    }

    public void updateNet(String newNet) {
        if (!newNet.equals(net.getValue())) {
            net.postValue(newNet);
        }
    }

    public void updateGross(String newGross) {
        if (!newGross.equals(gross.getValue())) {
            gross.postValue(newGross);
        }
    }

    public void updateTare(String newTare) {
        if (!newTare.equals(tare.getValue())) {
            tare.postValue(newTare);
        }
    }

    public void updateUnit(String newUnit) {
        if (!newUnit.equals(unit.getValue())) {
            unit.postValue(newUnit);
        }
    }

    public void updateStable(Boolean newStable) {
        if (!newStable.equals(stable.getValue())) {
            stable.postValue(newStable);
        }
    }

}