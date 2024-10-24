package com.jws.jwsapi.core.label;

import com.jws.jwsapi.core.printer.Printer;
import com.jws.jwsapi.core.printer.PrinterObject;
import com.jws.jwsapi.core.printer.PrinterPreferences;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class LabelManager {

    public final PrinterObject<String> linea = new PrinterObject<>();
    public final PrinterObject<String> name = new PrinterObject<>();
    public final PrinterObject<String> batch = new PrinterObject<>();
    public final PrinterObject<String> destinatation = new PrinterObject<>();
    public final PrinterObject<String> expirateDate = new PrinterObject<>();
    public final PrinterObject<String> tareBox = new PrinterObject<>();
    public final PrinterObject<String> tareParts = new PrinterObject<>();
    public final PrinterObject<String> tareIce = new PrinterObject<>();
    public final PrinterObject<String> tareTop = new PrinterObject<>();
    public final PrinterObject<String> caliber = new PrinterObject<>();
    public List<String> nameLabelList = new ArrayList<>();
    public List<Printer> varPrinterList;
    public List<String> constantPrinterList;
    PrinterPreferences printerPreferences;

    @Inject
    public LabelManager(PrinterPreferences printerPreferences) {
        this.printerPreferences = printerPreferences;
        initPrint();
    }

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
        nameLabelList.add("PESADA DE LINEA");

        varPrinterList = new ArrayList<>();
        varPrinterList.add(new Printer("", linea, "Linea", varPrinterList.size()));
        varPrinterList.add(new Printer("", name, "Producto", varPrinterList.size()));
        varPrinterList.add(new Printer("", destinatation, "Destinatario", varPrinterList.size()));
        varPrinterList.add(new Printer("", batch, "Lote", varPrinterList.size()));
        varPrinterList.add(new Printer("", expirateDate, "Vencimiento", varPrinterList.size()));
        varPrinterList.add(new Printer("", tareBox, "Tara envase", varPrinterList.size()));
        varPrinterList.add(new Printer("", tareParts, "Tara piezas", varPrinterList.size()));
        varPrinterList.add(new Printer("", tareIce, "Tara hielo", varPrinterList.size()));
        varPrinterList.add(new Printer("", tareTop, "Tara tapa", varPrinterList.size()));
        varPrinterList.add(new Printer("", caliber, "Calibre", varPrinterList.size()));

    }


}
