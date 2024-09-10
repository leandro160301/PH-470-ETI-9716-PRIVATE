package com.jws.jwsapi.general.home;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import com.service.Balanzas.BalanzaService;

public class HomeViewModelFactory implements ViewModelProvider.Factory {
    private final BalanzaService.Balanzas scale;

    public HomeViewModelFactory(BalanzaService.Balanzas scale) {
        this.scale = scale;
    }

    @SuppressWarnings("unchecked")
    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(HomeViewModel.class)) {
            return (T) new HomeViewModel(scale);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}