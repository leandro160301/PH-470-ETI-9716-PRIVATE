package com.jws.jwsapi.general.weighing;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.jws.jwsapi.common.users.UsersManager;
import com.jws.jwsapi.general.pallet.Pallet;
import com.jws.jwsapi.general.shared.PalletRepository;

import java.util.List;

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
    private final CompositeDisposable compositeDisposable = new CompositeDisposable();
    private final LiveData<List<Weighing>> weighings;
    private final MutableLiveData<WeighingResponse> weighingResponse = new MutableLiveData<>();
    private final MutableLiveData<Boolean> loading = new MutableLiveData<>();
    private final MutableLiveData<String> errorRequest = new MutableLiveData<>();
    private final MutableLiveData<String> error = new MutableLiveData<>();
    private final LiveData<Pallet> currentPallet;
    private final UsersManager usersManager;

    @Inject
    public WeighingViewModel(PalletRepository repository, WeighingService weighingService, UsersManager usersManager) {
        this.repository = repository;
        this.weighingService = weighingService;
        this.usersManager = usersManager;
        this.weighings = weighingService.getAllWeighings();
        this.currentPallet = repository.getCurrentPallet();

    }

    public LiveData<Pallet> getCurrentPallet() {
        return currentPallet;
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
        Weighing weighing= new Weighing();
        Pallet pallet= getCurrentPallet().getValue();
        if(pallet!=null) {
            weighing.setCode(pallet.getCode());
            weighing.setGross(gross);
            weighing.setTare(net);
            weighing.setNet(tare);
            weighing.setUnit(unit);
            weighing.setName(pallet.getName());
            weighing.setOperator(usersManager.getUsuarioActual());
            weighing.setIdPallet(pallet.getId());
            weighing.setScaleNumber(pallet.getScaleNumber());
            weighing.setQuantity(pallet.getQuantity());
            weighing.setSerialNumber(pallet.getSerialNumber());
            WeighingRequest weighingRequest = new WeighingRequest(weighing.getSerialNumber(),weighing.getCode(), weighing.getName(), weighing.getNet(), weighing.getGross(), weighing.getTare());
            createWeighingRequest(weighingRequest,weighing);
        }else{
            error.setValue("pallet null");
        }
    }

    public void createWeighingRequest(WeighingRequest weighingRequest, Weighing weighing) {
        loading.setValue(true);
        Integer id= repository.getCurrentPalletId();
        if(id!=null&&id>-1) {
            Disposable disposable = weighingService.newWeighing(weighingRequest,weighing, id)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .doFinally(() -> loading.setValue(false))
                    .subscribe(
                            weighingResponse::setValue,
                            throwable -> errorRequest.setValue(throwable.getMessage())
                    );
            compositeDisposable.add(disposable);
        }else{
            error.setValue("id null");
        }
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        compositeDisposable.clear();
    }
}
