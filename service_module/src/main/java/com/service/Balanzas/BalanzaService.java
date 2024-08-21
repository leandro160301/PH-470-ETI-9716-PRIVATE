package com.service.Balanzas;

import android.content.Context;
import android.content.SharedPreferences;
import androidx.appcompat.app.AppCompatActivity;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.service.Balanzas.Interfaz.Balanza;
import com.service.Balanzas.Clases.MINIMA_I;
import com.service.Balanzas.Clases.OPTIMA_I;
import com.service.Comunicacion.OnFragmentChangeListener;
import com.service.Balanzas.Clases.R31P30_I;
import com.service.PuertosSerie.PuertosSerie;
import com.service.R;
import com.service.Utils;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android_serialport_api.SerialPort;

public class BalanzaService implements Balanza, Balanza.Struct, Serializable {

    public Map<Integer, Struct> balanzas = new HashMap<>();
    public PuertosSerie serialPortA;
    public PuertosSerie serialPortB;
    public PuertosSerie serialPortC;
    public static final String PUERTO_A="/dev/ttyXRUSB0";
    public static final String PUERTO_B="/dev/ttyXRUSB1";
    public static final String PUERTO_C="/dev/ttyXRUSB2";
    private String NAME="SERVICE";
    public AppCompatActivity activity;
    public OnFragmentChangeListener fragmentChangeListener;
    int baudA=9600,baudB=9600,baudC=9600,stopBitA=1,stopBitB=1,stopBitC=1,dataBitsA=8,dataBitsB=8,dataBitsC=8,parityA=0,parityB=0,parityC=0;

    public BalanzaService(AppCompatActivity activity,OnFragmentChangeListener fragmentChangeListener) {
        this.activity = activity;
        this.fragmentChangeListener = fragmentChangeListener;
    }

    public void init() {
        initializateSerialPort(
                baudA, stopBitA, dataBitsA, parityA, 0, 0,
                baudB, stopBitB, dataBitsB, parityB, 0, 0,
                baudC, stopBitC, dataBitsC, parityC, 0, 0);
        List<Integer> balanzaslist= getBalanzas();
        if(balanzaslist.size()==0){
            balanzaslist.add(0);
            setBalanzas(balanzaslist);
        }
       PuertosSerie serialPort=serialPortA;
        for (int i=0;i<balanzaslist.size();i++) {
            if(i==1){
                serialPort=serialPortB;
            }
            if(i==2){
                serialPort=serialPortC;
            }
            if (balanzaslist.get(i) == 0) {
                Balanza.Struct bza=new OPTIMA_I(serialPort, i+1,activity,this);
                bza.init(i+1);
                balanzas.put(i + 1, bza);
            }
            if (balanzaslist.get(i) == 1) {
                Balanza.Struct bza=new MINIMA_I(serialPort, i+1,activity,this);
                bza.init(i+1);
                balanzas.put(i + 1, bza);
            }
            if (balanzaslist.get(i) == 2) {
                Balanza.Struct bza=new R31P30_I(serialPort, i+1,activity,this);
                bza.init(i+1);
                balanzas.put(i + 1, bza);
            }

            /*
            //CUANDO ES POR ID CREAMOS EL OBJETO CON LA BALANZA Y DESPUES COMO TANTO TENGAMOS DE ID AGREGAMOS EL MISMO OBJETO EN LA LISTA (REFERENCIA)
            //LA INICIALIZACION DEBERIAMOS AGREGARLA AL FINAL DE PARA QUE LE PASEMOS A LA CLASE LA CANTIDAD
            if (balanzaslist.get(i) == 3) {
                Balanza.Struct bza=new OPTIMA_ID_I(serialPort, i+1,activity,this,5); // 5 es la cantidad de optimas id
                // es importante que le mandemos el i+1 para que luego cuando preguntemos sepa en que indice debe devolver
                for(int j=0;j<cantidad;j++){
                    balanzas.put(i + j+ 1, bza);
                }
                bza.init(i+1);
            }*/

        }

    }

