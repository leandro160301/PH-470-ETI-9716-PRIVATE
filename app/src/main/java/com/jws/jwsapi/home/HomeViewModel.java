package com.jws.jwsapi.home;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.jws.jwsapi.shared.WeighRepository;
import com.service.Balanzas.BalanzaService;

import java.util.Timer;
import java.util.TimerTask;

import io.reactivex.disposables.CompositeDisposable;

public class HomeViewModel extends ViewModel {

    private final BalanzaService.Balanzas scaleService;
    private final CompositeDisposable compositeDisposable = new CompositeDisposable();
    private final WeighRepository repository;
    private Timer timer;

    public HomeViewModel(BalanzaService.Balanzas scale,WeighRepository repository) {
        this.scaleService = scale;
        this.repository = repository;
        startUpdatingScale();
    }

    private void startUpdatingScale() {
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                repository.updateNet(scaleService.getNetoStr(1));
                repository.updateGross(scaleService.getBrutoStr(1));
                repository.updateUnit(scaleService.getUnidad(1));
            }
        }, 0, 200);
    }

    public LiveData<String> getNet() {
        return repository.getNet();
    }

    public LiveData<String> getGross() {
        return repository.getGross();
    }

    public LiveData<String> getUnit() {
        return repository.getUnit();
    }

    public LiveData<String> getTare() {
        return repository.getTare();
    }


    @Override
    protected void onCleared() {
        super.onCleared();
        compositeDisposable.clear();
        if (timer != null) {
            timer.cancel();
        }
    }
}
