package com.jws.jwsapi.line;

import androidx.lifecycle.ViewModel;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class LineDataViewModel extends ViewModel {

    private final LineManager lineManager;

    @Inject
    public LineDataViewModel(LineManager lineManager) {
        this.lineManager = lineManager;
    }

    public Integer getCurrentLine() {
        return lineManager.getCurrentProductionLineNumber();
    }

    public Line getLineOne() {
        return lineManager.getProductionLineOne();
    }

    public Line getLineTwo() {
        return lineManager.getProductionLineTwo();
    }

    public void updateBatchOne(String value) {
        lineManager.updateProductionLineBatchOne(value);
    }

    public void updateBatchTwo(String value) {
        lineManager.updateProductionLineBatchTwo(value);
    }

    public void updateExpirateOne(String value) {
        lineManager.updateProductionLineExpirateDateOne(value);
    }

    public void updateExpirateTwo(String value) {
        lineManager.updateProductionLineExpirateDateTwo(value);
    }

    public void updateDestinationOne(String value) {
        lineManager.updateProductionLineDestinationOne(value);
    }

    public void updateDestinationTwo(String value) {
        lineManager.updateProductionLineDestinationTwo(value);
    }

    public void updateProductOne(String value) {
        lineManager.updateProductionLineProductOne(value);
    }

    public void updateProductTwo(String value) {
        lineManager.updateProductionLineProductTwo(value);
    }

    public void updateCaliberOne(String value) {
        lineManager.updateProductionLineCaliberOne(value);
    }

    public void updateCaliberTwo(String value) {
        lineManager.updateProductionLineCaliberTwo(value);
    }

    public void updatePartsQuantityOne(String value) {
        lineManager.updateProductionLinePartsQuantityOne(value);
    }

    public void updatePartsQuantityTwo(String value) {
        lineManager.updateProductionLinePartsQuantityTwo(value);
    }

    public void resetLineQuantity(int line) {
        if (line == 1) {
            lineManager.resetProductionLineAccumulatedOne();
            lineManager.resetProductionLineQuantityOne();
        } else {
            lineManager.resetProductionLineAccumulatedTwo();
            lineManager.resetProductionLineQuantityTwo();
        }
    }

    public void finishWeight() {
        lineManager.finishWeight();
        lineManager.resetProductionLineQuantity();
    }
}
