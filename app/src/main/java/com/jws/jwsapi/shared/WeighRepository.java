package com.jws.jwsapi.shared;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import javax.inject.Singleton;

@Singleton
public class WeighRepository {
    private final MutableLiveData<String> netLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> grossLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> unitLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> tareLiveData = new MutableLiveData<>();

    public LiveData<String> getNet() {
        return netLiveData;
    }

    public LiveData<String> getGross() {
        return grossLiveData;
    }

    public LiveData<String> getUnit() {
        return unitLiveData;
    }

    public LiveData<String> getTare() {
        return tareLiveData;
    }

    public void updateNet(String net) {
        netLiveData.postValue(net);
    }

    public void updateGross(String gross) {
        grossLiveData.postValue(gross);
    }

    public void updateUnit(String unit) {
        unitLiveData.postValue(unit);
    }

    public void updateTare(String tare) {
        tareLiveData.postValue(tare);
    }
}