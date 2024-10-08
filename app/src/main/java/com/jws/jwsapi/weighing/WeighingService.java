package com.jws.jwsapi.weighing;

import androidx.lifecycle.LiveData;


import java.util.List;

import io.reactivex.Single;

public class WeighingService {

    private final WeighingApi weighingApi;
    private final WeighingDao weighingDao;

    public WeighingService(WeighingApi weighingApi, WeighingDao weighingDao) {
        this.weighingApi = weighingApi;
        this.weighingDao = weighingDao;
    }

    public Single<WeighingResponse> newWeighing(WeighingRequest weighingRequest, Weighing weighing) {
        return weighingApi.postNewWeighing(weighingRequest)
                .doOnSuccess(palletResponse -> {
                    weighingDao.insertWeighing(weighing);
                });
    }


    public LiveData<List<Weighing>> getAllWeighings() {
        return weighingDao.getAllWeighing();
    }

    public List<Weighing> getAllWeighingsStatic() {
        return weighingDao.getAllWeighingStatic();
    }

}
