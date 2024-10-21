package com.jws.jwsapi.productionline;

import com.jws.jwsapi.core.data.local.PreferencesHelper;

import javax.inject.Inject;

public class ProductionLinePreferences {

    private final PreferencesHelper preferencesHelper;
    
    @Inject
    public ProductionLinePreferences(PreferencesHelper preferencesHelper) {
        this.preferencesHelper = preferencesHelper;
    }

    public ProductionLine getProductionLineOne() {
        return new ProductionLine(
                preferencesHelper.getString("destinatation_one", ""),
                preferencesHelper.getString("product_one", ""),
                preferencesHelper.getString("maturity_one", ""),
                preferencesHelper.getString("caliber_one", ""),
                preferencesHelper.getString("batch_one", ""),
                preferencesHelper.getString("topTare_one", "0"),
                preferencesHelper.getString("partsTare_one", "0"),
                preferencesHelper.getString("iceTare_one", "0"),
                preferencesHelper.getString("coverTare_one", "0"),
                ProductionLineStates.valueOf(preferencesHelper.getString("productionLineState_one", ProductionLineStates.INIT.name()))
        );
    }

    public ProductionLine getProductionLineTwo() {
        return new ProductionLine(
                preferencesHelper.getString("destinatation_two", ""),
                preferencesHelper.getString("product_two", ""),
                preferencesHelper.getString("maturity_two", ""),
                preferencesHelper.getString("caliber_two", ""),
                preferencesHelper.getString("batch_two", ""),
                preferencesHelper.getString("topTare_two", "0"),
                preferencesHelper.getString("partsTare_two", "0"),
                preferencesHelper.getString("iceTare_two", "0"),
                preferencesHelper.getString("coverTare_two", "0"),
                ProductionLineStates.valueOf(preferencesHelper.getString("productionLineState_two", ProductionLineStates.INIT.name()))
        );
    }

    public Integer getCurrentProductionLineNumber() {
        return preferencesHelper.getInt("current_productline",1);
    }

    public void putCurrentProductionLineNumber(Integer value) {
        preferencesHelper.putInt("current_productline",value);
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

    public void putProductionLineState(ProductionLineStates productionLineState) {
        preferencesHelper.putString(getCurrentProductionLineNumber() == 1 ? "productionLineState_one" : "productionLineState_two", productionLineState.name());
    }


}