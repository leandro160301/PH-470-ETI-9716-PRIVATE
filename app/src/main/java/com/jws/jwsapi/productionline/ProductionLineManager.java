package com.jws.jwsapi.productionline;

import javax.inject.Inject;

public class ProductionLineManager {

    private final ProductionLine productionLineOne;
    private final ProductionLine productionLineTwo;
    private final ProductionLinePreferences preferences;
    private int currentProductionLineNumber;

    @Inject
    public ProductionLineManager(ProductionLinePreferences preferences) {
        this.productionLineOne = preferences.getProductionLineOne();
        this.productionLineTwo = preferences.getProductionLineTwo();
        currentProductionLineNumber = preferences.getCurrentProductionLineNumber();
        this.preferences = preferences;
    }

    public void updateProductionLineExpirateDateOne(String maturity) {
        updateProductionLineExpirateDate(maturity, 1);
    }

    public void updateProductionLineExpirateDateTwo(String maturity) {
        updateProductionLineExpirateDate(maturity, 2);
    }

    public void updateProductionLineCaliberOne(String caliber) {
        updateProductionLineCaliber(caliber, 1);
    }

    public void updateProductionLineCaliberTwo(String caliber) {
        updateProductionLineCaliber(caliber, 2);
    }

    public void updateProductionLineProductOne(String product) {
        updateProductionLineProduct(product, 1);
    }

    public void updateProductionLineProductTwo(String product) {
        updateProductionLineProduct(product, 2);
    }

    public void updateProductionLineDestinationOne(String destination) {
        updateProductionLineDestination(destination, 1);
    }

    public void updateProductionLineDestinationTwo(String destination) {
        updateProductionLineDestination(destination, 2);
    }

    public ProductionLine getProductionLineOne() {
        return this.productionLineOne;
    }

    public ProductionLine getProductionLineTwo() {
        return this.productionLineTwo;
    }

    public ProductionLine getCurrentProductionLine() {
        if (currentProductionLineNumber == 1) {
            return productionLineOne;
        } else {
            return productionLineTwo;
        }
    }

    public void changeCurrentProductionLine() {
        if (currentProductionLineNumber == 1) {
            currentProductionLineNumber = 2;
            preferences.putCurrentProductionLineNumber(2);
        } else {
            currentProductionLineNumber = 1;
            preferences.putCurrentProductionLineNumber(1);
        }

    }

    public int getCurrentProductionLineNumber() {
        return currentProductionLineNumber;
    }


    private void updateProductionLineDestination(String destination, int line) {
        if (line == 1) {
            productionLineOne.setDestinatation(destination);
            preferences.putDestinationOne(destination);
        } else {
            productionLineTwo.setDestinatation(destination);
            preferences.putDestinationTwo(destination);
        }

    }

    private void updateProductionLineProduct(String product, int line) {
        if (line == 1) {
            productionLineOne.setProduct(product);
            preferences.putProductOne(product);
        } else {
            productionLineTwo.setProduct(product);
            preferences.putProductTwo(product);
        }
    }

    private void updateProductionLineExpirateDate(String maturity, int line) {
        if (line == 1) {
            productionLineOne.setExpirateDate(maturity);
            preferences.putExpirateDateOne(maturity);
        } else {
            productionLineTwo.setExpirateDate(maturity);
            preferences.putExpirateDateTwo(maturity);
        }

    }

    private void updateProductionLineCaliber(String caliber, int line) {
        if (line == 1) {
            productionLineOne.setCaliber(caliber);
            preferences.putCaliberOne(caliber);
        } else {
            productionLineTwo.setCaliber(caliber);
            preferences.putCaliberTwo(caliber);
        }

    }

    public void updateProductionLineBatchOne(String batch) {
        updateProductionLineBatch(batch, 1);
    }

    public void updateProductionLineBatchTwo(String batch) {
        updateProductionLineBatch(batch, 2);
    }

    private void updateProductionLineBatch(String batch, int line) {
        if (line == 1) {
            productionLineOne.setBatch(batch);
            preferences.putBatchOne(batch);
        } else {
            productionLineTwo.setBatch(batch);
            preferences.putBatchTwo(batch);
        }

    }

    public void updateProductionLineTopTare(String topTare) {
        if (currentProductionLineNumber == 1) {
            productionLineOne.setTopTare(topTare);
        } else {
            productionLineTwo.setTopTare(topTare);
        }
        preferences.putTopTare(topTare);
    }

    public void updateProductionLinePartsTare(String partsTare) {
        if (currentProductionLineNumber == 1) {
            productionLineOne.setPartsTare(partsTare);
        } else {
            productionLineTwo.setPartsTare(partsTare);
        }
        preferences.putPartsTare(partsTare);
    }

    public void updateProductionLineIceTare(String iceTare) {
        if (currentProductionLineNumber == 1) {
            productionLineOne.setIceTare(iceTare);
        } else {
            productionLineTwo.setIceTare(iceTare);
        }
        preferences.putIceTare(iceTare);
    }

    public void updateProductionLineCoverTare(String coverTare) {
        if (currentProductionLineNumber == 1) {
            productionLineOne.setBoxTare(coverTare);
        } else {
            productionLineTwo.setBoxTare(coverTare);
        }
        preferences.putCoverTare(coverTare);
    }

    public void updateProductionLineState(ProductionLineStates productionLineState) {
        if (currentProductionLineNumber == 1) {
            productionLineOne.setProductionLineState(productionLineState);
        } else {
            productionLineTwo.setProductionLineState(productionLineState);
        }
        preferences.putProductionLineState(productionLineState);
    }
}