package com.jws.jwsapi.general.home;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.jws.jwsapi.general.pallet.PalletService;
import com.service.Balanzas.BalanzaService;

import java.util.Timer;
import java.util.TimerTask;

import io.reactivex.disposables.CompositeDisposable;


public class HomeViewModel extends ViewModel {

    private final BalanzaService.Balanzas scaleService;
    private final CompositeDisposable compositeDisposable = new CompositeDisposable();
    private final MutableLiveData<String> netLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> grossLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> unitLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> tareLiveData = new MutableLiveData<>();
    private Timer timer;

    public HomeViewModel(BalanzaService.Balanzas scale) {
        this.scaleService = scale;
        startUpdatingScale();
    }

    private void startUpdatingScale() {
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                netLiveData.postValue(scaleService.getNetoStr(1));
                grossLiveData.postValue(scaleService.getBrutoStr(1));
                unitLiveData.postValue(scaleService.getUnidad(1));
            }
        }, 0, 200);
    }

    public LiveData<String> getNet() {
        return netLiveData;
    }

    public LiveData<String> getGross() {
        return grossLiveData;
    }

    public LiveData<String> getUnit() {
        return unitLiveData;
    }

    public LiveData<String> getTare() {
        return tareLiveData;
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
