package com.jws.jwsapi.productionline;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

@HiltViewModel
public class ProductionLineViewModel extends ViewModel {

    private final CompositeDisposable compositeDisposable = new CompositeDisposable();
    private final MutableLiveData<Boolean> loading = new MutableLiveData<>();
    private final MutableLiveData<String> batch = new MutableLiveData<>();
    private final MutableLiveData<String> destination = new MutableLiveData<>();
    private final MutableLiveData<String> product = new MutableLiveData<>();
    private final MutableLiveData<String> expirationDate = new MutableLiveData<>();
    private final MutableLiveData<String> caliber = new MutableLiveData<>();
    private final MutableLiveData<Integer> lineNumber = new MutableLiveData<>();
    private final ProductionLineManager productionLineManager;
    private final static int PERIOD = 200;
    private Disposable pollingDisposable;

    @Inject
    public ProductionLineViewModel(ProductionLineManager productionLineManager) {
        this.productionLineManager = productionLineManager;
        startPolling();
    }

    public void startPolling() {
        pollingDisposable = Observable.interval(PERIOD, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(tick -> updateData());
    }

    private void updateData() {
        ProductionLine productionLine = productionLineManager.getCurrentProductionLine();
        String batch = productionLine.getBatch();
        String destination = productionLine.getDestinatation();
        String product = productionLine.getProduct();
        String expirationDate = productionLine.getExpirateDate();
        String caliber = productionLine.getCaliber();
        Integer lineNumber = productionLineManager.getCurrentProductionLineNumber();
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
    }

    public MutableLiveData<Integer> getLineNumber() {
        return lineNumber;
    }

    public LiveData<Boolean> getLoading() {
        return loading;
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

    public void stopPolling() {
        if (pollingDisposable != null && !pollingDisposable.isDisposed()) {
            pollingDisposable.dispose();
        }
    }

    public void changeCurrentLine() {
        productionLineManager.changeCurrentProductionLine();
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        compositeDisposable.clear();
    }
}
