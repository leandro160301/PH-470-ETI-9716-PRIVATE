package com.jws.jwsapi.service;

import androidx.lifecycle.ViewModel;

import com.jws.jwsapi.shared.WeighRepository;
import com.service.Balanzas.BalanzaService;

import java.util.Timer;
import java.util.TimerTask;

import io.reactivex.disposables.CompositeDisposable;

public class ServiceViewModel extends ViewModel implements ServiceScaleButtons {

    private static final int STABLE_COUNT_THRESHOLD = 15;
    private static final double ZERO_BAND = 49.0;
    private final BalanzaService.Balanzas scaleService;
    private final CompositeDisposable compositeDisposable = new CompositeDisposable();
    private final WeighRepository repository;
    private Timer timer;
    private int counterStable = 0;
    private WeightListener weightListener;
    private boolean weightConformed = false;

    public ServiceViewModel(BalanzaService.Balanzas scale, WeighRepository repository) {
        this.scaleService = scale;
        this.repository = repository;
        repository.setScaleActions(this);
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
                boolean stable = scaleService.getEstable(repository.getScaleNumber());
                repository.updateStable(stable);

                double grossWeight = scaleService.getBruto(repository.getScaleNumber());
                if (stable && grossWeight > ZERO_BAND && !weightConformed) {
                    counterStable++;
                } else {
                    counterStable = 0;
                }

                if (counterStable >= STABLE_COUNT_THRESHOLD) {
                    weightListener.onWeightConformed();
                    weightConformed = true;
                    counterStable = 0;
                }

                if (grossWeight < ZERO_BAND) {
                    weightConformed = false;
                }
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

    public void setWeightListener(WeightListener weightListener) {
        this.weightListener = weightListener;
    }

    @Override
    public void setTare() {
        scaleService.setTaraDigital(repository.getScaleNumber(), scaleService.getBruto(repository.getScaleNumber()));
    }

    @Override
    public void setZero() {
        scaleService.setCero(repository.getScaleNumber());
    }

    public interface WeightListener {
        void onWeightConformed();
    }
}
