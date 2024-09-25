package com.jws.jwsapi.shared;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.jws.jwsapi.pallet.Pallet;
import com.jws.jwsapi.pallet.PalletDao;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class PalletRepository {

    private final PalletDao palletDao;
    private LiveData<Pallet> currentPallet;
    private Integer currentPalletId;

    @Inject
    public PalletRepository(PalletDao palletDao) {
        this.palletDao = palletDao;
        currentPallet = new MutableLiveData<>();
        currentPalletId = null;
    }

    public LiveData<Pallet> getCurrentPallet() {
        return currentPallet;
    }

    public void setCurrentPallet(int palletId) {
        currentPallet = palletDao.getPalletById(palletId, false);
        currentPalletId = palletId;
    }

    public Integer getCurrentPalletId() {
        return currentPalletId;
    }
}