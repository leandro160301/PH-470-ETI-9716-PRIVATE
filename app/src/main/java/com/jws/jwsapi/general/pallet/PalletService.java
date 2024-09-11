package com.jws.jwsapi.general.pallet;

import androidx.lifecycle.LiveData;

import java.util.List;

import io.reactivex.Single;

public class PalletService {

    private final PalletApi palletApi;
    private final PalletDao palletDao;

    public PalletService(PalletApi palletApi, PalletDao palletDao) {
        this.palletApi = palletApi;
        this.palletDao = palletDao;
    }

    public Single<PalletResponse> createPallet(PalletRequest palletRequest) {
        return palletApi.postNewPallet(palletRequest)
                .doOnSuccess(palletResponse -> {

                    Pallet pallet = new Pallet();
                    pallet.setOriginPallet(palletRequest.getOriginPallet());
                    pallet.setDestinationPallet(palletRequest.getDestinationPallet());
                    pallet.setScaleNumber(palletRequest.getScaleNumber());
                    pallet.setCode(palletResponse.getCode());
                    pallet.setName(palletResponse.getName());
                    pallet.setQuantity(palletResponse.getQuantity());
                    pallet.setSerialNumber(palletResponse.getSerialNumber());
                    pallet.setClosed(false);
                    pallet.setTotalNet("0");
                    palletDao.insertPallet(pallet);
                });
    }

    public Single<PalletCloseResponse> closePallet(PalletCloseRequest palletCloseRequest){
        return palletApi.closePallet(palletCloseRequest)
                .doOnSuccess(palletCloseResponse -> {
                    if(palletCloseResponse.getStatus()) {
                        palletDao.updatePalletClosedStatus(palletCloseRequest.getSerialNumber(),true);
                    }

                });
    }

    public Single<PalletCloseResponse> deletePallet(PalletCloseRequest palletCloseRequest){
        return palletApi.closePallet(palletCloseRequest)
                .doOnSuccess(palletCloseResponse -> {
                    if(palletCloseResponse.getStatus()) {
                        palletDao.deletePalletBySerialNumber(palletCloseRequest.getSerialNumber());
                    }
                });
    }

    public LiveData<List<Pallet>> getAllPallets() {
        return palletDao.getAllPallets();
    }
}