    public Boolean initializateSerialPort(int baudrate_A,int stopbit_A,int databits_A,int parity_A,int flowcon_A,int flags_A,int baudrate_B,int stopbit_B,int databits_B,int parity_B,int flowcon_B,int flags_B,int baudrate_C,int stopbit_C,int databits_C,int parity_C,int flowcon_C,int flags_C){
        serialPortA= new PuertosSerie();
        SerialPort Puerto_A_OPEN=serialPortA.open(PUERTO_A,baudrate_A,stopbit_A,databits_A,parity_A,flowcon_A,flags_A);
        serialPortB= new PuertosSerie();
        SerialPort Puerto_B_OPEN=serialPortB.open(PUERTO_B,baudrate_B,stopbit_B,databits_B,parity_B,flowcon_B,flags_B);
        serialPortC= new PuertosSerie();
        SerialPort Puerto_C_OPEN=serialPortC.open(PUERTO_C,baudrate_C,stopbit_C,databits_C,parity_C,flowcon_C,flags_C);
        if (Puerto_A_OPEN!=null&&Puerto_B_OPEN!=null&&Puerto_C_OPEN!=null){
            return false;
        }else{
            Utils.Mensaje("ERROR PUERTOS", R.layout.item_customtoasterror,activity);
            return true;
        }
    }

