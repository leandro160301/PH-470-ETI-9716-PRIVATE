package com.jws.jwsapi.productionline;

import javax.inject.Inject;

public class ProductionLineManager {

    private final ProductionLine productionLineOne;
    private final ProductionLine productionLineTwo;
    private int currentProductionLineNumber;
    private final ProductionLinePreferences preferences;

    @Inject
    public ProductionLineManager(ProductionLinePreferences preferences) {
        this.productionLineOne = preferences.getProductionLineOne();
        this.productionLineTwo = preferences.getProductionLineTwo();
        currentProductionLineNumber = preferences.getCurrentProductionLineNumber();
        this.preferences = preferences;
    }

    public ProductionLine getCurrentProductionLine() {
        if (currentProductionLineNumber == 1) {
            return productionLineOne;
        } else {
            return productionLineTwo;
        }
    }

    public void setCurrentProductionLineNumber(int lineNumber) {
        this.currentProductionLineNumber = lineNumber;
        preferences.putCurrentProductionLineNumber(lineNumber);
    }

    public int getCurrentProductionLineNumber() {
        return currentProductionLineNumber;
    }

    public void updateProductionLineDestination(String destination) {
        if (currentProductionLineNumber == 1) {
            productionLineOne.setDestinatation(destination);
        } else {
            productionLineTwo.setDestinatation(destination);
        }
        preferences.putDestination(destination);
    }

    public void updateProductionLineProduct(String product) {
        if (currentProductionLineNumber == 1) {
            productionLineOne.setProduct(product);
        } else {
            productionLineTwo.setProduct(product);
        }
        preferences.putProduct(product);
    }

    public void updateProductionLineMaturity(String maturity) {
        if (currentProductionLineNumber == 1) {
            productionLineOne.setMaturity(maturity);
        } else {
            productionLineTwo.setMaturity(maturity);
        }
        preferences.putMaturity(maturity);
    }

    public void updateProductionLineCaliber(String caliber) {
        if (currentProductionLineNumber == 1) {
            productionLineOne.setCaliber(caliber);
        } else {
            productionLineTwo.setCaliber(caliber);
        }
        preferences.putCaliber(caliber);
    }

    public void updateProductionLineBatch(String batch) {
        if (currentProductionLineNumber == 1) {
            productionLineOne.setBatch(batch);
        } else {
            productionLineTwo.setBatch(batch);
        }
        preferences.putBatch(batch);
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
            productionLineOne.setCoverTare(coverTare);
        } else {
            productionLineTwo.setCoverTare(coverTare);
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