package com.jws.jwsapi.service;

import androidx.lifecycle.ViewModel;

import com.jws.jwsapi.shared.WeighRepository;
import com.service.Balanzas.BalanzaService;

import java.util.Timer;
import java.util.TimerTask;

import io.reactivex.disposables.CompositeDisposable;

public class ServiceViewModel extends ViewModel {

    private final BalanzaService.Balanzas scaleService;
    private final CompositeDisposable compositeDisposable = new CompositeDisposable();
    private final WeighRepository repository;
    private Timer timer;

    public ServiceViewModel(BalanzaService.Balanzas scale, WeighRepository repository) {
        this.scaleService = scale;
        this.repository = repository;
        startUpdatingScale();
    }

    private void startUpdatingScale() {
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                repository.updateNet(scaleService.getNetoStr(repository.getScaleNumber()));
                repository.updateTare(scaleService.getTaraStr(repository.getScaleNumber()));
                repository.updateGross(scaleService.getBrutoStr(repository.getScaleNumber()));
                repository.updateUnit(scaleService.getUnidad(repository.getScaleNumber()));
                repository.updateStable(scaleService.getEstable(repository.getScaleNumber()));
            }
        }, 0, 200);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        compositeDisposable.clear();
        if (timer != null) {
            timer.cancel();
        }
    }

    public BalanzaService.Balanzas getScaleService() {
        return scaleService;
    }
}
