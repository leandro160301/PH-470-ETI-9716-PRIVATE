package com.jws.jwsapi.core.label;

import com.jws.jwsapi.core.printer.Printer;
import com.jws.jwsapi.core.printer.PrinterObject;
import com.jws.jwsapi.core.printer.PrinterPreferences;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class LabelManager {

    PrinterPreferences printerPreferences;
    private final PrinterObject<String> name = new PrinterObject<>();
    private final PrinterObject<String> code = new PrinterObject<>();
    private final PrinterObject<String> scale = new PrinterObject<>();
    private final PrinterObject<String> number = new PrinterObject<>();
    private final PrinterObject<String> origin = new PrinterObject<>();
    private final PrinterObject<String> destination = new PrinterObject<>();


    @Inject
    public LabelManager(PrinterPreferences printerPreferences) {
        this.printerPreferences = printerPreferences;
        initPrint();
    }

    public List<String> nameLabelList = new ArrayList<>();
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
        varPrinterList.add(new Printer("", name, "Nombre producto", varPrinterList.size()));
        varPrinterList.add(new Printer("", code, "Codigo producto", varPrinterList.size()));
        varPrinterList.add(new Printer("", scale, "Numero de balanza", varPrinterList.size()));
        varPrinterList.add(new Printer("", number, "Numero de pesada", varPrinterList.size()));
        varPrinterList.add(new Printer("", origin, "Origen", varPrinterList.size()));
        varPrinterList.add(new Printer("", destination, "Destino", varPrinterList.size()));

    }

    public void setName(String name) {
        this.name.value = name;
    }

    public void setCode(String code) {
        this.code.value = code;
    }

    public void setScale(String scale) {
        this.scale.value = scale;
    }

    public void setNumber(String number) {
        this.number.value = number;
    }

    public void setOrigin(String origin) {
        this.origin.value = origin;
    }

    public void setDestination(String destination) {
        this.destination.value = destination;
    }

}
