package com.jws.jwsapi.general.weighing;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.jws.jwsapi.general.pallet.Pallet;
import com.jws.jwsapi.general.shared.PalletRepository;

import java.util.List;
import java.util.Objects;

import javax.inject.Inject;
import dagger.hilt.android.lifecycle.HiltViewModel;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

@HiltViewModel
public class WeighingViewModel extends ViewModel {

    private final PalletRepository repository;
    private final WeighingService weighingService;
    private final LiveData<Pallet> currentPallet;
    private final CompositeDisposable compositeDisposable = new CompositeDisposable();

    private final LiveData<List<Weighing>> weighings;
    private final MutableLiveData<WeighingResponse> WeighingResponse = new MutableLiveData<>();
    private final MutableLiveData<Boolean> loading = new MutableLiveData<>();
    private final MutableLiveData<String> error = new MutableLiveData<>();
    private final WeighingDao weighingDao;


    @Inject
    public WeighingViewModel(PalletRepository repository, WeighingService weighingService, WeighingDao weighingDao) {
        this.repository = repository;
        this.weighingService = weighingService;
        this.weighings = weighingService.getAllWeighings();
        this.currentPallet = repository.getCurrentPallet();
        this.weighingDao = weighingDao;
    }

    public LiveData<List<Weighing>> getWeighings() {
        return weighings;
    }

    public LiveData<WeighingResponse> getWeighingResponse() {
        return WeighingResponse;
    }

    public LiveData<Boolean> getLoading() {
        return loading;
    }

    public LiveData<String> getError() {
        return error;
    }

    public LiveData<Pallet> getCurrentPallet() {
        return currentPallet;
    }

    public void createWeighing(Weighing weighing) {
        WeighingRequest WeighingRequest = new WeighingRequest(weighing.getSerialNumber(),weighing.getCode(), weighing.getName(), weighing.getNet(), weighing.getGross(), weighing.getTare());
        createWeighingRequest(WeighingRequest,weighing);
    }

    public void createWeighingRequest(WeighingRequest weighingRequest, Weighing weighing) {
        loading.setValue(true);
        int id= Objects.requireNonNull(repository.getCurrentPallet().getValue()).getId();
        Disposable disposable = weighingService.newWeighing(weighingRequest,weighing, id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doFinally(() -> loading.setValue(false))
                .subscribe(
                        weighingResponse -> {
                            WeighingResponse.setValue(weighingResponse);
                            repository.setCurrentPallet(id);
                        },
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