    public List<Integer> getBalanzas() {
        SharedPreferences Preferencias2 = activity.getSharedPreferences(NAME, Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = Preferencias2.getString("ModeloBalanza", null);
        Type type = new TypeToken<List<Integer>>() {}.getType();
        List<Integer> balanzas = gson.fromJson(json, type);
        return balanzas != null ? balanzas : new ArrayList<>();
    }
    public void setBalanzas(List<Integer> Balanza) {
        SharedPreferences Preferencias2 = activity.getSharedPreferences(NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor ObjEditor2 = Preferencias2.edit();
        Gson gson = new Gson();
        String json = gson.toJson(Balanza);
        ObjEditor2.putString("ModeloBalanza", json);
        ObjEditor2.apply();
    }

    public PuertosSerie getSerialPort(int numero){
        if(numero==1){
            return serialPortA;
        }
        if(numero==2){
            return serialPortB;
        }
        if(numero==3){
            return serialPortC;
        }
        return serialPortA;

    }
    public Balanza.Struct getBalanza(int numero){
        Balanza.Struct balanza = balanzas.get(numero);
        if (balanza != null) {
            return balanza;
        }
        return null;
    }

    @Override
    public float getNeto(int numero) {
        Balanza.Struct balanza = balanzas.get(numero);
        if (balanza != null) {
            return balanza.getNeto(numero);
        }
        return 0;
    }

    @Override
    public String getNetoStr(int numero) {
        Balanza.Struct balanza = balanzas.get(numero);
        if (balanza != null) {
            return balanza.getNetoStr(numero);
        }
        return "0";
    }

    @Override
    public float getBruto(int numero) {
        Balanza.Struct balanza = balanzas.get(numero);
        if (balanza != null) {
            return balanza.getBruto(numero);
        }
        return 0;
    }

    @Override
    public String getBrutoStr(int numero) {
        Balanza.Struct balanza = balanzas.get(numero);
        if (balanza != null) {
            return balanza.getBrutoStr(numero);
        }
        return "0";
    }

    @Override
    public float getTara(int numero) {
        Balanza.Struct balanza = balanzas.get(numero);
        if (balanza != null) {
            return balanza.getTara(numero);
        }
        return 0;
    }

    @Override
    public String getTaraStr(int numero) {
        Balanza.Struct balanza = balanzas.get(numero);
        if (balanza != null) {
            return balanza.getTaraStr(numero);
        }
        return "0";
    }

    @Override
    public void setTara(int numero) {
        Balanza.Struct balanza = balanzas.get(numero);
        if (balanza != null) {
            balanza.setTara(numero);
        }
    }

    @Override
    public void setCero(int numero) {
        Balanza.Struct balanza = balanzas.get(numero);
        if (balanza != null) {
            balanza.setCero(numero);
        }
    }

    @Override
    public void setTaraDigital(int numero, float TaraDigital) {
        Balanza.Struct balanza = balanzas.get(numero);
        if (balanza != null) {
            balanza.setTaraDigital(numero, TaraDigital);
        }
    }

    @Override
    public String getTaraDigital(int numero) {
        Balanza.Struct balanza = balanzas.get(numero);
        if (balanza != null) {
            return balanza.getTaraDigital(numero);
        }
        return "0";
    }

    @Override
    public Boolean getBandaCero(int numero) {
        Balanza.Struct balanza = balanzas.get(numero);
        if (balanza != null) {
            return balanza.getBandaCero(numero);
        }
        return false;
    }

    @Override
    public void setBandaCero(int numero, Boolean bandaCeroi) {
        Balanza.Struct balanza = balanzas.get(numero);
        if (balanza != null) {
            balanza.setBandaCero(numero,bandaCeroi);
        }
    }

    @Override
    public float getBandaCeroValue(int numero) {
        Balanza.Struct balanza = balanzas.get(numero);
        if (balanza != null) {
            return balanza.getBandaCeroValue(numero);
        }
        return 0;
    }

    @Override
    public void setBandaCeroValue(int numero, float bandaCeroValue) {
        Balanza.Struct balanza = balanzas.get(numero);
        if (balanza != null) {
            balanza.setBandaCeroValue(numero,bandaCeroValue);
        }
    }

    @Override
    public Boolean getEstable(int numero) {
        Balanza.Struct balanza = balanzas.get(numero);
        if (balanza != null) {
            return balanza.getEstable(numero);
        }
        throw new IllegalArgumentException("Balanza no encontrada");
    }

    @Override
    public String format(int numero,String peso) {
        Balanza.Struct balanza = balanzas.get(numero);
        if (balanza != null) {
            return balanza.format(numero,peso);
        }
        throw new IllegalArgumentException("Balanza no encontrada");
    }

    @Override
    public String getUnidad(int numero) {
        try {
            Balanza.Struct balanza = balanzas.get(numero);
            if (balanza != null) {
                return balanza.getUnidad(numero);
            }
            throw new IllegalArgumentException("Balanza no encontrada");
        } catch (IllegalArgumentException e) {
            //mainActivity.Mensaje("Error:"+e.getMessage(), R.layout.item_customtoasterror);
        }
        return "error";

    }

    @Override
    public String getPicoStr(int numero) {
        Balanza.Struct balanza = balanzas.get(numero);
        if (balanza != null) {
            return balanza.getPicoStr(numero);
        }
        throw new IllegalArgumentException("Balanza no encontrada");
    }

    @Override
    public float getPico(int numero) {
        Balanza.Struct balanza = balanzas.get(numero);
        if (balanza != null) {
            return balanza.getPico(numero);
        }
        throw new IllegalArgumentException("Balanza no encontrada");
    }

    @Override
    public void init(int numero) {
        Balanza.Struct balanza = balanzas.get(numero);
        if (balanza != null) {
            balanza.init(numero);
        }
    }

    @Override
    public void stop(int numero) {

    }

    @Override
    public void start(int numero) {
        Balanza.Struct balanza = balanzas.get(numero);
        if (balanza != null) {
            balanza.start(numero);
        }
    }


    @Override
    public void openCalibracion(int numero) {
        Balanza.Struct balanza = balanzas.get(numero);
        if (balanza != null) {
            balanza.openCalibracion(numero);
        }
    }

    @Override
    public Boolean getSobrecarga(int numero) {
        Balanza.Struct balanza = balanzas.get(numero);
        if (balanza != null) {
            return balanza.getSobrecarga(numero);
        }
        return false;
    }

    @Override
    public String getEstado(int numero) {
        Balanza.Struct balanza = balanzas.get(numero);
        if (balanza != null) {
            return balanza.getEstado(numero);
        }
        return "";
    }

    @Override
    public void setEstado(int numero, String estado) {
        Balanza.Struct balanza = balanzas.get(numero);
        if (balanza != null) {
            balanza.setEstado(numero,estado);
        }
    }

    @Override
    public void Itw410FrmSetear(int numero, String setPoint, int Salida) {

    }

    @Override
    public String Itw410FrmGetSetPoint(int numero) {
        return null;
    }

    @Override
    public int Itw410FrmGetSalida(int numero) {
        return 0;
    }

    @Override
    public void Itw410FrmStart(int numero) {

    }

    @Override
    public int Itw410FrmGetEstado(int numero) {
        return 1;
    }

    @Override
    public String Itw410FrmGetUltimoPeso(int numero) {
        return "10.45";
    }

    @Override
    public int Itw410FrmGetUltimoIndice(int numero) {
        return 0;
    }

}
