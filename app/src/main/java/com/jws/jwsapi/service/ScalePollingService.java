package com.jws.jwsapi.service;

import com.jws.jwsapi.shared.WeighRepository;
import com.service.Balanzas.BalanzaService;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class ScalePollingService {

    private final static int PERIOD = 200;
    private final WeightConformationManager weightConformationManager;
    private final BalanzaService.Balanzas scaleService;
    private final WeighRepository repository;
    private Disposable pollingDisposable;

    public ScalePollingService(WeightConformationManager weightConformationManager, BalanzaService.Balanzas scaleService, WeighRepository repository) {
        this.weightConformationManager = weightConformationManager;
        this.scaleService = scaleService;
        this.repository = repository;
    }

    public void startPolling() {
        pollingDisposable = Observable.interval(PERIOD, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(tick -> updateScaleData());
    }

    private void updateScaleData() {
        repository.updateNetStr(scaleService.getNetoStr(repository.getScaleNumber()));
        repository.updateTare(scaleService.getTaraStr(repository.getScaleNumber()));
        repository.updateGrossStr(scaleService.getBrutoStr(repository.getScaleNumber()));
        repository.updateUnit(scaleService.getUnidad(repository.getScaleNumber()));
        repository.updateNet(scaleService.getNeto(repository.getScaleNumber()));
        repository.updateGross(scaleService.getBruto(repository.getScaleNumber()));

        boolean stable = scaleService.getEstable(repository.getScaleNumber());
        repository.updateStable(stable);

        weightConformationManager.evaluateWeightConformation(stable);
    }

    public void stopPolling() {
        if (pollingDisposable != null && !pollingDisposable.isDisposed()) {
            pollingDisposable.dispose();
        }
    }

}
