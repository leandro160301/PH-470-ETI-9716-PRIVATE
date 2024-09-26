package com.jws.jwsapi.weighing;

import androidx.lifecycle.LiveData;

import com.jws.jwsapi.pallet.PalletDao;

import java.util.List;

import io.reactivex.Single;

public class WeighingService {

    private final WeighingApi weighingApi;
    private final WeighingDao weighingDao;
    private final PalletDao palletDao;

    public WeighingService(WeighingApi weighingApi, WeighingDao weighingDao, PalletDao palletDao) {
        this.weighingApi = weighingApi;
        this.weighingDao = weighingDao;
        this.palletDao = palletDao;
    }

    public Single<WeighingResponse> newWeighing(WeighingRequest weighingRequest, Weighing weighing, int id) {
        return weighingApi.postNewWeighing(weighingRequest)
                .doOnSuccess(palletResponse -> {
                    weighingDao.insertWeighing(weighing);
                    palletDao.incrementDoneById(id);
                    palletDao.updatePalletTotalNet(id, weighing.getNet());
                });
    }


    public LiveData<List<Weighing>> getAllWeighings() {
        return weighingDao.getAllWeighing();
    }

    public List<Weighing> getAllWeighingsStatic() {
        return weighingDao.getAllWeighingStatic();
    }

}
