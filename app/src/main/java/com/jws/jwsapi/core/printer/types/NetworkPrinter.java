package com.jws.jwsapi.core.printer.types;

import java.io.PrintWriter;
import java.net.Socket;

public class NetworkPrinter {
    public void Imprimir(String ip, String etiqueta) {
        Thread thread = new Thread(() -> {
            try {
                Socket sock = new Socket(ip, 9100);
                PrintWriter oStream = new PrintWriter(sock.getOutputStream());
                oStream.println(etiqueta);
                oStream.close();
                sock.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        thread.start();
    }



}
