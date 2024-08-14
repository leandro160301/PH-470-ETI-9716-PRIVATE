package com.service.PuertosSerie;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android_serialport_api.SerialPort;

public class PuertosSerie {
    SerialPort serialPort=null;
    OutputStream mOutputStream;
    InputStream mInputStream;
    String Puerto;
    int Baudrate=9600,StopBits=1,DataBits=8,Parity=0,FlowCon=0,Flags=0;


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
