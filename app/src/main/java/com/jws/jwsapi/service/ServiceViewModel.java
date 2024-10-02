package com.jws.jwsapi.service;

import androidx.lifecycle.ViewModel;

import com.jws.jwsapi.core.data.local.PreferencesHelper;
import com.jws.jwsapi.shared.WeighRepository;
import com.service.Balanzas.BalanzaService;

import io.reactivex.disposables.CompositeDisposable;

public class ServiceViewModel extends ViewModel implements ServiceScaleButtons {

    private final BalanzaService.Balanzas scaleService;
    private final WeighRepository repository;
    private final CompositeDisposable compositeDisposable = new CompositeDisposable();
    private final WeightConformationManager weightConformationManager;
    private final ScalePollingService scalePollingService;

    public ServiceViewModel(BalanzaService.Balanzas scale, WeighRepository repository, PreferencesHelper preferencesHelper) {
        this.scaleService = scale;
        this.repository = repository;
        this.weightConformationManager = new WeightConformationManager(repository, new WeightPreferences(preferencesHelper));
        this.scalePollingService = new ScalePollingService(weightConformationManager, scaleService, repository);
        this.repository.setScaleActions(this);
        this.scalePollingService.startPolling();
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        compositeDisposable.clear();
        this.scalePollingService.stopPolling();
    }

    public void setWeightListener(WeightListener weightListener) {
        this.weightConformationManager.setWeightListener(weightListener);
    }

    public BalanzaService.Balanzas getScaleService() {
        return scaleService;
    }

    @Override
    public void setTare() {
        scaleService.setTaraDigital(repository.getScaleNumber(), scaleService.getBruto(repository.getScaleNumber()));
    }

    @Override
    public void setZero() {
        scaleService.setCero(repository.getScaleNumber());
    }

}
