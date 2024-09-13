package com.jws.jwsapi.home;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.jws.jwsapi.shared.WeighRepository;
import com.service.Balanzas.BalanzaService;

public class HomeViewModelFactory implements ViewModelProvider.Factory {
    private final BalanzaService.Balanzas scale;
    private final WeighRepository repository;

    public HomeViewModelFactory(BalanzaService.Balanzas scale, WeighRepository repository) {
        this.scale = scale;
        this.repository = repository;
    }

    @SuppressWarnings("unchecked")
    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(HomeViewModel.class)) {
            return (T) new HomeViewModel(scale,repository);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}