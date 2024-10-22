package com.jws.jwsapi.home;

import androidx.lifecycle.ViewModel;

import com.jws.jwsapi.MainActivity;
import com.service.PuertosSerie.PuertosSerie2;

public class HomeViewModel extends ViewModel {

    private final HomeService homeService;

    public HomeViewModel(HomeService homeService) {
        this.homeService = homeService;
    }

    public void print(MainActivity mainActivity, PuertosSerie2 serialPort) {
        homeService.print(mainActivity, serialPort);
    }

}
