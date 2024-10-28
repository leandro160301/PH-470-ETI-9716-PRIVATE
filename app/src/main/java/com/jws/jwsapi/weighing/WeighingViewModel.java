package com.jws.jwsapi.weighing;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.jws.jwsapi.line.Line;
import com.jws.jwsapi.line.LineManager;
import com.jws.jwsapi.line.LineStates;
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
    private final LineManager lineManager;
    private final WeighRepository weighRepository;

    @Inject
    public WeighingViewModel(WeighingService weighingService, UserRepository userRepository, LineManager lineManager, WeighRepository weighRepository) {
        this.weighingService = weighingService;
        this.userRepository = userRepository;
        this.lineManager = lineManager;
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
        LineStates state = lineManager.getCurrentProductionLine().getProductionLineState();
        if (state == LineStates.TOP) {
            String gross = weighRepository.getGrossStr().getValue();
            String net = weighRepository.getNetStr().getValue();
            String unit = weighRepository.getUnit().getValue();
            Weighing weighing = new Weighing();
            Line line = lineManager.getCurrentProductionLine();
            if (line == null || gross == null || net == null || unit == null) return;
            weighing.setBatch(line.getBatch());
            weighing.setCaliber(line.getCaliber());
            weighing.setBoxTare(line.getBoxTare());
            weighing.setDestination(line.getDestinatation());
            weighing.setGross(gross);
            weighing.setExpirateDate(line.getExpirateDate());
            weighing.setIceTare(line.getIceTare());
            weighing.setNet(net);
            weighing.setTopTare(line.getTopTare());
            weighing.setPartsTare(line.getPartsTare());
            weighing.setProduct(line.getProduct());
            weighing.setOperator(userRepository.getCurrentUser());
            weighing.setDate(DateUtils.getDate());
            weighing.setHour(DateUtils.getHour());
            weighing.setUnit(unit);
            weighing.setLine(String.format(Locale.US, "%s %s", "Linea", lineManager.getCurrentProductionLineNumber()));
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
                lineManager.finishWeight();
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
