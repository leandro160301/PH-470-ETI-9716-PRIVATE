package com.jws.jwsapi.scale;

import com.jws.jwsapi.shared.WeighRepository;
import com.service.Balanzas.BalanzaService;

import dagger.assisted.Assisted;
import dagger.assisted.AssistedFactory;
import dagger.assisted.AssistedInject;
import io.reactivex.disposables.CompositeDisposable;

public class ScaleViewModel extends androidx.lifecycle.ViewModel implements ScaleButtons {

    private final BalanzaService.Balanzas scaleService;
    private final WeighRepository repository;
    private final CompositeDisposable compositeDisposable = new CompositeDisposable();
    private final WeightConformationManager weightConformationManager;
    private final ScalePollingService scalePollingService;

    @AssistedInject
    public ScaleViewModel(
            @Assisted BalanzaService.Balanzas scale,
            WeighRepository repository,
            WeightConformationManager weightConformationManager) {
        this.weightConformationManager = weightConformationManager;
        this.scaleService = scale;
        this.repository = repository;
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

    public void setWeightListener(WeightConformationListener weightConformationListener) {
        this.weightConformationManager.setWeightListener(weightConformationListener);
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

    @AssistedFactory
    public interface Factory {
        ScaleViewModel create(BalanzaService.Balanzas scaleService);
    }

}
