package com.jws.jwsapi.weighing;

import com.google.gson.annotations.SerializedName;

public class WeighingRequest {
    @SerializedName("numeroSerie")
    private String serialNumber;

    @SerializedName("codigo")
    private String code;

    @SerializedName("nombre")
    private String name;

    @SerializedName("neto")
    private String net;

    @SerializedName("bruto")
    private String gross;

    @SerializedName("tara")
    private String tare;

    public WeighingRequest(String serialNumber, String code, String name, String net, String gross, String tare) {
        this.serialNumber = serialNumber;
        this.code = code;
        this.name = name;
        this.net = net;
        this.gross = gross;
        this.tare = tare;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
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

}