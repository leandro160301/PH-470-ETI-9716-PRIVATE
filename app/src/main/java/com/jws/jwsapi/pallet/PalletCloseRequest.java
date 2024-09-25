package com.jws.jwsapi.pallet;

import com.google.gson.annotations.SerializedName;

public class PalletCloseRequest {

    @SerializedName("numeroSerie")
    private String serialNumber;

    public PalletCloseRequest(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }
}