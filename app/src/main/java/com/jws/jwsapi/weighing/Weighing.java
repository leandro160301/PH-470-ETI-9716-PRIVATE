package com.jws.jwsapi.weighing;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "weighing")
public class Weighing {

    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "product")
    private String product;

    @ColumnInfo(name = "destination")
    private String destination;

    @ColumnInfo(name = "operator")
    private String operator;

    @ColumnInfo(name = "net")
    private String net;

    @ColumnInfo(name = "gross")
    private String gross;

    @ColumnInfo(name = "batch")
    private String batch;

    @ColumnInfo(name = "caliber")
    private String caliber;

    @ColumnInfo(name = "line")
    private String line;

    @ColumnInfo(name = "expirate_date")
    private String expirateDate;

    @ColumnInfo(name = "top_tare")
    private String topTare;

    @ColumnInfo(name = "parts_tare")
    private String partsTare;

    @ColumnInfo(name = "ice_tare")
    private String iceTare;

    @ColumnInfo(name = "box_tare")
    private String boxTare;

    @ColumnInfo(name = "date")
    private String date;

    @ColumnInfo(name = "hour")
    private String hour;

    @ColumnInfo(name = "unit")
    private String unit;

    @ColumnInfo(name = "parts_quantity")
    private String partsQuantity;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getHour() {
        return hour;
    }

    public void setHour(String hour) {
        this.hour = hour;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getProduct() {
        return product;
    }

    public void setProduct(String product) {
        this.product = product;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public String getNet() {
        return net;
    }

    public void setNet(String net) {
        this.net = net;
    }

    public String getGross() {
        return gross;
    }

    public void setGross(String gross) {
        this.gross = gross;
    }

    public String getBatch() {
        return batch;
    }

    public void setBatch(String batch) {
        this.batch = batch;
    }

    public String getCaliber() {
        return caliber;
    }

    public void setCaliber(String caliber) {
        this.caliber = caliber;
    }

    public String getLine() {
        return line;
    }

    public void setLine(String line) {
        this.line = line;
    }

    public String getExpirateDate() {
        return expirateDate;
    }

    public void setExpirateDate(String expirateDate) {
        this.expirateDate = expirateDate;
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

    public String getPartsQuantity() {
        return partsQuantity;
    }

    public void setPartsQuantity(String partsQuantity) {
        this.partsQuantity = partsQuantity;
    }

}