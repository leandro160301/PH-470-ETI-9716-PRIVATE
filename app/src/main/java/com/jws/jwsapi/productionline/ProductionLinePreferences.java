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

    public void putDestination(String destination) {
        preferencesHelper.putString(getCurrentProductionLineNumber()==1 ? "destinatation_one" : "destinatation_two", destination);
    }

    public void putProduct(String product) {
        preferencesHelper.putString(getCurrentProductionLineNumber()==1 ? "product_one" : "product_two", product);
    }

    public void putMaturity(String maturity) {
        preferencesHelper.putString(getCurrentProductionLineNumber() == 1 ? "maturity_one" : "maturity_two", maturity);
    }

    public void putCaliber(String caliber) {
        preferencesHelper.putString(getCurrentProductionLineNumber() == 1 ? "caliber_one" : "caliber_two", caliber);
    }

    public void putBatch(String batch) {
        preferencesHelper.putString(getCurrentProductionLineNumber() == 1 ? "batch_one" : "batch_two", batch);
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