package com.service.PuertosSerie;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

import android_serialport_api.SerialPort;


public class PuertosSerie2 {
    SerialPort serialPort=null;
    OutputStream mOutputStream;
    InputStream mInputStream;
    String Puerto;

    int Baudrate=9600,StopBits=1,DataBits=8,Parity=0,FlowCon=0,Flags=0;

    public interface PuertosSerie2Listener {
        void onMsjPort(String data);
    }

    public static class SerialPortReader {
        private InputStream mInputStream;
        Thread readThread;
        private PuertosSerie2Listener PuertosSerie2Listener;
        private boolean running = false;

        public SerialPortReader(InputStream Port, PuertosSerie2Listener listener) {
            this.mInputStream = Port;
            this.PuertosSerie2Listener = listener;
        }

        public void startReading() {
            running = true;
             readThread = new Thread(() -> {
                while (running) {
                    if (mInputStream != null) {
                        try {
                            byte[] buffer = new byte[1024];
                            int size = mInputStream.read(buffer);
                            if (size > 0) {
                                String str = new String(buffer, 0, size,StandardCharsets.ISO_8859_1);
                                if(str!=null ) {
                                    PuertosSerie2Listener.onMsjPort(str);
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            running = false;
                        }
                    }
                }
            });
            readThread.start();
        }

        public void stopReading() {
            running = false;
            mInputStream=null;
            readThread.interrupt();

        }
    }
    public SerialPort open(String puerto, int baudrate, int stopBits, int dataBits, int parity, int flowCon, int flags){

        Puerto=puerto;
        Baudrate=baudrate;
        StopBits=stopBits;
        DataBits=dataBits;
        Parity=parity;
        FlowCon=flowCon;
        Flags=flags;
        File archivo=new File(puerto);

        if(archivo.exists()){
            try {
                serialPort=new SerialPort(archivo, baudrate,stopBits,dataBits,parity,flowCon,flags);
                mOutputStream = serialPort.getOutputStream();
                mInputStream = serialPort.getInputStream();
            } catch (
                    IOException e) {
                e.printStackTrace();
            }



            // Iniciar la lectura
            return serialPort;
        }else{
            return null;
        }


    }

    public InputStream getInputStream() {
        return mInputStream;
    }
    public int getInputStreamavailable() throws IOException {
        if(serialPort!=null){
            return mInputStream.available();
        }else{
            return 0;
        }

    }

    public OutputStream getOutputStream() {
        return mOutputStream;
    }

    public String PUERTO(){
        return Puerto;
    }
    public int BAUDRATE(){
        return Baudrate;
    }
    public int STOPBIT(){
        return StopBits;
    }
    public int DATABIT(){
        return DataBits;
    }
    public int PARITY(){
        return Parity;
    }
    public int FLOWCON(){
        return FlowCon;
    }
    public int FLAG(){
        return Flags;
    }

    public boolean HabilitadoLectura() throws IOException {
        if(serialPort!=null){
            return serialPort.getInputStream().available() > 0;
        }else {
            return false;
        }


    }
    public String read_2()
    {
        if(serialPort!=null){
            String str=null;
            int size;
            try {
                if (mInputStream == null)
                    return null;
                byte[] buffer = new byte[1024];
                size = mInputStream.read(buffer);
                if (size > 0) {
                    str=new String(buffer,0,size);
                }
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
            return str;
        }else{
            return "";
        }

    }
    public void flush() throws IOException {
        if(HabilitadoLectura()){
            read_2();
        }
    }
    public String read_menora13()
    {
        if(serialPort!=null){
            String str=null;
            int size;
            try {
                if (mInputStream == null)
                    return null;
                byte[] buffer = new byte[1024];
                size = mInputStream.read(buffer);
                if (size > 0 && size < 13) {
                    str=new String(buffer,0,size);
                }
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
            return str;
        }else{
            return "";
        }

    }
    public void write(String cmd)
    {
        if(serialPort!=null){
            byte[] mBuffer = cmd.getBytes();
            try {
                if (mOutputStream != null) {
                    mOutputStream.write(mBuffer);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

}
