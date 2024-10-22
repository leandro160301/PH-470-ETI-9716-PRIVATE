package com.jws.jwsapi.weighing;

import androidx.lifecycle.LiveData;

import java.util.List;

public class WeighingService {

    private final WeighingDao weighingDao;

    public WeighingService(WeighingDao weighingDao) {
        this.weighingDao = weighingDao;
    }

    public void newWeighing(Weighing weighing) {
        weighingDao.insertWeighing(weighing);
    }

    public LiveData<List<Weighing>> getAllWeighings() {
        return weighingDao.getAllWeighing();
    }


}
