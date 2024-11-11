package com.jws.jwsapi.line;

public class Line {

    private final Integer lineQuantity;
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
    private Integer partsQuantity;
    private Integer destinationQuantity;
    private String partsAccumulated;

    public Line(String destinatation, String product, String expirateDate,
                String caliber, String batch, String topTare, String partsTare,
                String iceTare, String boxTare, LineStates productionLineState,
                Integer partsQuantity, Integer destinationQuantity, String partsAccumulated,
                Integer lineQuantity) {
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
        this.partsQuantity = partsQuantity;
        this.destinationQuantity = destinationQuantity;
        this.partsAccumulated = partsAccumulated;
        this.lineQuantity = lineQuantity;
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

    public Integer getPartsQuantity() {
        return partsQuantity;
    }

    public void setPartsQuantity(Integer partsQuantity) {
        this.partsQuantity = partsQuantity;
    }

    public Integer getDestinationQuantity() {
        return destinationQuantity;
    }

    public void setDestinationQuantity(Integer destinationQuantity) {
        this.destinationQuantity = destinationQuantity;
    }

    public String getPartsAccumulated() {
        return partsAccumulated;
    }

    public void setPartsAccumulated(String partsAccumulated) {
        this.partsAccumulated = partsAccumulated;
    }


}
