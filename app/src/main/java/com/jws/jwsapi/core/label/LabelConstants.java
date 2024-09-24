package com.jws.jwsapi.core.label;

import java.util.ArrayList;
import java.util.List;

public class LabelConstants {

    public final List<String> constantPrinterList;

    public LabelConstants() {
        constantPrinterList = new ArrayList<>();
        constantPrinterList.add("");
        constantPrinterList.add("Bruto");//w0001
        constantPrinterList.add("Tara");//w0002
        constantPrinterList.add("Neto");//w0003
        constantPrinterList.add("Operador");//w0004
        constantPrinterList.add("Fecha");//w0005
        constantPrinterList.add("Hora");//w0006
        constantPrinterList.add("Ingresar texto (fijo)");
        constantPrinterList.add("Concatenar datos");//si agregamos nuevas mantener estas dos últimas
    }
}