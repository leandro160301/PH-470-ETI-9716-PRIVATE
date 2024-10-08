package com.jws.jwsapi.productionline;

public class ProductionLine {

    private String destinatation;
    private String product;
    private String maturity;
    private String caliber;
    private String batch;
    private String topTare;
    private String partsTare;
    private String iceTare;
    private String coverTare;
    private ProductionLineStates productionLineState;

    public ProductionLine(String destinatation, String product, String maturity, String caliber,
                          String batch, String topTare, String partsTare, String iceTare,
                          String coverTare, ProductionLineStates productionLineState) {
        this.destinatation = destinatation;
        this.product = product;
        this.maturity = maturity;
        this.caliber = caliber;
        this.batch = batch;
        this.topTare = topTare;
        this.partsTare = partsTare;
        this.iceTare = iceTare;
        this.coverTare = coverTare;
        this.productionLineState = productionLineState;
    }

    public String getDestinatation() {
        return destinatation;
    }

    public void setDestinatation(String destinatation) {
        this.destinatation = destinatation;
    }

    public String getProduct() {
        return product;
    }

    public void setProduct(String product) {
        this.product = product;
    }

    public String getMaturity() {
        return maturity;
    }

    public void setMaturity(String maturity) {
        this.maturity = maturity;
    }

    public String getCaliber() {
        return caliber;
    }

    public void setCaliber(String caliber) {
        this.caliber = caliber;
    }

    public String getBatch() {
        return batch;
    }

    public void setBatch(String batch) {
        this.batch = batch;
    }

    public String getTopTare() {
        return topTare;
    }

    public void setTopTare(String topTare) {
        this.topTare = topTare;
    }

    public String getPartsTare() {
        return partsTare;
    }

    public void setPartsTare(String partsTare) {
        this.partsTare = partsTare;
    }

    public String getIceTare() {
        return iceTare;
    }

    public void setIceTare(String iceTare) {
        this.iceTare = iceTare;
    }

    public String getCoverTare() {
        return coverTare;
    }

    public void setCoverTare(String coverTare) {
        this.coverTare = coverTare;
    }

    public ProductionLineStates getProductionLineState() {
        return productionLineState;
    }

    public void setProductionLineState(ProductionLineStates productionLineState) {
        this.productionLineState = productionLineState;
    }

}
