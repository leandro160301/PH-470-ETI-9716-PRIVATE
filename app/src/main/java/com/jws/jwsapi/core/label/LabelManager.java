package com.jws.jwsapi.core.label;

import com.jws.jwsapi.core.printer.Printer;
import com.jws.jwsapi.core.printer.PrinterPreferences;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class LabelManager {

    PrinterPreferences printerPreferences;
    @Inject
    public LabelManager(PrinterPreferences printerPreferences){
        this.printerPreferences = printerPreferences;
        initPrint();
    }

    public List<String> nameLabelList =new ArrayList<>();
    public List<Printer> varPrinterList;
    public List<String> constantPrinterList;

    public void initPrint() {
        constantPrinterList = new ArrayList<>();
        constantPrinterList.add("");
        constantPrinterList.add("Bruto");//w0001
        constantPrinterList.add("Tara");//w0002
        constantPrinterList.add("Neto");//w0003
        constantPrinterList.add("Operador");//w0004
        constantPrinterList.add("Fecha");//w0005
        constantPrinterList.add("Hora");//w0006
        constantPrinterList.add("Ingresar texto (fijo)");
        constantPrinterList.add("Concatenar datos");//si agregamos nuevas mantener estas ultimas dos ultimas

        nameLabelList = new ArrayList<>();
        nameLabelList.add("PESADA DE PALLET");

        varPrinterList = new ArrayList<>();

    }


}
