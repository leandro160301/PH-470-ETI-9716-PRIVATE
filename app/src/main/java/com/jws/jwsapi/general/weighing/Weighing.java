package com.jws.jwsapi.general.weighing;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "weighing")
public class Weighing {

    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "id_pallet")
    private int idPallet;

    @ColumnInfo(name = "scale_number")
    private int scaleNumber;

    @ColumnInfo(name = "code")
    private String code;

    @ColumnInfo(name = "name")
    private String name;

    @ColumnInfo(name = "serial_number")
    private String serialNumber;

    @ColumnInfo(name = "operator")
    private String operator;

    @ColumnInfo(name = "net")
    private String net;

    @ColumnInfo(name = "gross")
    private String gross;

    @ColumnInfo(name = "tare")
    private String tare;

    @ColumnInfo(name = "quantity")
    private int quantity;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdPallet() {
        return idPallet;
    }

    public void setIdPallet(int idPallet) {
        this.idPallet = idPallet;
    }

    public int getScaleNumber() {
        return scaleNumber;
    }

    public void setScaleNumber(int scaleNumber) {
        this.scaleNumber = scaleNumber;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
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

    public String getTare() {
        return tare;
    }

    public void setTare(String tare) {
        this.tare = tare;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

}