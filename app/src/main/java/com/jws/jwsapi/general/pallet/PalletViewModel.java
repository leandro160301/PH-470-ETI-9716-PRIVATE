package com.jws.jwsapi.general.pallet;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

@HiltViewModel
public class PalletViewModel extends ViewModel {
    private final PalletService palletService;
    private final CompositeDisposable compositeDisposable = new CompositeDisposable();

    private final MutableLiveData<PalletResponse> palletResponse = new MutableLiveData<>();
    private final MutableLiveData<Boolean> loading = new MutableLiveData<>();
    private final MutableLiveData<String> error = new MutableLiveData<>();

    @Inject
    public PalletViewModel(PalletService palletService) {
        this.palletService = palletService;
    }

    public LiveData<PalletResponse> getPalletResponse() {
        return palletResponse;
    }

    public LiveData<Boolean> getLoading() {
        return loading;
    }

    public LiveData<String> getError() {
        return error;
    }

    public void createPallet(PalletRequest palletRequest) {
        loading.setValue(true);

        Disposable disposable = palletService.createPallet(palletRequest)
                .doFinally(() -> loading.setValue(false))
                .subscribe(
                        response -> palletResponse.setValue(response),
                        throwable -> error.setValue(throwable.getMessage())
                );

        compositeDisposable.add(disposable);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        compositeDisposable.clear();
    }
}
