package com.jws.jwsapi.general.pallet;

import com.google.gson.annotations.SerializedName;

public class PalletRequest {
    @SerializedName("balanza")
    private int scaleNumber;

    @SerializedName("palletDestino")
    private String destinationPallet;

    @SerializedName("palletOrigen")
    private String originPallet;

    public PalletRequest(int scaleNumber, String destinationPallet, String originPallet) {
        this.scaleNumber = scaleNumber;
        this.destinationPallet = destinationPallet;
        this.originPallet = originPallet;
    }

    public int getScaleNumber() {
        return scaleNumber;
    }

    public void setScaleNumber(int scaleNumber) {
        this.scaleNumber = scaleNumber;
    }

    public String getDestinationPallet() {
        return destinationPallet;
    }

    public void setDestinationPallet(String destinationPallet) {
        this.destinationPallet = destinationPallet;
    }

    public String getOriginPallet() {
        return originPallet;
    }

    public void setOriginPallet(String originPallet) {
        this.originPallet = originPallet;
    }
}