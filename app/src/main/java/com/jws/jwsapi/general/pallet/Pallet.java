package com.jws.jwsapi.general.pallet;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "pallet")
public class Pallet {

    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "origin_pallet")
    private String originPallet;

    @ColumnInfo(name = "destination_pallet")
    private String destinationPallet;

    @ColumnInfo(name = "scale_number")
    private int scaleNumber;

    @ColumnInfo(name = "code")
    private String code;

    @ColumnInfo(name = "name")
    private String name;

    @ColumnInfo(name = "quantity")
    private int quantity;

    @ColumnInfo(name = "done")
    private int done;

    @ColumnInfo(name = "serial_number")
    private String serialNumber;

    @ColumnInfo(name = "is_closed")
    private boolean isClosed;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getOriginPallet() {
        return originPallet;
    }

    public void setOriginPallet(String originPallet) {
        this.originPallet = originPallet;
    }

    public String getDestinationPallet() {
        return destinationPallet;
    }

    public void setDestinationPallet(String destinationPallet) {
        this.destinationPallet = destinationPallet;
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

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public boolean isClosed() {
        return isClosed;
    }

    public void setClosed(boolean closed) {
        isClosed = closed;
    }

    public int getDone() {
        return done;
    }

    public void setDone(int done) {
        this.done = done;
    }
}