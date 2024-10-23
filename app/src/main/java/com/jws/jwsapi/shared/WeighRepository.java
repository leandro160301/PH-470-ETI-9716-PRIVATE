package com.jws.jwsapi.shared;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.jws.jwsapi.scale.ScaleOperations;

import javax.inject.Singleton;

@Singleton
public class WeighRepository {
    private final MutableLiveData<String> netStr = new MutableLiveData<>();
    private final MutableLiveData<String> grossStr = new MutableLiveData<>();
    private final MutableLiveData<String> unit = new MutableLiveData<>();
    private final MutableLiveData<String> tare = new MutableLiveData<>();
    private final MutableLiveData<Boolean> stable = new MutableLiveData<>();
    private float net = 0;
    private float gross = 0;
    private int scaleNumber = 1;
    private ScaleOperations serviceScaleOperations;

    public void setScaleActions(ScaleOperations serviceScaleOperations) {
        this.serviceScaleOperations = serviceScaleOperations;
    }

    public LiveData<String> getNetStr() {
        return netStr;
    }

    public LiveData<String> getGrossStr() {
        return grossStr;
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

    public int getScaleNumber() {
        return scaleNumber;
    }

    public float getNet() {
        return net;
    }

    public float getGross() {
        return gross;
    }

    public void updateScaleNumber(int scaleNumber) {
        this.scaleNumber = scaleNumber;
    }

    public void updateNet(float newNet) {
        this.net = newNet;
    }

    public void updateGross(float newGross) {
        this.gross = newGross;
    }

    public void updateNetStr(String newNet) {
        if (!newNet.equals(netStr.getValue())) {
            netStr.postValue(newNet);
        }
    }

    public void updateGrossStr(String newGross) {
        if (!newGross.equals(grossStr.getValue())) {
            grossStr.postValue(newGross);
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

    public void setZero() {
        if (serviceScaleOperations != null) serviceScaleOperations.setZero();
    }

    public void setTare() {
        if (serviceScaleOperations != null) serviceScaleOperations.setTare();
    }

    public String format(String weight) {
        if (serviceScaleOperations != null) {
            return serviceScaleOperations.format(weight);
        } else {
            return "0";
        }
    }

}