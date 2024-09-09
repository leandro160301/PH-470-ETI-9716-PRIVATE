package com.jws.jwsapi.general.shared;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.jws.jwsapi.general.pallet.Pallet;
import com.jws.jwsapi.general.pallet.PalletDao;

public class PalletRepository {

    private final PalletDao palletDao;
    private LiveData<Pallet> currentPallet = new MutableLiveData<>();

    public PalletRepository(PalletDao palletDao) {
        this.palletDao = palletDao;
    }

    /**
     * Si hacemos un leve update del elemento room no lo actualizara porque crees que no es necesario
     */
    public LiveData<Pallet> getCurrentPallet() {
        return currentPallet;
    }

    public void setCurrentPallet(int palletId) {
        currentPallet = palletDao.getPalletById(palletId);
    }

}