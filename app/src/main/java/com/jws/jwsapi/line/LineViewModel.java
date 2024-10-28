package com.jws.jwsapi.line;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.jws.jwsapi.shared.WeighRepository;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

@HiltViewModel
public class LineViewModel extends ViewModel {

    private final static int PERIOD = 200;
    private final CompositeDisposable compositeDisposable = new CompositeDisposable();
    private final MutableLiveData<String> batch = new MutableLiveData<>();
    private final MutableLiveData<String> destination = new MutableLiveData<>();
    private final MutableLiveData<String> product = new MutableLiveData<>();
    private final MutableLiveData<String> expirationDate = new MutableLiveData<>();
    private final MutableLiveData<String> caliber = new MutableLiveData<>();
    private final MutableLiveData<Integer> lineNumber = new MutableLiveData<>();
    private final MutableLiveData<LineStates> state = new MutableLiveData<>();
    private final MutableLiveData<String> error = new MutableLiveData<>();
    private final MutableLiveData<String> message = new MutableLiveData<>();
    private final LineManager lineManager;
    private final WeighRepository weighRepository;
    private Disposable pollingDisposable;

    @Inject
    public LineViewModel(LineManager lineManager, WeighRepository weighRepository) {
        this.lineManager = lineManager;
        this.weighRepository = weighRepository;
        startPolling();
    }

    public void startPolling() {
        pollingDisposable = Observable.interval(PERIOD, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(tick -> updateData());
    }

    private void updateData() {
        Line line = lineManager.getCurrentProductionLine();
        String batch = line.getBatch();
        String destination = line.getDestinatation();
        String product = line.getProduct();
        String expirationDate = line.getExpirateDate();
        String caliber = line.getCaliber();
        Integer lineNumber = lineManager.getCurrentProductionLineNumber();
        LineStates state = lineManager.getCurrentProductionLine().getProductionLineState();
        if (!batch.equals(this.batch.getValue())) {
            this.batch.postValue(batch);
        }
        if (!destination.equals(this.destination.getValue())) {
            this.destination.postValue(destination);
        }
        if (!product.equals(this.product.getValue())) {
            this.product.postValue(product);
        }
        if (!expirationDate.equals(this.expirationDate.getValue())) {
            this.expirationDate.postValue(expirationDate);
        }
        if (!caliber.equals(this.caliber.getValue())) {
            this.caliber.postValue(caliber);
        }
        if (!lineNumber.equals(this.lineNumber.getValue())) {
            this.lineNumber.postValue(lineNumber);
        }
        if (!state.equals(this.state.getValue())) {
            this.state.postValue(state);
        }
    }

    public LiveData<String> getError() {
        return error;
    }

    public LiveData<String> getMessage() {
        return message;
    }

    public MutableLiveData<Integer> getLineNumber() {
        return lineNumber;
    }

    public MutableLiveData<String> getBatch() {
        return batch;
    }

    public MutableLiveData<String> getDestination() {
        return destination;
    }

    public MutableLiveData<String> getProduct() {
        return product;
    }

    public MutableLiveData<String> getExpirationDate() {
        return expirationDate;
    }

    public MutableLiveData<String> getCaliber() {
        return caliber;
    }

    public MutableLiveData<LineStates> getState() {
        return state;
    }

    public void putTareBoxProcess() {
        lineManager.updateProductionLineCoverTare(weighRepository.format(String.valueOf(weighRepository.getNet())));
        lineManager.updateProductionLineState(LineStates.BOX);
    }

    public void putTarePartsProcess() {
        lineManager.updateProductionLinePartsTare(weighRepository.format(String.valueOf(weighRepository.getNet())));
        lineManager.updateProductionLineState(LineStates.PARTS);
    }

    public void putTareIceProcess() {
        lineManager.updateProductionLineIceTare(weighRepository.format(String.valueOf(weighRepository.getNet())));
        lineManager.updateProductionLineState(LineStates.ICE);
    }

    public void putTareTopProcess() {
        lineManager.updateProductionLineTopTare(weighRepository.format(String.valueOf(weighRepository.getNet())));
        lineManager.updateProductionLineState(LineStates.TOP);
    }

    public void stopPolling() {
        if (pollingDisposable != null && !pollingDisposable.isDisposed()) {
            pollingDisposable.dispose();
        }
    }

    public void changeCurrentLine() {
        lineManager.changeCurrentProductionLine();
    }

    public void tareButton() {
        LineStates state = lineManager.getCurrentProductionLine().getProductionLineState();
        switch (state) {
            case INIT:
                putTareBoxProcess();
                message.setValue("Caja lista");
                weighRepository.setTare();
                break;
            case BOX:
                putTarePartsProcess();
                message.setValue("Piezas listas");
                weighRepository.setTare();
                break;
            case PARTS:
                putTareIceProcess();
                message.setValue("Hielo listo");
                weighRepository.setTare();
                break;
            case ICE:
                putTareTopProcess();
                message.setValue("Caja cerrada");
                weighRepository.setTare();
                break;
            default:
                error.setValue("Error caja finalizada, pulse imprimir");
        }
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        compositeDisposable.clear();
    }
}
