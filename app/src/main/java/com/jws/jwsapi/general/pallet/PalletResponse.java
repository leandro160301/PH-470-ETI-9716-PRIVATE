package com.jws.jwsapi.general.pallet;


import com.google.gson.annotations.SerializedName;

/**
 * Se puede usar la entity, solo debajo de @ColumnInfo debemos poner el @SerializedName del body
 * En este caso usamos una aparte porque el servidor devuelve menos datos que la entity</p>
 */
public class PalletResponse {

    @SerializedName("codigo")
    private String code;

    @SerializedName("nombre")
    private String name;

    @SerializedName("cantidad")
    private int quantity;

    @SerializedName("numeroSerie")
    private String serialNumber;

    public String getCode(){
        return code;
    }

    public void setCode(String code){
        this.code = code;
    }

    public String getName(){
        return name;
    }

    public void setName(String name){
        this.name = name;
    }

    public int getQuantity(){
        return quantity;
    }

    public void setQuantity(int quantity){
        this.quantity = quantity;
    }

    public String getSerialNumber(){
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber){
        this.serialNumber = serialNumber;
    }

}