package com.jws.jwsapi.general.weighing;

import androidx.lifecycle.LiveData;

import java.util.List;

import io.reactivex.Single;

public class WeighingService {

    private final WeighingApi weighingApi;
    private final WeighingDao weighingDao;

    public WeighingService(WeighingApi weighingApi,WeighingDao weighingDao){
        this.weighingApi = weighingApi;
        this.weighingDao = weighingDao;
    }

    public Single<WeighingResponse> sendWeighing(WeighingRequest weighingRequest){
        return weighingApi.postNewWeighing(weighingRequest)
                .doOnSuccess(palletResponse -> {

                });
    }

    public LiveData<List<Weighing>> getAllWeighings(){
        return weighingDao.getAllWeighing();
    }

}
