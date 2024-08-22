package com.jws.jwsapi.common.impresora.tipos;

import com.service.PuertosSerie.PuertosSerie;
import com.service.PuertosSerie.PuertosSerie2;

public class ImprimirRS232 {
    private final PuertosSerie2 serialPort;

    public ImprimirRS232(PuertosSerie2 serialPort) {
        this.serialPort = serialPort;
    }
    public void Imprimir(String etiqueta){
        serialPort.write(etiqueta);
    }


}
