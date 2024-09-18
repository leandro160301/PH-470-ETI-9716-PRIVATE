package com.jws.jwsapi.core.printer;

public class Printer {
    public String descripcion;
    PrinterObject impresoravar;
    String codigo;
    int num;

    public Printer(String codigo, PrinterObject impresoravar, String descripcion, int num) {
        this.codigo=codigo;
        this.impresoravar=impresoravar;
        this.descripcion=descripcion;
        this.num=num;
    }
    public String value(){
        return (String) impresoravar.value;
    }

}
