package com.jws.jwsapi.general.pallet;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import java.util.List;
import javax.inject.Inject;
import dagger.hilt.android.lifecycle.HiltViewModel;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

@HiltViewModel
public class PalletViewModel extends ViewModel {
    private final PalletService palletService;
    private final CompositeDisposable compositeDisposable = new CompositeDisposable();
    private final LiveData<List<Pallet>> pallets;
    private final MutableLiveData<PalletResponse> palletResponse = new MutableLiveData<>();
    private final MutableLiveData<Boolean> loading = new MutableLiveData<>();
    private final MutableLiveData<String> error = new MutableLiveData<>();
    private final MutableLiveData<Integer> scale = new MutableLiveData<>();
    private final MutableLiveData<String> palletOrigin = new MutableLiveData<>();
    private final MutableLiveData<String> palletDestination = new MutableLiveData<>();

    @Inject
    public PalletViewModel(PalletService palletService) {
        this.palletService = palletService;
        this.pallets = palletService.getAllPallets();
    }

    public LiveData<List<Pallet>> getPallets() {
        return pallets;
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

    public void setScale(Integer scale) {
        this.scale.setValue(scale);
    }

    public void setPalletOrigin(String palletOrigin) {
        this.palletOrigin.setValue(palletOrigin);
    }

    public void setPalletDestination(String palletDestination){
        this.palletDestination.setValue(palletDestination);
    }

    public void createPallet() {
        if(scale.getValue()!=null&&palletOrigin.getValue()!=null&&palletDestination.getValue()!=null){
            PalletRequest palletRequest = new PalletRequest(scale.getValue(), palletOrigin.getValue(), palletDestination.getValue());
            createPalletRequest(palletRequest);
        }else {
            error.setValue("Complete los datos");
        }

    }

    public void createPalletRequest(PalletRequest palletRequest) {
        loading.setValue(true);

        Disposable disposable = palletService.createPallet(palletRequest)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doFinally(() -> loading.setValue(false))
                .subscribe(
                        palletResponse::setValue,
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
