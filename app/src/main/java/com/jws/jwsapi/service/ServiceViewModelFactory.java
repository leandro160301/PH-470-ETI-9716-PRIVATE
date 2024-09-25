package com.jws.jwsapi.service;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.jws.jwsapi.shared.WeighRepository;
import com.service.Balanzas.BalanzaService;

public class ServiceViewModelFactory implements ViewModelProvider.Factory {
    private final BalanzaService.Balanzas scale;
    private final WeighRepository repository;

    public ServiceViewModelFactory(BalanzaService.Balanzas scale, WeighRepository repository) {
        this.scale = scale;
        this.repository = repository;
    }

    @SuppressWarnings("unchecked")
    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(ServiceViewModel.class)) {
            return (T) new ServiceViewModel(scale, repository);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}