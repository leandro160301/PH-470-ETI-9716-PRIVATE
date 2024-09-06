package com.jws.jwsapi.general.shared;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.jws.jwsapi.general.pallet.Pallet;

public class PalletRepository {

    private final MutableLiveData<Pallet> currentPallet = new MutableLiveData<>();

    public LiveData<Pallet> getCurrentPallet() {
        return currentPallet;
    }

    public void setCurrentPallet(Pallet pallet) {
        currentPallet.setValue(pallet);
    }

}