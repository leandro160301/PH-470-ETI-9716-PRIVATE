package com.jws.jwsapi.core.printer;

public class Printer {
    private String description;
    private PrinterObject<String> printerVar;
    private String code;
    private int num;

    public Printer(String code, PrinterObject<String> printerVar, String description, int num) {
        this.code = code;
        this.printerVar = printerVar;
        this.description = description;
        this.num = num;
    }

    public String value() {
        return printerVar.value;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public PrinterObject<String> getPrinterVar() {
        return printerVar;
    }

    public void setPrinterVar(PrinterObject<String> printerVar) {
        this.printerVar = printerVar;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }


}
