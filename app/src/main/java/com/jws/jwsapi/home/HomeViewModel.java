package com.jws.jwsapi.home;

import androidx.lifecycle.ViewModel;

import com.jws.jwsapi.MainActivity;
import com.service.PuertosSerie.PuertosSerie2;

import javax.inject.Inject;

public class HomeViewModel extends ViewModel {

    @Inject
    HomeService homeService;

    public void print(MainActivity mainActivity, PuertosSerie2 serialPort) {
        homeService.print(mainActivity, serialPort);
    }

}
