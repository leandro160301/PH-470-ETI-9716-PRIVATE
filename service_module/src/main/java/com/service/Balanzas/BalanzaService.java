package com.service.Balanzas;

import static com.service.Utils.Mensaje;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;

import androidx.appcompat.app.AppCompatActivity;

import com.service.Balanzas.Clases.ITW410;
import com.service.Balanzas.Clases.MINIMA_I;
import com.service.Balanzas.Fragments.ServiceFragment2;
import com.service.Balanzas.Interfaz.Balanza;
import com.service.Balanzas.Clases.OPTIMA_I;
import com.service.Balanzas.Interfaz.modbus;
import com.service.Balanzas.Interfaz.serviceDevice;
import com.service.Comunicacion.OnFragmentChangeListener;
import com.service.Balanzas.Clases.R31P30_I;
import com.service.Impresora.ImprimirEstandar;
import com.service.Modbus.ModbusMasterRtu;
import com.service.Modbus.Req.ModbusReqRtuMaster;
import com.service.PuertosSerie.PuertosSerie2;
import com.service.R;
import com.service.Utils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import android_serialport_api.SerialPort;

public class BalanzaService implements Serializable {
    public ArrayList<ModbusMasterRtu> ModbusList;
    private PuertosSerie2 serialPortA;
    private PuertosSerie2 serialPortB;
    ServiceFragment2 ServiceFragment2= new ServiceFragment2();

    private PuertosSerie2 serialPortC;
    public static Impresoras Impresoras;
    public static Balanzas Balanzas;
    public static final String PUERTO_A="/dev/ttyXRUSB0";
    public static final String PUERTO_B="/dev/ttyXRUSB1";
    public static final String PUERTO_C="/dev/ttyXRUSB2";
    public PuertosSerie2 A,B,C;
    private String NAME="SERVICE";
    static BalanzaService Service;
    int baudA=9600,baudB=9600,baudC=9600,stopBitA=1,stopBitB=1,stopBitC=1,dataBitsA=8,dataBitsB=8,dataBitsC=8,parityA=0,parityB=0,parityC=0;
    public static AppCompatActivity activity;
    public OnFragmentChangeListener fragmentChangeListener;



    public BalanzaService( AppCompatActivity activity, OnFragmentChangeListener fragmentChangeListener) {
        this.Service = this;
        this.activity =  activity;
        this.fragmentChangeListener = fragmentChangeListener;
    }


    public Boolean initializateSerialPort(int baudrate_A,int stopbit_A,int databits_A,int parity_A,int flowcon_A,int flags_A,int baudrate_B,int stopbit_B,int databits_B,int parity_B,int flowcon_B,int flags_B,int baudrate_C,int stopbit_C,int databits_C,int parity_C,int flowcon_C,int flags_C){
        //Inicia los puertos de la pantalla

         A = new PuertosSerie2();
        SerialPort AS=A.open(PUERTO_A,baudrate_A,stopbit_A,databits_A,parity_A,flowcon_A,flags_A);
         B= new PuertosSerie2();
        SerialPort BS=B.open(PUERTO_B,baudrate_B,stopbit_B,databits_B,parity_B,flowcon_B,flags_B);
         C= new PuertosSerie2();
        SerialPort CS=C.open(PUERTO_C,baudrate_C,stopbit_C,databits_C,parity_C,flowcon_C,flags_C);
        if (AS!=null&&BS!=null&&CS!=null){
            serialPortA=A;
            serialPortB=B;
            serialPortC=C;
            return false;
        }else{
            Mensaje("ERROR_PUERTOS",R.layout.item_customtoasterror,activity);
            return true;
        }
    }

