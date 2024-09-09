package com.jws.jwsapi.general.home;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import com.jws.jwsapi.general.pallet.PalletService;
import com.service.Balanzas.BalanzaService;

public class HomeViewModelFactory implements ViewModelProvider.Factory {
    private final PalletService palletService;
    private final BalanzaService.Balanzas scale;

    public HomeViewModelFactory(PalletService palletService, BalanzaService.Balanzas scale) {
        this.palletService = palletService;
        this.scale = scale;
    }

    @SuppressWarnings("unchecked")
    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(HomeViewModel.class)) {
            return (T) new HomeViewModel(palletService, scale);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}