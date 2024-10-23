package com.jws.jwsapi.weighing;

import androidx.lifecycle.LiveData;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class WeighingService {

    private final WeighingDao weighingDao;

    public WeighingService(WeighingDao weighingDao) {
        this.weighingDao = weighingDao;
    }

    public Completable newWeighing(Weighing weighing) {
        return Completable.fromAction(() -> weighingDao.insertWeighing(weighing))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public LiveData<List<Weighing>> getAllWeighings() {
        return weighingDao.getAllWeighing();
    }

    public List<Weighing> getAllWeighingsStatic() {
        return weighingDao.getAllWeighingStatic();
    }

    public Completable deleteAllWeighing() {
        return Completable.fromAction(weighingDao::deleteAllWeighings)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

    }


}