    public void init() {

        try{
            Utils.clearCache(activity.getApplicationContext());
            initializateSerialPort(
                    baudA, stopBitA, dataBitsA, parityA, 0, 0,
                    baudB, stopBitB, dataBitsB, parityB, 0, 0,
                    baudC, stopBitC, dataBitsC, parityC, 0, 0);
            Thread.sleep(1000);
        }catch (Exception e){
        }
         Impresoras  = new Impresoras();
         Balanzas = new Balanzas();
         Balanzas.initializateBalanza();
    }
    public void openServiceFragment2(){
        ServiceFragment2 fragment = ServiceFragment2.newInstance(Service);
        Bundle args = new Bundle();
        args.putSerializable("instanceService", Service);
        fragmentChangeListener.openFragmentService( fragment,args);
    }
    public class Impresoras {
        public void setModo(int Modo,Integer num) {
            SharedPreferences Preferencias=activity.getSharedPreferences("Impresoras",Context.MODE_PRIVATE);
            SharedPreferences.Editor ObjEditor=Preferencias.edit();
            ObjEditor.putInt("Modo_"+num,Modo);
            ObjEditor.apply();
        }
        public void setIP(String ip,Integer num) {
            SharedPreferences Preferencias=activity.getSharedPreferences("Impresoras",Context.MODE_PRIVATE);
            SharedPreferences.Editor ObjEditor=Preferencias.edit();
            ObjEditor.putString("IP_"+num,ip);
            ObjEditor.apply();
        }
        public void setMAC(String MAC,Integer num) {
            SharedPreferences Preferencias=activity.getSharedPreferences("Impresoras",Context.MODE_PRIVATE);
            SharedPreferences.Editor ObjEditor=Preferencias.edit();
            ObjEditor.putString("MAC_"+num,MAC);
            ObjEditor.apply();
        }
        public void ImprimirEstandar(Integer numImpresora, String etiqueta){
            List<Integer> impresorasTipoList = getImpresoras();
            Integer tipoImpresora = impresorasTipoList.get(numImpresora);
            if(tipoImpresora !=null){
                switch (tipoImpresora +1){
                    case 1:{
                        ImprimirEstandar Impresora  =new ImprimirEstandar(activity.getApplicationContext(),activity,etiqueta, numImpresora,serialPortA);
                        Impresora.EnviarEtiqueta();
                        break;
                    }
                    case 2:{
                        ImprimirEstandar Impresora  =new ImprimirEstandar(activity.getApplicationContext(),activity,etiqueta, numImpresora,serialPortB);
                        Impresora.EnviarEtiqueta();
                        break;
                    }
                    case 3:{
                        ImprimirEstandar Impresora  =new ImprimirEstandar(activity.getApplicationContext(),activity,etiqueta, numImpresora,serialPortC);
                        Impresora.EnviarEtiqueta();
                        break;
                    }
                    default:{
                        ImprimirEstandar Impresora = new ImprimirEstandar(activity.getApplicationContext(),activity,etiqueta, numImpresora,null);
                        Impresora.EnviarEtiqueta();
                        break;
                    }
                }
            }else{
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Mensaje("No existe esta Impresora en configuracion Service", R.layout.item_customtoasterror,activity);
                    }
                });
            }
        }
        public List<Integer> getImpresoras() {
            List<Integer> impresoraList = new ArrayList<Integer>() ;
            Integer tipoImpresora =1;
            Integer numImpresora =0;
            while(tipoImpresora !=-1){
                SharedPreferences Preferencias=activity.getSharedPreferences("Impresoras", Context.MODE_PRIVATE);
                tipoImpresora =(Preferencias.getInt("Modo_"+ numImpresora,-1));
                numImpresora++;
                if(tipoImpresora ==-1){
                    impresoraList.add(null);
                }else{
                    impresoraList.add(tipoImpresora);
                }
            }
            return impresoraList;//balanzas != null ? balanzas : new ArrayList<>();
        }
    }
    public class Balanzas implements Balanza, Balanza.Struct{
        private Map<Integer, Struct> balanzas = new HashMap<>();

        @Override
        public String getEstado(int numBza) {
            Struct balanza = balanzas.get(numBza);
            if (balanza != null) {
                return balanza.getEstado(numBza);
            }
            return "";
        }
        @Override
        public void setEstado(int numBza, String estadoBZA) {
            Struct balanza = balanzas.get(numBza);
            if (balanza != null) {
                balanza.setEstado(numBza,estadoBZA);
            }
        }

        private void SettingsDef(){
            String tipo="";
            int num=0;
            int numbza=0;
            SharedPreferences Preferencias=activity.getSharedPreferences("devicesService",Context.MODE_PRIVATE);
            ArrayList<serviceDevice> Arraydevicesaux= new ArrayList<serviceDevice>();
            while(num<5){
                serviceDevice x=new serviceDevice();
                tipo= Preferencias.getString("Tipo_"+num,"fin");
                if(tipo.equals("fin")&&num==0){
                    tipo="Balanza";
                }
                Boolean seteo=false;
                seteo= Preferencias.getBoolean("seteo_"+num,false);
                switch (num){
                    case 0:{
                        if(!seteo){

                            ArrayList<String> arrayList=new ArrayList<>();
                            arrayList.add("9600");arrayList.add("8");arrayList.add("1");arrayList.add("0");
                            x.setTipo(0);
                            x.setNB(0);
                            x.setDireccion(arrayList);
                            x.setSeteo(true);
                            x.setSalida("PuertoSerie 1");
                            x.setID(0);
                            addDevice(x,false);
                        }
                        break;
                    }
                    case 1:{

                        break;
                    }
                    case 2:{

                        break;
                    }
                    case 3:{

                        break;
                    }
                    case 4:{

                        break;
                    }
                }
                num++;
            }
            ;
        }
        public void initializateBalanza(){
            SettingsDef();
            ArrayList<serviceDevice> balanzasList = get_arrayServiceDevices();
            CountDownLatch latch = new CountDownLatch(balanzasList.size());
       //     if(balanzasList.size()==0){
         //       balanzasList.
                //setBalanzas(balanzasList);
          //  }
          //  PuertosSerie2 serialPort=serialPortA;
            for (int i = 0; i< balanzasList.size(); i++) {
                String puerto="";
                int balanzalenght=balanzasList.size();
                switch (balanzasList.get(i).getSalida()){
                    case "PuertoSerie 1":{
                        puerto=PUERTO_A;

                        break;
                    }
                    case "PuertoSerie 2":{
                        puerto=PUERTO_B;

                        break;
                    }
                    case "PuertoSerie 3":{
                        puerto=PUERTO_C;

                        break;
                    }
                    case "USB":{

                        break;
                    }
                    case "TCP/IP":{

                        break;
                    }
                    case "MAC":{

                        break;
                    }
                }
                System.out.println("OPTIMA "+puerto);
                PuertosSerie2 serialPort = new PuertosSerie2();
                switch (puerto){
                    case PUERTO_A:{
                        serialPort=serialPortA;
                        break;
                    }

                    case PUERTO_B:{
                        serialPort=serialPortB;
                        break;
                    }

                    case PUERTO_C:{
                        serialPort=serialPortC;
                        break;
                    }
                }
                if (balanzasList.get(i).getModelo() == 0) { // 0
                    System.out.println("OPTIMA");
                    //seria ideal inicializar el puerto aca___
                    if(balanzasList.get(i).getID()==0) {
                        Struct bza = new OPTIMA_I(serialPortA,balanzasList.get(i).getID(), activity, Service, fragmentChangeListener);
                        bza.init(1);
                        balanzas.put(1, bza);
                    }else{
                       // Struct bza = new OPTIMA_ID_I(serialPort, balanzasList.get(i).getID(), activity, Service, fragmentChangeListener);
                       // bza.init(balanzalenght + 1);
                       // balanzas.put(balanzalenght + 1, bza);
                    }
                    latch.countDown();
                }
                if (balanzasList.get(i).getModelo()  == 1) {
                    System.out.println("MINIMA");
                    Struct BZA =new MINIMA_I(serialPort,  balanzasList.get(i).getID(),activity,Service,fragmentChangeListener);
                    BZA.init(balanzalenght+1);
                    balanzas.put(balanzalenght + 1, BZA);
                    latch.countDown();
                }
                if (balanzasList.get(i).getModelo()  == 2) {

                    System.out.println("R31P30");
                    Struct BZA =new R31P30_I(serialPort,  balanzasList.get(i).getID(),activity,fragmentChangeListener);
                    BZA.init(balanzalenght+1);
                    balanzas.put(balanzalenght + 1, BZA);
                    latch.countDown();
                }
                if(balanzasList.get(i).getModelo() ==3){ // 3
                    System.out.println("ITW410");
                    int finalI2 = i;
                   // pasar adentro  el modbusconfig finalPort,modbus.getBaud(),modbus.getDatabit(),modbus.getStopbit(),modbus.getParity()
                     System.out.println("position bza"+finalI2+1);
                   ArrayList<modbus> modbuslistconfig= new ArrayList<>();//= getConfigModbus();
                    try {
                        for (modbus modbus : modbuslistconfig) {
                            if (modbus.getPortselected()!=-1) { // 20 es error
                             //   ModbusMasterRtu modbusmasterinit= new ModbusMasterRtu();
                                if(modbus.getPortselected()!=4) { // 4 seria "RED" o TCP
                                    String finalPort = puerto;
                                    int finalI1 = balanzalenght;
                                    int finalI3 = i;
                                    new Thread(new Runnable() {
                                        @Override
                                        public void run() {
                                            ModbusReqRtuMaster Modbus = null;
                                            try {
                                                ITW410 bza1 = new ITW410(balanzasList.get(finalI3).getID(), Service, fragmentChangeListener, 1,balanzasList.get(finalI3).getDireccion());
                                                bza1.init(finalI2+1);
                                                balanzas.put(finalI2+1,bza1);
                                            } catch (Exception e) {
                                                throw new RuntimeException(e);
                                            }
                                        }
                                    });
                                    int finalI = balanzalenght+1;
                                    new Thread(new Runnable() {
                                        @Override
                                        public void run() {
                                            ModbusReqRtuMaster Modbus = null;
                                            try {

                                                ITW410 bza2 = new ITW410(balanzasList.get(finalI3).getID(), Service, fragmentChangeListener, 2,balanzasList.get(finalI3).getDireccion());
                                                bza2.init(finalI2+1);

                                                balanzas.put(finalI2+1,bza2); // tengo que mantener la posicion de la lista balanzalist pero aca tengo que correr 1 posicion. como no hay add()
                                            } catch (Exception e) {
                                                throw new RuntimeException(e);
                                            }


                                        }
                                    });



                                    //   initializateModbus();
                                }else{
                                    //MODBUSTCP
                                }
                            }
                        }
                    } catch(Exception e) {
                   }
                    latch.countDown();
                }

            }
            try {
                latch.await(10000, TimeUnit.MILLISECONDS);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        public ArrayList<Integer> getBalanzas() {
            SharedPreferences Preferencias = activity.getSharedPreferences("devicesService", Context.MODE_PRIVATE);
            String tipo="";
            ArrayList<Integer> balanzas = new ArrayList<>();
            int num=0;
            while(tipo!="fin"){
                tipo= Preferencias.getString("Tipo_"+num,"fin");
                if(tipo.equals("fin")&&num==0){
                    tipo="Balanza";
                }
                if(tipo.contains("Balanza")){
                    String Modelobza = Preferencias.getString("Modelo_"+num,"Optima");
                     switch (Modelobza){
                        case "Optima":{
                            balanzas.add(0);
                            break;
                        }
                        case "Minima":{

                            balanzas.add(1);
                            break;
                        }
                        case "R31P30":{

                            balanzas.add(2);
                            break;
                        }
                        case "ITW410":{
                            balanzas.add(3);
                            break;
                        }
                        default:{
                            balanzas.add(-1);
                            break;
                        }
                    }

                }
                num++;
             }
           return balanzas != null ? balanzas : new ArrayList<>();
        }

       // public void setBalanzas(List<Integer> Balanza) {

           /* SharedPreferences Preferencias = activity.getSharedPreferences(NAME, Context.MODE_PRIVATE);
            SharedPreferences.Editor ObjEditor = Preferencias.edit();
            Gson gson = new Gson();
            String json = gson.toJson(Balanza);
            ObjEditor.putString("ModeloBalanza", json);
            ObjEditor.apply();*/
       // }


        public PuertosSerie2 getSerialPort(int numeroPuerto){
            if(numeroPuerto ==1){
                return serialPortA;
            }
            if(numeroPuerto ==2){
                return serialPortB;
            }
            if(numeroPuerto ==3){
                return serialPortC;
            }
            return serialPortA;

        }
        public ArrayList<serviceDevice> get_balanzalistglob(){
            ArrayList<serviceDevice> ArrayBalanzas = new ArrayList<serviceDevice>();
            SharedPreferences Preferencias = activity.getSharedPreferences("devicesService", Context.MODE_PRIVATE);
            String tipo="";
            serviceDevice balanza= new serviceDevice();
            int num=0;
            int numbza=0;
            while(tipo!="fin") {
                tipo = Preferencias.getString("Tipo_" + num, "fin");
                if(tipo.contains("fin") && num==0){
                    tipo="Balanza";
                }
                if(tipo.equals("Balanza")) {
                    balanza.setNB(numbza);
                    numbza++;
                    String Modelobza = Preferencias.getString("Modelo_" + num, "Optima");
                    String Salida = Preferencias.getString("Salida_" + num, "PuertoSerie 1");
                    int NumeroID = Preferencias.getInt("ID_" + num, 1);
                    Boolean seteo = Preferencias.getBoolean("seteo_" + num, false);
                    ArrayList<String> listaux = new ArrayList<String>();
                    if (Salida.contains("PuertoSerie")) {
                         String baud = Preferencias.getString("Baud_" + num, "9600");
                        String dataB = Preferencias.getString("DataB_" + num, "8");
                        String stopB = Preferencias.getString("StopB_" + num, "1");
                        String parity = Preferencias.getString("Parity_" + num, "0");
                        listaux.add(baud);
                        listaux.add(dataB);
                        listaux.add(stopB);
                        listaux.add(parity);
                        tipo = "fin";
                    }else if(Salida.contains("IP")){
                        String Direccion = Preferencias.getString("Direccion_" + num, "1.1.1.1");
                        listaux.add(Direccion);
                    }
                    if ((ArrayBalanzas.size() < 1)) {
                        listaux.add("");
                        listaux.add("");
                        listaux.add("");
                        listaux.add("");
                        listaux.add("");

                    }
                    int Modelo =-1;
                    switch (Modelobza){
                        case "Optima":{
                            Modelo =0;
                            break;
                        }
                        case "Minima":{
                            Modelo =1;
                            break;
                        }
                        case "R31P30":{
                            Modelo =2;
                            break;
                        }
                        case "ITW410":{
                            Modelo =3;
                            break;
                        }
                        default: {
                            Modelo =-1;
                            break;
                        }
                    }
                        balanza.setID(NumeroID);
                        balanza.setModelo(Modelo);
                        balanza.setSalida(Salida);
                        balanza.setDireccion(listaux);
                        balanza.setTipo(0);
                        balanza.setSeteo(seteo);
                        ArrayBalanzas.add(balanza);
                }
                num++;
            }
            return ArrayBalanzas;
        }
        public ArrayList<serviceDevice> get_balanzalistPerPort(String Salida){
            String  Salidastr="";
            switch (Salida){
                case "Puerto Serie 1":{
                    Salidastr="PuertoSerie 1";
                    break;
                }
                case "Puerto Serie 2":{
                    Salidastr="PuertoSerie 2";
                    break;
                }
                case "Puerto Serie 3":{
                    Salidastr="PuertoSerie 3";
                    break;
                }
                default:{
                    break;
                }

            }
            ArrayList<serviceDevice> ArrayBalanzas = new ArrayList<serviceDevice>();
            SharedPreferences Preferencias = activity.getSharedPreferences("devicesService", Context.MODE_PRIVATE);
            String tipo="";
            int num=0;
            int numbza=0;
            while(tipo!="fin") {
                serviceDevice balanza= new serviceDevice();
                tipo = Preferencias.getString("Tipo_" + num, "fin");
                if(tipo.contains("fin") && num==0){
                    tipo="Balanza";
                }
                if(tipo.equals("Balanza")) {
//                    balanza.setNB(numbza);
                    balanza.setNB(numbza);
                    numbza++;
                    //numbza++;
                    Boolean seteo = Preferencias.getBoolean("seteo_" + num, false);
                    String Modelobza="";

                        Modelobza = Preferencias.getString("Modelo_" + num, "Optima");


                    String Salidaaux = Preferencias.getString("Salida_" + num, "PuertoSerie 1");
                    int NumeroID = Preferencias.getInt("ID_" + num, 1);
                    System.out.println("NumeroID "+NumeroID);
                    ArrayList<String> listaux = new ArrayList<String>();
                    if (Salidastr.contains("PuertoSerie")) {
                        String baud = Preferencias.getString("Baud_" + num, "9600");
                        String dataB = Preferencias.getString("DataB_" + num, "8");
                        String stopB = Preferencias.getString("StopB_" + num, "1");
                        String parity = Preferencias.getString("Parity_" + num, "0");
                        listaux.add(baud);
                        listaux.add(dataB);
                        listaux.add(stopB);
                        listaux.add(parity);
                    }else if(Salidastr.contains("IP")){
                        String Direccion = Preferencias.getString("Direccion_" + num, "1.1.1.1");
                        listaux.add(Direccion);
                    }
                    if ((ArrayBalanzas.size() < 1)) {
                        listaux.add("");
                        listaux.add("");
                        listaux.add("");
                        listaux.add("");
                        listaux.add("");

                    }
                    if (Salidaaux.equals(Salidastr)) {
                        switch (Modelobza){
                            case "Optima":{
                                balanza.setModelo(0);
                                break;
                            }
                            case "Minima":{
                                balanza.setModelo(1);
                                break;
                            }
                            case "R31P30":{
                                balanza.setModelo(2);
                                break;
                            }
                            case "ITW410":{
                                balanza.setModelo(3);
                                break;
                            }
                            default:{
                                balanza.setModelo(-1);
                            }
                        }
                        balanza.setID(NumeroID);
                        balanza.setSalida(Salida);
                        balanza.setDireccion(listaux);
                        balanza.setTipo(0);
                        balanza.setSeteo(seteo);
                        ArrayBalanzas.add(balanza);
                    }

                }
                num++;
            }



            return ArrayBalanzas;

        }
        public ArrayList<serviceDevice> get_balanzalist2(String Salidax){
            ArrayList<serviceDevice> ArrayBalanzas = new ArrayList<serviceDevice>();
            SharedPreferences Preferencias = activity.getSharedPreferences("devicesService", Context.MODE_PRIVATE);
            String tipo="";
            int num=0;
            int numbza=0;
            String Modelostr="";
            String Salidastr="";
            switch (Salidax){
                case "Puerto Serie 1":{
                    Salidastr="PuertoSerie 1";
                    break;
                }
                case "Puerto Serie 2":{
                    Salidastr="PuertoSerie 2";
                    break;
                }
                case "Puerto Serie 3":{
                    Salidastr="PuertoSerie 3";
                    break;
                }

            }int Modelo=0;
            while(tipo!="fin") {
                serviceDevice balanza= new serviceDevice();
                tipo = Preferencias.getString("Tipo_" + num, "fin");
                if(tipo.contains("fin") && num==0){
                    tipo="Balanza";
                }
                if(tipo.equals("Balanza")) {
//                    balanza.setNB(numbza);
                    balanza.setNB(numbza);
                    numbza++;
                    //numbza++;
                    Boolean seteo = Preferencias.getBoolean("seteo_" + num, false);
                    String Modelobza="";
                    if(seteo){
                        Modelobza = Preferencias.getString("Modelo_" + num, "Optima");
                    }else{
                        Modelobza = Preferencias.getString("Modelo_" + num, Modelostr);
                    }

                    switch (Modelostr){
                        case "Optima":{
                            Modelo=0;
                            break;
                        }
                        case "Minima":{
                            Modelo=1;
                            break;
                        }
                        case "R31P30":{
                            Modelo=2;
                            break;
                        }
                        case "ITW410":{
                            Modelo=3 ;
                            break;
                        }
                        default: {
                            Modelo = -1;
                            break;
                        }
                    }
                    String Salida = Preferencias.getString("Salida_" + num, "PuertoSerie 1");
                    int NumeroID = Preferencias.getInt("ID_" + num, 1);
                    System.out.println("NumeroID "+NumeroID);
                    ArrayList<String> listaux = new ArrayList<String>();
                    if (Salida.contains("PuertoSerie")) {
                        String baud = Preferencias.getString("Baud_" + num, "9600");
                        String dataB = Preferencias.getString("DataB_" + num, "8");
                        String stopB = Preferencias.getString("StopB_" + num, "1");
                        String parity = Preferencias.getString("Parity_" + num, "0");
                        listaux.add(baud);
                        listaux.add(dataB);
                        listaux.add(stopB);
                        listaux.add(parity);
                    }else if(Salida.contains("IP")){
                        String Direccion = Preferencias.getString("Direccion_" + num, "1.1.1.1");
                        listaux.add(Direccion);
                    }
                    if ((ArrayBalanzas.size() < 1)) {
                        listaux.add("");
                        listaux.add("");
                        listaux.add("");
                        listaux.add("");
                        listaux.add("");

                    }
                    if (Salida.equals(Salidastr)) {
                        balanza.setID(NumeroID);
                        balanza.setModelo(Modelo);
                        balanza.setSalida(Salida);
                        balanza.setDireccion(listaux);
                        balanza.setTipo(0);
                        balanza.setSeteo(seteo);

                        ArrayBalanzas.add(balanza);
                    }

                }
                num++;
            }
            if(ArrayBalanzas.size()==1){
                ArrayBalanzas.get(0).setModelo(Modelo);
            }



            return ArrayBalanzas;

        }

        public ArrayList<serviceDevice> get_balanzalist(int Modelo){
            ArrayList<serviceDevice> ArrayBalanzas = new ArrayList<serviceDevice>();
            SharedPreferences Preferencias = activity.getSharedPreferences("devicesService", Context.MODE_PRIVATE);
            String tipo="";
            int num=0;
            int numbza=0;
            String Modelostr="";
            switch (Modelo){
                case 0:{
                    Modelostr="Optima";
                    break;
                }
                case 1:{
                    Modelostr="Minima";
                    break;
                }
                case 2:{
                    Modelostr="R31P30";
                    break;
                }
                case 3:{
                    Modelostr= "ITW410";
                    break;
                }
                default: {
                    Modelostr = "none";
                    break;
                }
            }

             while(tipo!="fin") {
                serviceDevice balanza= new serviceDevice();
                tipo = Preferencias.getString("Tipo_" + num, "fin");
                 if(tipo.contains("fin") && num==0){
                    tipo="Balanza";
                }
                if(tipo.equals("Balanza")) {
//                    balanza.setNB(numbza);
                    balanza.setNB(numbza);
                    numbza++;
                    //numbza++;
                    Boolean seteo = Preferencias.getBoolean("seteo_" + num, false);
                    String Modelobza="";
                    if(seteo){
                         Modelobza = Preferencias.getString("Modelo_" + num, "Optima");
                    }else{
                         Modelobza = Preferencias.getString("Modelo_" + num, Modelostr);
                    }

                    String Salida = Preferencias.getString("Salida_" + num, "PuertoSerie 1");
                    int NumeroID = Preferencias.getInt("ID_" + num, 1);
                    System.out.println("NumeroID "+NumeroID);
                    ArrayList<String> listaux = new ArrayList<String>();
                    if (Salida.contains("PuertoSerie")) {
                        String baud = Preferencias.getString("Baud_" + num, "9600");
                        String dataB = Preferencias.getString("DataB_" + num, "8");
                        String stopB = Preferencias.getString("StopB_" + num, "1");
                        String parity = Preferencias.getString("Parity_" + num, "0");
                        listaux.add(baud);
                        listaux.add(dataB);
                        listaux.add(stopB);
                        listaux.add(parity);
                    }else if(Salida.contains("IP")){
                        String Direccion = Preferencias.getString("Direccion_" + num, "1.1.1.1");
                        listaux.add(Direccion);
                    }
                    if ((ArrayBalanzas.size() < 1)) {
                        listaux.add("");
                        listaux.add("");
                        listaux.add("");
                        listaux.add("");
                        listaux.add("");

                    }
                      if (Modelobza.equals(Modelostr)) {
                        balanza.setID(NumeroID);
                        balanza.setModelo(Modelo);
                        balanza.setSalida(Salida);
                        balanza.setDireccion(listaux);
                        balanza.setTipo(0);
                        balanza.setSeteo(seteo);

                        ArrayBalanzas.add(balanza);
                    }

                }
                num++;
            }
            if(ArrayBalanzas.size()==1){
                ArrayBalanzas.get(0).setModelo(Modelo);
            }



            return ArrayBalanzas;

        }
   /*     public ArrayList<String> get_primeraDireccion(int Modelo){

            ArrayList<String> direccion = new ArrayList<String>();
            SharedPreferences Preferencias = activity.getSharedPreferences("devicesService", Context.MODE_PRIVATE);
            String tipo="";
            ArrayList<Integer> balanzas = new ArrayList<>();
            int num=0;
            String Modelostr="";
            System.out.println("PRIMERA VEZ ? "+Modelo);
            switch (Modelo){
                case 0:{
                    Modelostr="Optima";
                    break;
                }
                case 1:{
                    Modelostr="Minima";
                    break;
                }
                case 2:{
                    Modelostr="R31P30";
                    break;
                }
                case 3:{
                    Modelostr= "ITW410";
                    break;
                }
                default: {
                    Modelostr = "none";
                    break;
                }
                }

            while(tipo!="fin") {
                tipo = Preferencias.getString("Tipo_" + num, "fin");
                if(tipo.contains("fin") && num==0){
                    tipo="Balanza";
                }

                String Modelobza = Preferencias.getString("Modelo_" + num, "Optima");
                String Salida = Preferencias.getString("Salida_" + num, "PuertoSerie 1");
                int NumeroID=Preferencias.getInt("ID_"+num,1);
                if (Salida.contains("PuertoSerie") && Modelobza.equals(Modelostr)) {

                    System.out.println("PRIMERA VEZ ? "+num);
                   String baud = Preferencias.getString("Baud_" + num, "9600");
                    String dataB = Preferencias.getString("DataB_" + num, "8");
                    String stopB = Preferencias.getString("StopB_" + num, "1");
                    String parity = Preferencias.getString("Parity_" + num, "0");

                    System.out.println("PRIMERA VEZ ? "+dataB);
                    direccion.add(baud);
                    direccion.add(dataB);
                    direccion.add(stopB);
                    direccion.add(parity);
                    direccion.add(Salida);
                    direccion.add(String.valueOf(NumeroID));
                    tipo="fin";
                }
                num++;
            }
            if((direccion.size()<1)){
                direccion.add("");
                direccion.add("");
                direccion.add("");
                direccion.add("");
                direccion.add("");

            }
            return  direccion;

        }*/

        public Boolean initializateSerialPort(int baudrate_A,int stopbit_A,int databits_A,int parity_A,int flowcon_A,int flags_A,int baudrate_B,int stopbit_B,int databits_B,int parity_B,int flowcon_B,int flags_B,int baudrate_C,int stopbit_C,int databits_C,int parity_C,int flowcon_C,int flags_C){
            serialPortA= new PuertosSerie2();
            SerialPort Puerto_A_OPEN=serialPortA.open(PUERTO_A,baudrate_A,stopbit_A,databits_A,parity_A,flowcon_A,flags_A);
            serialPortB= new PuertosSerie2();
            SerialPort Puerto_B_OPEN=serialPortB.open(PUERTO_B,baudrate_B,stopbit_B,databits_B,parity_B,flowcon_B,flags_B);
            serialPortC= new PuertosSerie2();
            SerialPort Puerto_C_OPEN=serialPortC.open(PUERTO_C,baudrate_C,stopbit_C,databits_C,parity_C,flowcon_C,flags_C);
            if (Puerto_A_OPEN!=null&&Puerto_B_OPEN!=null&&Puerto_C_OPEN!=null){
                return false;
            }else{
                Mensaje("ERROR PUERTOS", R.layout.item_customtoasterror,activity);
                return true;
            }
        }
        @Override
        public void Itw410FrmSetTiempoEstabilizacion(int numero, int Tiempo) {
           Struct balanza = balanzas.get(numero);
            if(balanza!=null) {
           balanza.Itw410FrmSetTiempoEstabilizacion(numero, Tiempo);
            }}

        public Struct getBalanza(int numeroBza){
            Struct balanza = balanzas.get(numeroBza);
            if (balanza != null) {
                return balanza;
            }
            return null;
        }


        @Override
        public void setID(int numID,int numBza) {
            Struct balanza = balanzas.get(numBza);
            if (balanza != null) {
                balanza.setID(numID,numBza);
            }
        }

        @Override
        public int getID(int numBza) {
            Struct balanza = balanzas.get(numBza);
            if (balanza != null) {
                return balanza.getID(numBza);
            }else{
                return 0;
            }

        }

        @Override
        public float getNeto(int numBza) {
            Struct balanza = balanzas.get(numBza);
            if (balanza != null) {
                return balanza.getNeto(numBza);
            }
            return 0;
        }

        @Override
        public String getNetoStr(int numBza) {
            Struct balanza = balanzas.get(numBza);
            if (balanza != null) {
                return balanza.getNetoStr(numBza);
            }
            return "0";
        }

        @Override
        public float getBruto(int numBza) {
            Struct balanza = balanzas.get(numBza);
            if (balanza != null) {
                return balanza.getBruto(numBza);
            }
            return 0;
        }

        @Override
        public String getBrutoStr(int numBza) {
            Struct balanza = balanzas.get(numBza);
            if (balanza != null) {
                return balanza.getBrutoStr(numBza);
            }
            return "0";
        }

        @Override
        public float getTara(int numBza) {
            Struct balanza = balanzas.get(numBza);
            if (balanza != null) {
                return balanza.getTara(numBza);
            }
            return 0;
        }

        @Override
        public String getTaraStr(int numBza) {
            Struct balanza = balanzas.get(numBza);
            if (balanza != null) {
                return balanza.getTaraStr(numBza);
            }
            return "0";
        }

        @Override
        public void setTara(int numBza) {
            Struct balanza = balanzas.get(numBza);
            if (balanza != null) {
                balanza.setTara(numBza);
            }
        }

        @Override
        public void setCero(int numBza) {
            Struct balanza = balanzas.get(numBza);
            if (balanza != null) {
                balanza.setCero(numBza);
            }
        }

        @Override
        public void setTaraDigital(int numBza, float TaraDigital) {
            Struct balanza = balanzas.get(numBza);
            if (balanza != null) {
                balanza.setTaraDigital(numBza, TaraDigital);
            }
        }

        @Override
        public String getTaraDigital(int numBza) {
            Struct balanza = balanzas.get(numBza);
            if (balanza != null) {
                return balanza.getTaraDigital(numBza);
            }
            return "0";
        }

        @Override
        public Boolean getBandaCero(int numBza) {
            Struct balanza = balanzas.get(numBza);
            if (balanza != null) {
                return balanza.getBandaCero(numBza);
            }
            return false;
        }

        @Override
        public void setBandaCero(int numBza, Boolean bandaCeroi) {
            Struct balanza = balanzas.get(numBza);
            if (balanza != null) {
                balanza.setBandaCero(numBza,bandaCeroi);
            }
        }

        @Override
        public float getBandaCeroValue(int numBza) {
            Struct balanza = balanzas.get(numBza);
            if (balanza != null) {
                return balanza.getBandaCeroValue(numBza);
            }
            return 0;
        }

        @Override
        public void setBandaCeroValue(int numBza, float bandaCeroValue) {
            Struct balanza = balanzas.get(numBza);
            if (balanza != null) {
                balanza.setBandaCeroValue(numBza,bandaCeroValue);
            }
        }

        @Override
        public Boolean getEstable(int numBza) {
            Struct balanza = balanzas.get(numBza);
            if (balanza != null) {
                return balanza.getEstable(numBza);
            }
            return false;
        }

        @Override
        public String format(int numero, String peso) {
            Struct balanza = balanzas.get(numero);
            if (balanza != null) {
                return balanza.format(numero,peso);
            }
            return "null";
        }

        @Override
        public String getUnidad(int numBza) {
            try {
                Struct balanza = balanzas.get(numBza);
                if (balanza != null) {
                    return balanza.getUnidad(numBza);
                }

            } catch (IllegalArgumentException e) {
                //mainActivity.Mensaje("Error:"+e.getMessage(), R.layout.item_customtoasterror);
            }
            return "error";

        }

        @Override
        public String getPicoStr(int numBza) {
            Struct balanza = balanzas.get(numBza);
            if (balanza != null) {
                return balanza.getPicoStr(numBza);
            }
        return "null";
        }

        @Override
        public float getPico(int numBza) {
            Struct balanza = balanzas.get(numBza);
            if (balanza != null) {
                return balanza.getPico(numBza);
            }
            return -1;
        }
        @Override
        public void init(int numBza) {
            Struct balanza = balanzas.get(numBza);
            if (balanza != null) {
                balanza.init(numBza);
            }
        }
        @Override
        public void escribir(String msj, int numBza) {
            balanzas.get(numBza).escribir(msj, numBza);
        }
        @Override
        public void stop(int numBza) {
        }
        @Override
        public void start(int numBza) {
            Struct balanza = balanzas.get(numBza);
            if (balanza != null) {
                balanza.start(numBza);
            }
        }
        @Override
        public Boolean calibracionHabilitada(int numBza) {
            Struct balanza = balanzas.get(numBza);
            if (balanza != null) {
                balanza.calibracionHabilitada(numBza);
            }
            return false;
        }
        @Override
        public void openCalibracion(int numBza) {
            Struct balanza = balanzas.get(numBza);
            if (balanza != null) {
                balanza.openCalibracion(numBza);
            }
        }
        @Override
        public Boolean getSobrecarga(int numBza) {
            Struct balanza = balanzas.get(numBza);
            if (balanza != null) {
                balanza.getSobrecarga(numBza);
            }
            return false;
        }
        @Override
        public Boolean getEstadoCentroCero(int numBza) {
            return false;
        }
        @Override
        public Boolean getEstadoSobrecarga(int numBza) {
            return false;
        }
        @Override
        public Boolean getEstadoNeto(int numBza) {
            return false;
        }
        @Override
        public Boolean getEstadoPesoNeg(int numBza) {
            return false;
        }
        @Override
        public Boolean getEstadoBajoCero(int numBza) {
            return false;
        }
        @Override
        public Boolean getEstadoBzaEnCero(int numBza) {
            return false;
        }
        @Override
        public Boolean getEstadoBajaBateria(int numBza) {
            return false;
        }
        @Override
        public String getFiltro1(int numBza) {
            return "null";
        }
        @Override
        public String getFiltro2(int numBza) {
            return "null";
        }
        @Override
        public String getFiltro3(int numBza) {
            return "null";
        }
        @Override
        public String getFiltro4(int numBza) {
            return "null";
        }
        @Override
        public Boolean getEstadoEstable(int numBza) {
            return false;
        }
        @Override
        public void onEvent() {
        }
        @Override
        public Boolean Itw410FrmSetear(int numero, String setPoint, int Salida) {
            Boolean a=false;
            Struct balanza = balanzas.get(numero);
            if(balanza!=null) {
              a =  balanza.Itw410FrmSetear(numero,setPoint,Salida);
            }
            return a;
        }
        @Override
        public String Itw410FrmGetSetPoint(int numero) { // Creas el CountDownLatch con un contador de 1
            String a = "null";
            Struct balanza = balanzas.get(numero);
            if(balanza!=null) {
                a = balanza.Itw410FrmGetSetPoint(numero);
            }

            return a;
        }
        @Override
        public int Itw410FrmGetSalida(int numero) {
            int a = -1;
            Struct balanza = balanzas.get(numero);
            if(balanza!=null) {
                a = balanza.Itw410FrmGetSalida(numero);
            }
            return a;
        }
        @Override
        public void Itw410FrmStart(int numero)
        {
            Struct balanza = balanzas.get(numero);
            if(balanza!=null) {
                balanza.Itw410FrmStart(numero);
            }

        }
        @Override
        public int Itw410FrmGetEstado(int numero) {
            int a=-1;
            Struct balanza = balanzas.get(numero);
            if(balanza!=null) {
               a= balanza.Itw410FrmGetEstado(numero);
            }

            return a;
        }
        @Override
        public String Itw410FrmGetUltimoPeso(int numero) {
            String a="null";
            Struct balanza = balanzas.get(numero);
            if(balanza!=null) {
                a= balanza.Itw410FrmGetUltimoPeso(numero);
            }

            return a;
        }
        @Override
        public int Itw410FrmGetUltimoIndice(int numero) {
            int a=-1;
            Struct balanza = balanzas.get(numero);
            if(balanza!=null) {
                a= balanza.Itw410FrmGetUltimoIndice(numero);
            }
          return a;
        }
        @Override
        public void itw410FrmPause(int numero) {
            Struct balanza = balanzas.get(numero);
            if(balanza!=null) {
                balanza.itw410FrmPause(numero);
            }

        }
        @Override
        public void itw410FrmStop(int numero) {
            Struct balanza = balanzas.get(numero);
            if(balanza!=null) {
                balanza.itw410FrmStop(numero);
            }
        }

      /*  public  void setConfigModbus(String Salida,int Slave,int Baud,int Datab,int Stopb,int Parity,int numBza){
            SharedPreferences Preferencias = activity.getSharedPreferences("modbus", Context.MODE_PRIVATE);
            SharedPreferences.Editor ObjEditor = Preferencias.edit();

            ObjEditor.putString("port_"+numBza,Salida);
            ObjEditor.putInt("slave_"+numBza,Slave);
            ObjEditor.putInt("baud_"+numBza,Baud);
            ObjEditor.putInt("data_"+numBza, Datab);
            ObjEditor.putInt("stop_"+numBza,Stopb);
            ObjEditor.putInt("parity_"+numBza,Parity);
            ObjEditor.commit();
        }
        public ArrayList<modbus> getConfigModbus() {
            int numModbus = 0;
            ArrayList<modbus> modlist=new ArrayList<>();
            String port="";
            while(port!="fin"){
                modlist.add(new modbus());
                SharedPreferences Preferencias = activity.getSharedPreferences("modbus", Context.MODE_PRIVATE);
                int Slave= (Preferencias.getInt("Slave_" + numModbus, 0));

                int baud= (Preferencias.getInt("baud_" + numModbus, 0));
                int stopB= (Preferencias.getInt("stop_" + numModbus, 0));
                int dataB= (Preferencias.getInt("data_" + numModbus, 0));
                int parityB= (Preferencias.getInt("parity_" + numModbus, 0));
                port= (Preferencias.getString("port_" + numModbus, "fin"));
                switch (port){
                    case "PUERTO SERIE 1":{
                        modlist.get(numModbus).setPortselected(1);
                        break;
                    }
                    case "PUERTO SERIE 2":{
                        modlist.get(numModbus).setPortselected(2);
                        break;
                    }
                    case "PUERTO SERIE 3":{
                        modlist.get(numModbus).setPortselected(3);
                        break;
                    }
                    case"TCP/IP": {
                        modlist.get(numModbus).setPortselected(4);
                        break;
                    }
                    case "fin":{
                        break;
                    }
                }


                    modlist.get(numModbus).setBaud(baud);
                    modlist.get(numModbus).setDatabit(dataB);
                    modlist.get(numModbus).setParity(parityB);
                    modlist.get(numModbus).setStopbit(stopB);
                    modlist.get(numModbus).setSlave(Slave);
                    numModbus++;
                    System.out.println("modbus:"+numModbus+":Sl"+Slave+"ba"+baud+"da"+dataB+"par"+parityB+"st"+stopB);
            }
            return modlist;
        }*/
    }
   /* public void initializateModbus(){
        ModbusReqRtuMaster ridin= null; // DialogoInformacion()
        String a="";

        ArrayList<ArrayList<String>> modbuslistconfig=getConfigModbus();
        try {
            for (ArrayList<String> modbus : modbuslistconfig) {
                if (modbus.get(0) != "fin") {
                    int baud = Integer.parseInt(modbus.get(1));
                    int data = Integer.parseInt(modbus.get(2));
                    int stop = Integer.parseInt(modbus.get(3));
                    int parity = Integer.parseInt(modbus.get(4));
                    ModbusList.add(new ModbusMasterRtu());
                    ModbusList.get(modbuslistconfig.indexOf(modbus)).init(modbus.get(0), baud, data, stop, parity);

                }
            }
        } catch(Exception e) {

        }
        ;
        try {
            ridin = new ModbusMasterRtu().init("/dev/ttyXRUSB0",9600, 8, 1, 0);
            OnRequestBack<String> callback = new OnRequestBack<String>() {

                @Override
                public void onSuccess(String s) {
                    System.out.println("SLAVE MODBUS WOWOWOWO: "+ s);

                }

                @Override
                public void onFailed(String errorMessage) {
                    System.err.println("SLAVE MODBUS WOWOWOWO err: " + errorMessage);
                }
            };
            OnRequestBack<boolean[]> callbackbool = new OnRequestBack<boolean[]>() {
                @Override
                public void onSuccess(boolean[] s) {
                    System.out.println("SLAVE MODBUS WOWOWOWO: "+ s);

                }

                @Override
                public void onFailed(String errorMessage) {
                    System.err.println("SLAVE MODBUS WOWOWOWO err: " + errorMessage);
                }
            };
            ridin.writeCoil(callback,1,2,false);
            Thread.sleep(100);
            ridin.readCoil(callbackbool,1,2,1);
            ridin.writeRegister(callback,1,2,10);
             }catch (Exception e){

        }
    }*/
// actualizacion :
    /**
     *  INCOMPLETO
     * @param x INCOMPLETO
     */
    public static void addDevice(serviceDevice x,boolean replacebool){
       // ArrayList<serviceDevice> List = get_arrayServiceDevices();

        int balanzalenght= x.getNB(); //pos;//List.size();

        int count=0;

     /*ArrayList<String> direccion= x.getDireccion();
        for (serviceDevice y :List) {
            if(y.getModelo()==x.getModelo()&&count==0){
                    direccion=y.getDireccion();
                    count++;
            }

        }*/

        System.out.println("NUMBER BZA"+x.getNB());
        String Tipo="";
        switch (x.getTipo()) {
            case -1: {
                Tipo="Borrado";
                break;
            }
            case 0: {
                Tipo="Balanza";
                break;
            }
            case 1:{
                Tipo="Impresora";
             break;
            }
            case 2:{
                Tipo="I/O";


                break;
            }
            case 3:{
                Tipo="Escaner";
                break;
            }
            case 4:{
                Tipo="Dispositivo";
                break;
            }
            default:{
                break;
            }
        }

        String Modelo="";
        switch (x.getModelo()){
            case 0:{
                Modelo="Optima";
                break;
            }
            case 1:{
                Modelo="Minima";
                break;
            }
            case 2:{
                Modelo="R31P30";
                break;
            }
            case 3:{
                Modelo= "ITW410";
                break;
            }
            default:{
               break;
            }
        }
        System.out.println("NB set device "+ balanzalenght);
        SharedPreferences Preferencias=Service.activity.getSharedPreferences("devicesService",Context.MODE_PRIVATE);
        SharedPreferences.Editor ObjEditor=Preferencias.edit();
        ObjEditor.putBoolean(String.valueOf("seteo_"+balanzalenght), x.getSeteo());
        if(Tipo.contains("Borrado")){
            ObjEditor.putString(String.valueOf("Baud_"+balanzalenght), x.getDireccion().get(0));
            ObjEditor.putString(String.valueOf("DataB_"+balanzalenght), x.getDireccion().get(1));
            ObjEditor.putString(String.valueOf("StopB_"+balanzalenght), x.getDireccion().get(2));
            ObjEditor.putString(String.valueOf("Parity_"+balanzalenght), x.getDireccion().get(3));
            ObjEditor.putString(String.valueOf("Direccion_"+balanzalenght), x.getDireccion().get(0));
        }
        if(x.getSalida().contains("PuertoSerie")){
            ObjEditor.putString(String.valueOf("Baud_"+balanzalenght), x.getDireccion().get(0));
            ObjEditor.putString(String.valueOf("DataB_"+balanzalenght), x.getDireccion().get(1));
            ObjEditor.putString(String.valueOf("StopB_"+balanzalenght), x.getDireccion().get(2));
            ObjEditor.putString(String.valueOf("Parity_"+balanzalenght), x.getDireccion().get(3));
        }else if(x.getSalida().contains("IP")||x.getSalida().contains("MAC")){
            ObjEditor.putString(String.valueOf("Direccion_"+balanzalenght), x.getDireccion().get(0));
        }else if (x.getSalida().contains("I/O")){


        } else if (x.getSalida().contains("USB")) {}
        ObjEditor.putString(String.valueOf("Tipo_"+balanzalenght), Tipo);
        ObjEditor.putString(String.valueOf("Salida_"+balanzalenght),x.getSalida());
        ObjEditor.putString(String.valueOf("Modelo_"+balanzalenght),Modelo);
        ObjEditor.putInt(String.valueOf("ID_"+balanzalenght),x.getID());

        ObjEditor.apply();

           /*if(numBza==-1){
               switch (Modelo){
                   case "ITW410":{
                       List.add(0);
                   }
               }
           }else{

               switch (Modelo){
                   case "ITW410":{
                      try{
                          List.set(numBza,0);
                      }catch (Exception e){
                          List.add(numBza,0);
                      }
                   }
               }
           }
            setBalanzas(List);*/
    }
    public static ArrayList<serviceDevice> get_arrayServiceDevices(){
        String tipo="";
        int num=0;
        int numbza=0;
        SharedPreferences Preferencias=activity.getSharedPreferences("devicesService",Context.MODE_PRIVATE);
        ArrayList<serviceDevice> Arraydevicesaux= new ArrayList<serviceDevice>();
       while(tipo!="fin"){
           serviceDevice x=new serviceDevice();
            tipo= Preferencias.getString("Tipo_"+num,"fin");
            if(tipo.equals("fin")&&num==0){
                tipo="Balanza";
            }
            String salida ="";
            String Modelobza="";
            String baud="";
            String dataB="";
            String stopB="";
            String parity="";
            String direccion="";
            Boolean seteo=false;
           int NumeroID=0;
           salida= Preferencias.getString("Salida_"+num,"PuertoSerie 1");
           seteo= Preferencias.getBoolean("seteo_"+num,false);
           NumeroID=Preferencias.getInt("ID_"+num,1);
           switch (tipo){
                case "Balanza":{

                    x.setNB(numbza);
                    numbza++;
                    x.setTipo(0);
                    Modelobza = Preferencias.getString("Modelo_"+num,"Optima");

                     switch (Modelobza){
                        case "Optima":{
                            x.setModelo(0);
                            break;
                        }
                        case "Minima":{
                            x.setModelo(1);
                            break;
                        }
                        case "R31P30":{
                            x.setModelo(2);
                            break;
                        }
                        case "ITW410":{
                            x.setModelo(3);
                            break;
                        }
                        default:{
                            x.setModelo(-1);
                        }
                    }
                    break;
                }
                case "Impresora":{

                    break;
                }
                case "I/O":{

                    break;
                }
                case "Escaner":{

                    break;
                }
                case "Dispositivo":{

                    break;
                }
            }
            ArrayList<String> listaux=new ArrayList<String>();
           x.setSalida(salida);  System.out.println("salida"+salida);

           if(salida.contains("PuertoSerie")){

                baud=Preferencias.getString("Baud_"+num,"9600");
                dataB=Preferencias.getString("DataB_"+num,"8");
                stopB=Preferencias.getString("StopB_"+num,"1");
                parity=Preferencias.getString("Parity_"+num,"0");

                listaux.add(baud);
                listaux.add(dataB);
                listaux.add(stopB);
                listaux.add(parity);
            } else if(salida.contains("IP")||salida.contains("MAC")){
                direccion=Preferencias.getString("Direccion_"+num,"null");
                listaux.add(direccion);
            }else if (salida.contains("I/O")){


            } else if (salida.contains("USB")) {}
            x.setDireccion(listaux);
            x.setID(NumeroID);
            x.setSeteo(seteo);
            if(tipo!="fin"){
                Arraydevicesaux.add(x);
            }
           num++;
        }
       ;
         return Arraydevicesaux;
    }
}
