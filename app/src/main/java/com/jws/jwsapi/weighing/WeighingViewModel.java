package com.jws.jwsapi.weighing;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.jws.jwsapi.shared.UserRepository;

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
    private final MutableLiveData<WeighingResponse> weighingResponse = new MutableLiveData<>();
    private final MutableLiveData<Boolean> loading = new MutableLiveData<>();
    private final MutableLiveData<String> errorRequest = new MutableLiveData<>();
    private final MutableLiveData<String> error = new MutableLiveData<>();
    private final UserRepository userRepository;

    @Inject
    public WeighingViewModel( WeighingService weighingService, UserRepository userRepository) {
        this.weighingService = weighingService;
        this.userRepository = userRepository;
        this.weighings = weighingService.getAllWeighings();

    }


    public LiveData<List<Weighing>> getWeighings() {
        return weighings;
    }

    public LiveData<WeighingResponse> getWeighingResponse() {
        return weighingResponse;
    }

    public LiveData<Boolean> getLoading() {
        return loading;
    }

    public LiveData<String> getErrorRequest() {
        return errorRequest;
    }

    public LiveData<String> getError() {
        return error;
    }

    public void createWeighing(String gross, String net, String tare, String unit) {

    }

    public void createWeighingRequest(WeighingRequest weighingRequest, Weighing weighing) {
        loading.setValue(true);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        compositeDisposable.clear();
    }
}
