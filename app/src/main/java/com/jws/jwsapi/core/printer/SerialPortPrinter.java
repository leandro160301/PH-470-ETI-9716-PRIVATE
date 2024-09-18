package com.jws.jwsapi.core.printer;

import com.service.PuertosSerie.PuertosSerie2;

public class SerialPortPrinter {
    private final PuertosSerie2 serialPort;

    public SerialPortPrinter(PuertosSerie2 serialPort) {
        this.serialPort = serialPort;
    }
    public void Imprimir(String etiqueta){
        serialPort.write(etiqueta);
    }


}
