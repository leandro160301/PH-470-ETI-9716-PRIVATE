package com.jws.jwsapi.weighing;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.jws.jwsapi.productionline.ProductionLine;
import com.jws.jwsapi.productionline.ProductionLineManager;
import com.jws.jwsapi.shared.UserRepository;

import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import io.reactivex.disposables.CompositeDisposable;

@HiltViewModel
public class WeighingViewModel extends ViewModel {

    private final WeighingService weighingService;
    private final CompositeDisposable compositeDisposable = new CompositeDisposable();
    private final LiveData<List<Weighing>> weighings;
    private final MutableLiveData<String> error = new MutableLiveData<>();
    private final UserRepository userRepository;
    private final ProductionLineManager productionLineManager;

    @Inject
    public WeighingViewModel(WeighingService weighingService, UserRepository userRepository, ProductionLineManager productionLineManager) {
        this.weighingService = weighingService;
        this.userRepository = userRepository;
        this.productionLineManager = productionLineManager;
        this.weighings = weighingService.getAllWeighings();
    }

    public LiveData<List<Weighing>> getWeighings() {
        return weighings;
    }

    public LiveData<String> getError() {
        return error;
    }

    public void createWeighing(String gross, String net, String tare, String unit) {
        Weighing weighing = new Weighing();
        ProductionLine productionLine = productionLineManager.getCurrentProductionLine();
        if (productionLine == null) return;
        weighing.setBatch(productionLine.getBatch());
        weighing.setCaliber(productionLine.getCaliber());
        weighing.setBoxTare(productionLine.getBoxTare());
        weighing.setDestination(productionLine.getDestinatation());
        weighing.setGross(gross);
        weighing.setExpirateDate(productionLine.getExpirateDate());
        weighing.setIceTare(productionLine.getIceTare());
        weighing.setNet(net);
        weighing.setTopTare(productionLine.getTopTare());
        weighing.setPartsTare(productionLine.getPartsTare());
        weighing.setProduct(productionLine.getProduct());
        weighing.setOperator(userRepository.getCurrentUser());
        weighingService.newWeighing(weighing);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        compositeDisposable.clear();
    }
}
