package com.jws.jwsapi.line;

import com.jws.jwsapi.utils.Utils;

import javax.inject.Inject;

public class LineManager {

    private final Line lineOne;
    private final Line lineTwo;
    private final LinePreferences preferences;
    private int currentProductionLineNumber;
    private Line lastLine;

    @Inject
    public LineManager(LinePreferences preferences) {
        this.lineOne = preferences.getProductionLineOne();
        this.lineTwo = preferences.getProductionLineTwo();
        currentProductionLineNumber = preferences.getCurrentProductionLineNumber();
        this.preferences = preferences;
    }

    public Line getLastLine() {
        return lastLine;
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

    public void updateProductionLinePartsQuantityOne(String quantity) {
        updateProductionLinePartsQuantity(quantity, 1);
    }

    public void updateProductionLinePartsQuantityTwo(String quantity) {
        updateProductionLinePartsQuantity(quantity, 2);
    }

    public Line getProductionLineOne() {
        return this.lineOne;
    }

    public Line getProductionLineTwo() {
        return this.lineTwo;
    }

    public Line getCurrentProductionLine() {
        if (currentProductionLineNumber == 1) {
            return lineOne;
        } else {
            return lineTwo;
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
            lineOne.setDestinatation(destination);
            preferences.putDestinationOne(destination);
        } else {
            lineTwo.setDestinatation(destination);
            preferences.putDestinationTwo(destination);
        }
    }

    private void updateProductionLineProduct(String product, int line) {
        if (line == 1) {
            lineOne.setProduct(product);
            preferences.putProductOne(product);
        } else {
            lineTwo.setProduct(product);
            preferences.putProductTwo(product);
        }
    }

    private void updateProductionLineExpirateDate(String maturity, int line) {
        if (line == 1) {
            lineOne.setExpirateDate(maturity);
            preferences.putExpirateDateOne(maturity);
        } else {
            lineTwo.setExpirateDate(maturity);
            preferences.putExpirateDateTwo(maturity);
        }
    }

    private void updateProductionLinePartsQuantity(String quantity, int line) {
        if (Utils.isNumeric(quantity)) {
            Integer quantityNumber = Integer.parseInt(quantity);
            if (line == 1) {
                lineOne.setPartsQuantity(quantityNumber);
                preferences.putPartsQuantityLineOne(quantityNumber);
            } else {
                lineTwo.setPartsQuantity(quantityNumber);
                preferences.putPartsQuantityLineTwo(quantityNumber);
            }
        }
    }

    public void updateProductionLineQuantity() {
        try {
            Integer quantity = getCurrentProductionLine().getDestinationQuantity();
            getCurrentProductionLine().setDestinationQuantity(quantity + 1);
            preferences.putDestinationQuantity(quantity + 1);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateProductionLineAccumulated(String partsWeight) {
        try {
            String accumulated = getCurrentProductionLine().getPartsAccumulated();
            if (Utils.isNumeric(accumulated) && Utils.isNumeric(partsWeight)) {
                float newAccumulated = Float.parseFloat(accumulated) + Float.parseFloat(partsWeight);
                String newAccumulatedStr = String.valueOf(Math.round(newAccumulated * 100) / 100f);
                getCurrentProductionLine().setPartsAccumulated(newAccumulatedStr);
                preferences.putPartsAccumulated(newAccumulatedStr);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void resetProductionLineQuantity() {
        try {
            preferences.putDestinationQuantity(0);
            getCurrentProductionLine().setDestinationQuantity(0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void resetProductionLineQuantityOne() {
        try {
            lineOne.setDestinationQuantity(0);
            preferences.putDestinationQuantityLineOne(0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void resetProductionLineQuantityTwo() {
        try {
            lineTwo.setDestinationQuantity(0);
            preferences.putDestinationQuantityLineTwo(0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void resetProductionLineAccumulatedOne() {
        try {
            lineOne.setPartsAccumulated("0");
            preferences.putPartsAccumulatedLineOne("0");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void resetProductionLineAccumulatedTwo() {
        try {
            lineTwo.setPartsAccumulated("0");
            preferences.putPartsAccumulatedLineTwo("0");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateProductionLineCaliber(String caliber, int line) {
        if (line == 1) {
            lineOne.setCaliber(caliber);
            preferences.putCaliberOne(caliber);
        } else {
            lineTwo.setCaliber(caliber);
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
            lineOne.setBatch(batch);
            preferences.putBatchOne(batch);
        } else {
            lineTwo.setBatch(batch);
            preferences.putBatchTwo(batch);
        }

    }

    public void updateProductionLineTopTare(String topTare) {
        if (currentProductionLineNumber == 1) {
            lineOne.setTopTare(topTare);
        } else {
            lineTwo.setTopTare(topTare);
        }
        preferences.putTopTare(topTare);
    }

    public void updateProductionLinePartsTare(String partsTare) {
        if (currentProductionLineNumber == 1) {
            lineOne.setPartsTare(partsTare);
        } else {
            lineTwo.setPartsTare(partsTare);
        }
        preferences.putPartsTare(partsTare);
    }

    public void updateProductionLineIceTare(String iceTare) {
        if (currentProductionLineNumber == 1) {
            lineOne.setIceTare(iceTare);
        } else {
            lineTwo.setIceTare(iceTare);
        }
        preferences.putIceTare(iceTare);
    }

    public void updateProductionLineCoverTare(String coverTare) {
        if (currentProductionLineNumber == 1) {
            lineOne.setBoxTare(coverTare);
        } else {
            lineTwo.setBoxTare(coverTare);
        }
        preferences.putCoverTare(coverTare);
    }

    public void updateProductionLineState(LineStates productionLineState) {
        if (currentProductionLineNumber == 1) {
            lineOne.setProductionLineState(productionLineState);
        } else {
            lineTwo.setProductionLineState(productionLineState);
        }
        preferences.putProductionLineState(productionLineState);
    }

    public void finishWeight() {
        Line lineOld = getCurrentProductionLine();
        lastLine = new Line(lineOld.getDestinatation(), lineOld.getProduct(), lineOld.getExpirateDate(), lineOld.getCaliber(), lineOld.getBatch(),
                lineOld.getTopTare(), lineOld.getPartsTare(), lineOld.getIceTare(), lineOld.getBoxTare(), lineOld.getProductionLineState(),
                lineOld.getPartsQuantity(), lineOld.getDestinationQuantity(), lineOld.getPartsAccumulated(), lineOld.getDestinationQuantity());
        updateProductionLineState(LineStates.INIT);
        updateProductionLineTopTare("0");
        updateProductionLineIceTare("0");
        updateProductionLinePartsTare("0");
        updateProductionLineCoverTare("0");

    }

}