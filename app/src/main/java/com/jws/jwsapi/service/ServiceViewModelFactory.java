package com.jws.jwsapi.service;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.jws.jwsapi.core.data.local.PreferencesHelper;
import com.jws.jwsapi.shared.WeighRepository;
import com.service.Balanzas.BalanzaService;

public class ServiceViewModelFactory implements ViewModelProvider.Factory {
    private final BalanzaService.Balanzas scale;
    private final WeighRepository repository;
    private final PreferencesHelper preferencesHelper;

    public ServiceViewModelFactory(BalanzaService.Balanzas scale, WeighRepository repository, PreferencesHelper preferencesHelper) {
        this.scale = scale;
        this.repository = repository;
        this.preferencesHelper = preferencesHelper;
    }

    @SuppressWarnings("unchecked")
    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(ServiceViewModel.class)) {
            return (T) new ServiceViewModel(scale, repository, preferencesHelper);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}