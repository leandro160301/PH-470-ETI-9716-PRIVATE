package com.service.Balanzas;

import static com.service.Utils.Mensaje;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.service.Balanzas.Clases.ITW410;
import com.service.Balanzas.Clases.MINIMA_I;
import com.service.Balanzas.Fragments.ServiceFragment;
import com.service.Balanzas.Interfaz.Balanza;
import com.service.Balanzas.Clases.OPTIMA_I;
import com.service.Balanzas.Interfaz.modbus;
import com.service.Comunicacion.OnFragmentChangeListener;
import com.service.Balanzas.Clases.R31P30_I;
import com.service.Impresora.ImprimirEstandar;
import com.service.Modbus.ModbusMasterRtu;
import com.service.Modbus.Req.ModbusReqRtuMaster;
import com.service.PuertosSerie.PuertosSerie2;
import com.service.R;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android_serialport_api.SerialPort;

public class BalanzaService implements Serializable {
    public ArrayList<ModbusMasterRtu> ModbusList;
    private PuertosSerie2 serialPortA;
    private PuertosSerie2 serialPortB;
    ServiceFragment serviceFragment= new ServiceFragment();

    private PuertosSerie2 serialPortC;
    public static Impresoras Impresoras;
    public static Balanzas Balanzas;
    public static final String PUERTO_A="/dev/ttyXRUSB0";
    public static final String PUERTO_B="/dev/ttyXRUSB1";
    public static final String PUERTO_C="/dev/ttyXRUSB2";
    public PuertosSerie2 A,B,C;
    private String NAME="SERVICE";
    BalanzaService Service;
    int baudA=9600,baudB=9600,baudC=9600,stopBitA=1,stopBitB=1,stopBitC=1,dataBitsA=8,dataBitsB=8,dataBitsC=8,parityA=0,parityB=0,parityC=0;
    public AppCompatActivity activity;
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
    public void openServiceFragment(){
        ServiceFragment fragment = ServiceFragment.newInstance(Service);
        Bundle args = new Bundle();
        args.putSerializable("instanceService", Service);
        fragmentChangeListener.openFragmentService(fragment,args);
        /*37:15.432  9825-14911 AndroidRuntime          com.jws.jwsapi                       E  FATAL EXCEPTION: Thread-17 (Ask Gemini)
                                                                                                    Process: com.jws.jwsapi, PID: 9825
                                                                                                    java.lang.RuntimeException: Can't create handler inside thread Thread[Thread-17,5,main] that has not called Looper.prepare()
                                                                                                    	at android.os.Handler.<init>(Handler.java:205)
                                                                                                    	at android.os.Handler.<init>(Handler.java:118)
                                                                                                    	at com.jws.jwsapi.feature.formulador.MainFormClass.openFragmentService(MainFormClass.java:106)
                                                                                                    	at com.service.Balanzas.BalanzaService.openServiceFragment(BalanzaService.java:100)
                                                                                                    	at com.service.Balanzas.Clases.ITW410.salir_cal(ITW410.java:1086)
                                                                                                    	at com.service.Balanzas.Fragments.CalibracionItw410Fragment$3.run(CalibracionItw410Fragment.java:253)
*/
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

        public void initializateBalanza(){

            ArrayList<Integer> balanzasList = getBalanzas();
            if(balanzasList.size()==0){
                balanzasList.add(0);
                setBalanzas(balanzasList);
            }
            PuertosSerie2 serialPort=serialPortA;
            for (int i = 0; i< balanzasList.size(); i++) {
                if(i==1){
                    serialPort=serialPortB;
                }
                if(i==2){
                    serialPort=serialPortC;
                }

                if (balanzasList.get(i) == 1) { // 0
                    System.out.println("OPTIMA");
                    Struct bza=new OPTIMA_I(serialPort, i+1,activity,Service,fragmentChangeListener);

                    bza.init(i+1);
                    balanzas.put(i + 1, bza);
                }

                if (balanzasList.get(i) == 1) {

                    System.out.println("MINIMA");
                    Struct BZA =new MINIMA_I(serialPort, i+1,activity,Service,fragmentChangeListener);
                    BZA.init(i+1);
                    balanzas.put(i + 1, BZA);
                }
                if (balanzasList.get(i) == 2) {

                    System.out.println("R31p30");
                    Struct BZA =new R31P30_I(serialPort, i+1,activity,fragmentChangeListener);
                    BZA.init(i+1);
                    balanzas.put(i + 1, BZA);
                }
                if(balanzasList.get(i)==0){ // 3
                    System.out.println("ITW410 STARTY");
                    //hardcodeadisimo
                    int finalI2 = i;
                   // pasar adentro  el modbusconfig finalPort,modbus.getBaud(),modbus.getDatabit(),modbus.getStopbit(),modbus.getParity()
                    ITW410 bza1 = new ITW410( balanzas.size(), Service, fragmentChangeListener, 1);
                    bza1.init(finalI2+1);
                    balanzas.put(finalI2+1,bza1);
                    System.out.println("position bza"+finalI2+1);
                    //hardcodeadisimo fin
                    ArrayList<modbus> modbuslistconfig=getConfigModbus();
                    try {
                        for (modbus modbus : modbuslistconfig) {
                            if (modbus.getPortselected()!=-1) { // 20 es error
                             //   ModbusMasterRtu modbusmasterinit= new ModbusMasterRtu();
                                String port = "";
                                if(modbus.getPortselected()!=4) { // 4 seria "RED" o TCP
                                    //MODBUS RTU
                                    if (modbus.getPortselected() == 1) {
                                        port = PUERTO_A;
                                    }
                                    if (modbus.getPortselected() == 2) {
                                        port = PUERTO_B;
                                    }

                                    if (modbus.getPortselected() == 3) {
                                        port = PUERTO_C;
                                    }
                                    String finalPort = port;
                                    int finalI1 = i;
                                    new Thread(new Runnable() {
                                        @Override
                                        public void run() {
                                            ModbusReqRtuMaster Modbus = null;
                                            try {
                                               // Modbus = modbusmasterinit.init(finalPort,modbus.getBaud(),modbus.getDatabit(),modbus.getStopbit(),modbus.getParity());
                                              //  Modbus =modbusmasterinit.init(PUERTO_A,115200,8,1,0);

                                                if (Modbus != null) {
                                               //     ITW410 bza1 = new ITW410(Modbus, balanzas.size(), activity, fragmentChangeListener, 1);
                                                 //   bza1.init(finalI1 + 1);
                                                  //  balanzas.put(finalI1 + 1, bza1);
                                                }
                                            } catch (Exception e) {
                                                throw new RuntimeException(e);
                                            }
                                        }
                                    });
                                    int finalI = i;
                                    new Thread(new Runnable() {
                                        @Override
                                        public void run() {
                                            ModbusReqRtuMaster Modbus = null;
                                            try {

                                            /*    modbusmasterinit.init(finalPort,modbus.getBaud(),modbus.getDatabit(),modbus.getStopbit(),modbus.getParity());

                                                if (Modbus != null) {
                                                    ITW410 bza2 = new ITW410(Modbus, balanzas.size(), activity, fragmentChangeListener, 2);
                                                    bza2.init(finalI + 1);
                                                    balanzas.put(finalI + 1, bza2);
                                                }*/   } catch (Exception e) {
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
                        System.out.println("ERROR CONCHUMADRE"+e);
                    }
                }

            }
        }

        public void addBalanza(String Modelo,int numBza){
           ArrayList<Integer> List = getBalanzas();
           if(numBza==-1){
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
            setBalanzas(List);
        }
        public ArrayList<Integer> getBalanzas() {
            SharedPreferences Preferencias = activity.getSharedPreferences(NAME, Context.MODE_PRIVATE);
            Gson gson = new Gson();
            String json = Preferencias.getString("ModeloBalanza", null);
            Type type = new TypeToken<List<Integer>>() {}.getType();
            ArrayList<Integer> balanzas = gson.fromJson(json, type);
            return balanzas != null ? balanzas : new ArrayList<>();
        }

        public void setBalanzas(List<Integer> Balanza) {
            SharedPreferences Preferencias = activity.getSharedPreferences(NAME, Context.MODE_PRIVATE);
            SharedPreferences.Editor ObjEditor = Preferencias.edit();
            Gson gson = new Gson();
            String json = gson.toJson(Balanza);
            ObjEditor.putString("ModeloBalanza", json);
            ObjEditor.apply();
        }


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
            balanzas.get(numero).Itw410FrmSetTiempoEstabilizacion(numero,Tiempo);
        }

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
            throw new IllegalArgumentException("Balanza no encontrada");
        }

        @Override
        public String format(int numero, String peso) {
            Struct balanza = balanzas.get(numero);
            if (balanza != null) {
                return balanza.format(numero,peso);
            }
            throw new IllegalArgumentException("Balanza no encontrada");
        }

        @Override
        public String getUnidad(int numBza) {
            try {
                Struct balanza = balanzas.get(numBza);
                if (balanza != null) {
                    return balanza.getUnidad(numBza);
                }
                throw new IllegalArgumentException("Balanza no encontrada");
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
            throw new IllegalArgumentException("Balanza no encontrada");
        }

        @Override
        public float getPico(int numBza) {
            Struct balanza = balanzas.get(numBza);
            if (balanza != null) {
                return balanza.getPico(numBza);
            }
            throw new IllegalArgumentException("Balanza no encontrada");
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
            return null;
        }

        @Override
        public Boolean getEstadoSobrecarga(int numBza) {
            return null;
        }

        @Override
        public Boolean getEstadoNeto(int numBza) {
            return null;
        }

        @Override
        public Boolean getEstadoPesoNeg(int numBza) {
            return null;
        }

        @Override
        public Boolean getEstadoBajoCero(int numBza) {
            return null;
        }

        @Override
        public Boolean getEstadoBzaEnCero(int numBza) {
            return null;
        }

        @Override
        public Boolean getEstadoBajaBateria(int numBza) {
            return null;
        }

        @Override
        public String getFiltro1(int numBza) {
            return "";
        }

        @Override
        public String getFiltro2(int numBza) {
            return "";
        }

        @Override
        public String getFiltro3(int numBza) {
            return "";
        }

        @Override
        public String getFiltro4(int numBza) {
            return "";
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

            System.out.println(numero);
             return balanzas.get(numero).Itw410FrmSetear(numero,setPoint,Salida);

        }

        @Override
        public String Itw410FrmGetSetPoint(int numero) { // Creas el CountDownLatch con un contador de 1
            String a = balanzas.get(numero).Itw410FrmGetSetPoint(numero);
            return a;
        }

        @Override
        public int Itw410FrmGetSalida(int numero) {
            int a = balanzas.get(numero).Itw410FrmGetSalida(numero);

            return a;
        }

        @Override
        public void Itw410FrmStart(int numero) {
             balanzas.get(numero).Itw410FrmStart(numero);
        }

        @Override
        public int Itw410FrmGetEstado(int numero) {
            int a = balanzas.get(numero).Itw410FrmGetEstado(numero);
            return a;
        }

        @Override
        public String Itw410FrmGetUltimoPeso(int numero) {
            String a = balanzas.get(numero).Itw410FrmGetUltimoPeso(numero);
            return a;
        }

        @Override
        public int Itw410FrmGetUltimoIndice(int numero) {

            int a = balanzas.get(numero).Itw410FrmGetUltimoIndice(numero);
            return a;
        }

        @Override
        public void itw410FrmPause(int numero) {
             balanzas.get(numero).itw410FrmPause(numero);

        }

        @Override
        public void itw410FrmStop(int numero) {

            balanzas.get(numero).itw410FrmStop(numero);
        }

        public  void setConfigModbus(String Salida,int Slave,int Baud,int Datab,int Stopb,int Parity,int numBza){
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
        }
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

}
