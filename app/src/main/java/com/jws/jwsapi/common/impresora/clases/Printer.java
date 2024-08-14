package com.jws.jwsapi.common.impresora.clases;


import android.content.Context;

public class Printer {
    private Context context;
    public String descripcion="";
    PrinterObject impresoravar;
    String codigo="";
    int num=0;

    public Printer(String codigo, PrinterObject impresoravar, String descripcion, int num, Context context) {
        this.codigo=codigo;
        this.impresoravar=impresoravar;
        this.descripcion=descripcion;
        this.num=num;
        this.context=context;
    }
    public String value(){
        return (String) impresoravar.value;
    }

}
