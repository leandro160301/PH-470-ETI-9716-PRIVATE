package com.jws.jwsapi.core.printer;

import java.io.PrintWriter;
import java.net.Socket;

public class NetworkPrinter {
    public void print(String ip, String label) {
        Thread thread = new Thread(() -> {
            try {
                Socket sock = new Socket(ip, 9100);
                PrintWriter oStream = new PrintWriter(sock.getOutputStream());
                oStream.println(label);
                oStream.close();
                sock.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        thread.start();
    }


}
