package com.jws.jwsapi.home;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.jws.jwsapi.MainActivity;
import com.jws.jwsapi.shared.WeighRepository;
import com.service.Balanzas.BalanzaService;
import com.service.PuertosSerie.PuertosSerie2;

import java.util.Timer;
import java.util.TimerTask;

import javax.inject.Inject;

import io.reactivex.disposables.CompositeDisposable;
import jssc.SerialPort;

public class HomeViewModel extends ViewModel {

    @Inject
    HomeService homeService;

    public void print(MainActivity mainActivity, PuertosSerie2 serialPort) {
        homeService.print(mainActivity, serialPort);
    }

}
