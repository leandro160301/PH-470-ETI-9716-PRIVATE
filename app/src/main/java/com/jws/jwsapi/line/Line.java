package com.jws.jwsapi.line;

public class Line {

    private String destinatation;
    private String product;
    private String expirateDate;
    private String caliber;
    private String batch;
    private String topTare;
    private String partsTare;
    private String iceTare;
    private String boxTare;
    private LineStates productionLineState;

    public Line(String destinatation, String product, String expirateDate, String caliber,
                String batch, String topTare, String partsTare, String iceTare,
                String boxTare, LineStates productionLineState) {
        this.destinatation = destinatation;
        this.product = product;
        this.expirateDate = expirateDate;
        this.caliber = caliber;
        this.batch = batch;
        this.topTare = topTare;
        this.partsTare = partsTare;
        this.iceTare = iceTare;
        this.boxTare = boxTare;
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

    public String getExpirateDate() {
        return expirateDate;
    }

    public void setExpirateDate(String expirateDate) {
        this.expirateDate = expirateDate;
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

    public String getBoxTare() {
        return boxTare;
    }

    public void setBoxTare(String boxTare) {
        this.boxTare = boxTare;
    }

    public LineStates getProductionLineState() {
        return productionLineState;
    }

    public void setProductionLineState(LineStates productionLineState) {
        this.productionLineState = productionLineState;
    }

}
