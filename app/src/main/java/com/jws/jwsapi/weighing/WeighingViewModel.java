package com.jws.jwsapi.weighing;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.jws.jwsapi.productionline.ProductionLine;
import com.jws.jwsapi.productionline.ProductionLineManager;
import com.jws.jwsapi.productionline.ProductionLineStates;
import com.jws.jwsapi.shared.UserRepository;
import com.jws.jwsapi.shared.WeighRepository;
import com.jws.jwsapi.utils.date.DateUtils;

import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import io.reactivex.CompletableObserver;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

@HiltViewModel
public class WeighingViewModel extends ViewModel {

    private final WeighingService weighingService;
    private final CompositeDisposable compositeDisposable = new CompositeDisposable();
    private final LiveData<List<Weighing>> weighings;
    private final MutableLiveData<String> error = new MutableLiveData<>();
    private final MutableLiveData<String> message = new MutableLiveData<>();
    private final UserRepository userRepository;
    private final ProductionLineManager productionLineManager;
    private final WeighRepository weighRepository;

    @Inject
    public WeighingViewModel(WeighingService weighingService, UserRepository userRepository, ProductionLineManager productionLineManager, WeighRepository weighRepository) {
        this.weighingService = weighingService;
        this.userRepository = userRepository;
        this.productionLineManager = productionLineManager;
        this.weighRepository = weighRepository;
        this.weighings = weighingService.getAllWeighings();
    }

    public LiveData<List<Weighing>> getWeighings() {
        return weighings;
    }

    public LiveData<String> getError() {
        return error;
    }

    public LiveData<String> getMessage() {
        return message;
    }

    public void createWeighing(WeighingPrintAction printAction) {
        ProductionLineStates state = productionLineManager.getCurrentProductionLine().getProductionLineState();
        if (state == ProductionLineStates.TOP) {
            String gross = weighRepository.getGrossStr().getValue();
            String net = weighRepository.getNetStr().getValue();
            String unit = weighRepository.getUnit().getValue();
            Weighing weighing = new Weighing();
            ProductionLine productionLine = productionLineManager.getCurrentProductionLine();
            if (productionLine == null || gross == null || net == null || unit == null) return;
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
            weighing.setDate(DateUtils.getDate());
            weighing.setHour(DateUtils.getHour());
            weighing.setUnit(unit);
            weighing.setLine(String.format(Locale.US, "%s %s", "Linea", productionLineManager.getCurrentProductionLineNumber()));
            insertWeighing(weighing, printAction);
        } else {
            error.setValue("No finalizo la pesada");
        }

    }

    private void insertWeighing(Weighing weighing, WeighingPrintAction printAction) {
        weighingService.newWeighing(weighing).subscribe(new CompletableObserver() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onComplete() {
                message.setValue("Operacion realizada con exito");
                printAction.print();
                productionLineManager.finishWeight();
                weighRepository.setTare();
            }

            @Override
            public void onError(Throwable e) {
                error.setValue(e.getMessage());
            }
        });
    }

    public void deleteAllWeighings() {
        if (userRepository.isEnabled()) {
            weighingService.deleteAllWeighing().subscribe(new CompletableObserver() {
                @Override
                public void onSubscribe(Disposable d) {

                }

                @Override
                public void onComplete() {
                    message.setValue("Pesadas eliminadas");
                }

                @Override
                public void onError(Throwable e) {
                    error.setValue(e.getMessage());
                }
            });
        } else {
            error.setValue("No esta habilitado para cambiar datos");
        }
    }


    @Override
    protected void onCleared() {
        super.onCleared();
        compositeDisposable.clear();
    }
}
