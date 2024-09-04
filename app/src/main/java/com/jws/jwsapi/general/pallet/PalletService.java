package com.jws.jwsapi.general.pallet;

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
                    System.out.println("palletResponse"+palletResponse);
                    // Opcionalmente, guarda en la base de datos local si es necesario
                    // palletDao.insertPallet(palletResponse.toPallet());
                });
    }

}