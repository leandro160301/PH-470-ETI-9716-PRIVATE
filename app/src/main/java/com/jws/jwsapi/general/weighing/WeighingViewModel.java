package com.jws.jwsapi.general.weighing;

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
public class WeighingViewModel extends ViewModel {
    private final WeighingService weighingService;
    private final CompositeDisposable compositeDisposable = new CompositeDisposable();
    private final LiveData<List<Weighing>> weighings;
    private final MutableLiveData<WeighingResponse> WeighingResponse = new MutableLiveData<>();
    private final MutableLiveData<Boolean> loading = new MutableLiveData<>();
    private final MutableLiveData<String> error = new MutableLiveData<>();
    private final MutableLiveData<Integer> scale = new MutableLiveData<>();

    @Inject
    public WeighingViewModel(WeighingService weighingService) {
        this.weighingService = weighingService;
        this.weighings = weighingService.getAllWeighings();
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

    public void setScale(Integer scale) {
        this.scale.setValue(scale);
    }

    public void createWeighing(Weighing weighing) {
        if(scale.getValue()!=null){
            WeighingRequest WeighingRequest = new WeighingRequest(weighing.getSerialNumber(),weighing.getCode(), weighing.getName(), weighing.getNet(), weighing.getGross(), weighing.getTare());
            createWeighingRequest(WeighingRequest,weighing);
        }else {
            error.setValue("Complete los datos");
        }

    }

    public void createWeighingRequest(WeighingRequest weighingRequest, Weighing weighing) {
        loading.setValue(true);

        Disposable disposable = weighingService.newWeighing(weighingRequest,weighing)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doFinally(() -> loading.setValue(false))
                .subscribe(
                        WeighingResponse::setValue,
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
