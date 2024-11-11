package com.jws.jwsapi.line;

import com.jws.jwsapi.core.data.local.PreferencesHelper;

import javax.inject.Inject;

public class LinePreferences {

    private final PreferencesHelper preferencesHelper;

    @Inject
    public LinePreferences(PreferencesHelper preferencesHelper) {
        this.preferencesHelper = preferencesHelper;
    }

    public Line getProductionLineOne() {
        return new Line(
                preferencesHelper.getString("destinatation_one", ""),
                preferencesHelper.getString("product_one", ""),
                preferencesHelper.getString("maturity_one", ""),
                preferencesHelper.getString("caliber_one", ""),
                preferencesHelper.getString("batch_one", ""),
                preferencesHelper.getString("topTare_one", "0"),
                preferencesHelper.getString("partsTare_one", "0"),
                preferencesHelper.getString("iceTare_one", "0"),
                preferencesHelper.getString("coverTare_one", "0"),
                LineStates.valueOf(preferencesHelper.getString("productionLineState_one", LineStates.INIT.name())),
                preferencesHelper.getInt("partsQuantity_one", 0),
                preferencesHelper.getInt("destinationQuantity_one", 0),
                preferencesHelper.getString("partsAccumulated_one", "0"),
                preferencesHelper.getInt("lineQuantity_one", 0)
        );
    }

    public Line getProductionLineTwo() {
        return new Line(
                preferencesHelper.getString("destinatation_two", ""),
                preferencesHelper.getString("product_two", ""),
                preferencesHelper.getString("maturity_two", ""),
                preferencesHelper.getString("caliber_two", ""),
                preferencesHelper.getString("batch_two", ""),
                preferencesHelper.getString("topTare_two", "0"),
                preferencesHelper.getString("partsTare_two", "0"),
                preferencesHelper.getString("iceTare_two", "0"),
                preferencesHelper.getString("coverTare_two", "0"),
                LineStates.valueOf(preferencesHelper.getString("productionLineState_two", LineStates.INIT.name())),
                preferencesHelper.getInt("partsQuantity_two", 0),
                preferencesHelper.getInt("destinationQuantity_two", 0),
                preferencesHelper.getString("partsAccumulated_two", "0"),
                preferencesHelper.getInt("lineQuantity_two", 0)
        );
    }

    public Integer getCurrentProductionLineNumber() {
        return preferencesHelper.getInt("current_productline", 1);
    }

    public void putCurrentProductionLineNumber(Integer value) {
        preferencesHelper.putInt("current_productline", value);
    }

    public void putDestinationOne(String destination) {
        preferencesHelper.putString("destinatation_one", destination);
    }

    public void putProductOne(String product) {
        preferencesHelper.putString("product_one", product);
    }

    public void putExpirateDateOne(String maturity) {
        preferencesHelper.putString("maturity_one", maturity);
    }

    public void putCaliberOne(String caliber) {
        preferencesHelper.putString("caliber_one", caliber);
    }

    public void putBatchOne(String batch) {
        preferencesHelper.putString("batch_one", batch);
    }

    public void putDestinationTwo(String destination) {
        preferencesHelper.putString("destinatation_two", destination);
    }

    public void putProductTwo(String product) {
        preferencesHelper.putString("product_two", product);
    }

    public void putExpirateDateTwo(String maturity) {
        preferencesHelper.putString("maturity_two", maturity);
    }

    public void putCaliberTwo(String caliber) {
        preferencesHelper.putString("caliber_two", caliber);
    }

    public void putBatchTwo(String batch) {
        preferencesHelper.putString("batch_two", batch);
    }

    public void putTopTare(String topTare) {
        preferencesHelper.putString(getCurrentProductionLineNumber() == 1 ? "topTare_one" : "topTare_two", topTare);
    }

    public void putPartsTare(String partsTare) {
        preferencesHelper.putString(getCurrentProductionLineNumber() == 1 ? "partsTare_one" : "partsTare_two", partsTare);
    }

    public void putIceTare(String iceTare) {
        preferencesHelper.putString(getCurrentProductionLineNumber() == 1 ? "iceTare_one" : "iceTare_two", iceTare);
    }

    public void putCoverTare(String coverTare) {
        preferencesHelper.putString(getCurrentProductionLineNumber() == 1 ? "coverTare_one" : "coverTare_two", coverTare);
    }

    public void putProductionLineState(LineStates productionLineState) {
        preferencesHelper.putString(getCurrentProductionLineNumber() == 1 ? "productionLineState_one" : "productionLineState_two", productionLineState.name());
    }

    public void putPartsQuantity(Integer partsQuantity) {
        preferencesHelper.putInt(getCurrentProductionLineNumber() == 1 ? "partsQuantity_one" : "partsQuantity_two", partsQuantity);
    }

    public void putDestinationQuantity(Integer destinationQuantity) {
        preferencesHelper.putInt(getCurrentProductionLineNumber() == 1 ? "destinationQuantity_one" : "destinationQuantity_two", destinationQuantity);
    }

    public void putLineQuantity(Integer lineQuantity) {
        preferencesHelper.putInt(getCurrentProductionLineNumber() == 1 ? "lineQuantity_one" : "lineQuantity_two", lineQuantity);
    }

    public void putPartsAccumulated(String partsAccumulated) {
        preferencesHelper.putString(getCurrentProductionLineNumber() == 1 ? "partsAccumulated_one" : "partsAccumulated_two", partsAccumulated);
    }


    // Para la línea 1
    public void putPartsQuantityLineOne(Integer partsQuantity) {
        preferencesHelper.putInt("partsQuantity_one", partsQuantity);
    }

    public void putDestinationQuantityLineOne(Integer destinationQuantity) {
        preferencesHelper.putInt("destinationQuantity_one", destinationQuantity);
    }

    public void putLineQuantityLineOne(Integer lineQuantity) {
        preferencesHelper.putInt("lineQuantity_one", lineQuantity);
    }

    public void putPartsAccumulatedLineOne(String partsAccumulated) {
        preferencesHelper.putString("partsAccumulated_one", partsAccumulated);
    }

    // Para la línea 2
    public void putPartsQuantityLineTwo(Integer partsQuantity) {
        preferencesHelper.putInt("partsQuantity_two", partsQuantity);
    }

    public void putDestinationQuantityLineTwo(Integer destinationQuantity) {
        preferencesHelper.putInt("destinationQuantity_two", destinationQuantity);
    }

    public void putLineQuantityLineTwo(Integer lineQuantity) {
        preferencesHelper.putInt("lineQuantity_two", lineQuantity);
    }

    public void putPartsAccumulatedLineTwo(String partsAccumulated) {
        preferencesHelper.putString("partsAccumulated_two", partsAccumulated);
    }


}