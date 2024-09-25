package com.jws.jwsapi.core.label;

import com.jws.jwsapi.core.printer.Printer;
import com.jws.jwsapi.core.printer.PrinterObject;

import java.util.ArrayList;
import java.util.List;

public class LabelVariables {
    private final PrinterObject<String> name = new PrinterObject<>();
    private final PrinterObject<String> code = new PrinterObject<>();
    private final PrinterObject<String> scale = new PrinterObject<>();
    private final PrinterObject<String> number = new PrinterObject<>();
    private final PrinterObject<String> origin = new PrinterObject<>();
    private final PrinterObject<String> destination = new PrinterObject<>();
    public List<Printer> varPrinterList;

    public LabelVariables() {
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
