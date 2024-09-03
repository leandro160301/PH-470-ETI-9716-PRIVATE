package com.jws.jwsapi.general.pallet;


/**
 * Si los nombres del cuerpo de la api coinciden con las columnas del entity usar el entity y no esta response
 *
 * <p> Usamos esta request porque los nombres del body no coinciden con las columnas de la entity</p>
 */
public class PalletResponse {
    private String code;
    private String name;
    private int quantity;
    private String serialNumber;

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public String getSerialNumber() { return serialNumber; }
    public void setSerialNumber(String serialNumber) { this.serialNumber = serialNumber; }
}