package com.service.Balanzas;

import static com.service.Utils.Mensaje;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;

import androidx.appcompat.app.AppCompatActivity;

import com.service.Balanzas.Clases.ITW410;
import com.service.Balanzas.Clases.MINIMA_I;
import com.service.Balanzas.Fragments.ServiceFragment;
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
import com.zgkxzx.modbus4And.requset.ModbusReq;

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
    ServiceFragment ServiceFragment= new ServiceFragment();

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
        CountDownLatch latch = new CountDownLatch(1);
         A = new PuertosSerie2();
        SerialPort AS=A.open(PUERTO_A,baudrate_A,stopbit_A,databits_A,parity_A,flowcon_A,flags_A);
         B= new PuertosSerie2();
        SerialPort BS=B.open(PUERTO_B,baudrate_B,stopbit_B,databits_B,parity_B,flowcon_B,flags_B);
         C= new PuertosSerie2();
        SerialPort CS=C.open(PUERTO_C,baudrate_C,stopbit_C,databits_C,parity_C,flowcon_C,flags_C);
        Boolean res=false;
        if (AS!=null&&BS!=null&&CS!=null){
            serialPortA=A;
            serialPortB=B;
            serialPortC=C;
            latch.countDown();
            res= false;
        }else{
            Mensaje("ERROR_PUERTOS",R.layout.item_customtoasterror,activity);
            res = true;
        }
        try {
            latch.await(1000,TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return res;
    }

    public void init() {
    Utils.clearCache(activity.getApplicationContext());

        System.out.println("SYSTEMOUTADOWN");
        SettingsDef();
         Impresoras  = new Impresoras();
         Balanzas = new Balanzas();
        ArrayList<serviceDevice> balanzasList =Balanzas.get_balanzalistglob();
        System.out.println("SIZE DEBUG AAAAAA"+balanzasList.size());

        if(balanzasList.size()>=1 && balanzasList.get(0).getSeteo()){

            Balanzas.initializateBalanza(balanzasList);
        }else{
            initializateSerialPort(baudA,stopBitA,dataBitsA,parityA,0,0,baudB,stopBitB,dataBitsB,parityB,0,0,baudC,stopBitC,dataBitsC,parityC,0,0);
        }

    }
    public void openServiceFragment2(){
        ServiceFragment fragment = ServiceFragment.newInstance(Service);
        Bundle args = new Bundle();
        args.putSerializable("instanceService", Service);
        fragmentChangeListener.openFragmentService( fragment,args);
    }
    private void SettingsDef(){
        String tipo="";
        int num=0;int numbza=0;
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
                    if(!seteo && tipo.equals("Balanza")){
                        ArrayList<String> arrayList=new ArrayList<>();
                        arrayList.add(OPTIMA_I.Bauddef);arrayList.add(OPTIMA_I.DataBdef);arrayList.add(OPTIMA_I.StopBdef);arrayList.add(OPTIMA_I.Paritydef);
                        x.setTipo(0);
                        x.setNB(0);
                        x.setDireccion(arrayList);
                        x.setSeteo(true);
                        x.setNumborrados(0);
                        x.setSalida("PuertoSerie 1");
                        x.setID(0);
                        System.out.println("SETTING DEFAULT ");
                        addDevice(x);
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
            return null;
        }
        @Override
        public void setEstado(int numBza, String estadoBZA) {
            Struct balanza = balanzas.get(numBza);
            if (balanza != null) {
                balanza.setEstado(numBza,estadoBZA);
            }
        }
        private int get_numeroSalidasBZA(){
            SharedPreferences Preferencias = activity.getSharedPreferences("devicesService", Context.MODE_PRIVATE);
            String tipo="";
            int num=0;
            int numeroSalidas=0;
            boolean[] array = new boolean[6];
            while(tipo!="fin") {
                tipo = Preferencias.getString("Tipo_" + num, "fin");
                if(tipo.contains("fin") && num==0){
                    tipo="Balanza";
                }

                if(tipo.equals("Balanza")) {
                    String Salida = Preferencias.getString("Salida_" + num, "PuertoSerie 1");
                    switch (Salida) {
                        case "PuertoSerie 1": {
                            if(!array[0]){
                                array[0]=true;
                                numeroSalidas++;
                            }
                            break;
                        }
                        case "PuertoSerie 2": {
                            if(!array[1]){

                                array[1]=true;
                                numeroSalidas++;
                            }
                            break;
                        }
                        case "PuertoSerie 3": {
                            if(!array[2]){

                                array[2]=true;
                                numeroSalidas++;
                            }
                            break;
                        }
                        case "USB": {
                            if(!array[3]){

                                array[3]=true;
                                numeroSalidas++;
                            }
                            break;
                        }
                        case "TCP/IP": {
                            if(!array[4]){

                                array[4]=true;
                                numeroSalidas++;
                            }
                            break;
                        }
                        case "MAC": {
                            if(!array[5]){
                                array[5]=true;
                                numeroSalidas++;
                            }
                            break;
                        }
                    }
                    num++;
                }
            }

            return numeroSalidas;
        }
        public PuertosSerie2 initPuertoSerie(String Puerto,int baudrate,int databits,int stopbit,int parity,int flowcon,int flags){
               PuertosSerie2 serialPort= new PuertosSerie2();
                SerialPort Puerto_A_OPEN=serialPort.open(Puerto,baudrate,stopbit,databits,parity,flowcon,flags);
            if(Puerto_A_OPEN!=null){
                System.out.println( Puerto+" INICIALIZADO PUERTO WOWOWO");
            }
                return serialPort;
        }
        public void initializateBalanza(ArrayList<serviceDevice> balanzasList){
            int numeroSalidas= get_numeroSalidasBZA();
            CountDownLatch latch=new CountDownLatch(balanzasList.size());
            for (int i = 0; i< balanzasList.size(); i++) {
                String puerto = "";
                PuertosSerie2 port = null;
                switch (balanzasList.get(i).getSalida()) {
                    case "PuertoSerie 1": {
                        puerto = PUERTO_A;
                        serialPortA = initPuertoSerie(puerto, Integer.parseInt(balanzasList.get(i).getDireccion().get(0)), Integer.parseInt(balanzasList.get(i).getDireccion().get(1)), Integer.parseInt(balanzasList.get(i).getDireccion().get(2)), Integer.parseInt(balanzasList.get(i).getDireccion().get(3)), 0, 0);
                        port=serialPortA;

                        break;
                    }
                    case "PuertoSerie 2": {
                        puerto = PUERTO_B;
                        serialPortB = initPuertoSerie(puerto, Integer.parseInt(balanzasList.get(i).getDireccion().get(0)), Integer.parseInt(balanzasList.get(i).getDireccion().get(1)), Integer.parseInt(balanzasList.get(i).getDireccion().get(2)), Integer.parseInt(balanzasList.get(i).getDireccion().get(3)), 0, 0);
                        port=serialPortB;
                        break;
                    }
                    case "PuertoSerie 3": {
                        puerto = PUERTO_C;
                        serialPortC = initPuertoSerie(puerto, Integer.parseInt(balanzasList.get(i).getDireccion().get(0)), Integer.parseInt(balanzasList.get(i).getDireccion().get(1)), Integer.parseInt(balanzasList.get(i).getDireccion().get(2)), Integer.parseInt(balanzasList.get(i).getDireccion().get(3)), 0, 0);
                        port=serialPortC;
                        break;
                    }
                    case "USB": {

                        break;
                    }
                    case "TCP/IP": {

                        break;
                    }
                    case "MAC": {

                        break;
                    }
                }
                if (balanzasList.get(i).getModelo() == 0) { // 0
                    System.out.println("OPTIMA");
                    if(port!=null) {
                        Struct bza = new OPTIMA_I(port, balanzasList.get(i).getID(), activity, Service, fragmentChangeListener);
                        bza.init(i + 1);
                        balanzas.put(i + 1, bza);
                    }
                        latch.countDown();

                }
                if (balanzasList.get(i).getModelo() == 1) {
                    System.out.println("MINIMA");
                    if(port!=null){
                        Struct BZA = new MINIMA_I(port, balanzasList.get(i).getID(), activity, Service, fragmentChangeListener);
                        BZA.init(i + 1);
                        balanzas.put(i + 1, BZA);
                    }
                    latch.countDown();
                }
                if (balanzasList.get(i).getModelo() == 2) {
                    System.out.println("R31P30");
                    if(port!=null){
                        Struct BZA = new R31P30_I(port, balanzasList.get(i).getID(), activity, fragmentChangeListener);
                        BZA.init(i + 1);
                        balanzas.put(i + 1, BZA);
                    }
                    latch.countDown();
                }
                if (balanzasList.get(i).getModelo() == 3) { // 3
                    System.out.println("ITW410");
                    int finalI2 = i;
                    try {
                        String finalPort = puerto;
                        int finalI1 = i;
                        int finalI3 = i;
                        try {
                            ModbusReqRtuMaster Modbus = initializatemodbus(puerto, Integer.parseInt(balanzasList.get(i).getDireccion().get(0)), Integer.parseInt(balanzasList.get(i).getDireccion().get(1)), Integer.parseInt(balanzasList.get(i).getDireccion().get(2)), Integer.parseInt(balanzasList.get(i).getDireccion().get(3)));
                            ITW410 bza1 = new ITW410(finalI2 + 1, Service, fragmentChangeListener, 1, Modbus);
                            bza1.init(finalI2 + 1);
                            balanzas.put(finalI2 + 1, bza1);
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                        // FALTA BZA 2 ,PODRIA SER CON UNA VARIABLE CONTADOR QUE VAYA SUMANDOSE . Y LUEGO QUE EN INIT Y PUT SEA por ejm: bza1.init(finalI2+CONTADOR+ 1);   balanzas.put(finalI2 +CONTADOR+ 1, bza1); EMPIEZA EN 0 Y VA SUMANDOSE A MEDIDA QUE HAY DOBLES BALANZAS
                    } catch (Exception e) {
                    }
                    latch.countDown();
                    try {
                        latch.await(numeroSalidas * 2000, TimeUnit.MILLISECONDS);
                    } catch (Exception e) {

                    }
                }
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

                    System.out.println("DEBUG "+tipo+ Modelobza+ num);
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
        public ModbusReqRtuMaster initializatemodbus(String Port,int Baud,int databit,int Stopbit,int Parity){
            CountDownLatch latch = new CountDownLatch(1);
            ModbusReqRtuMaster Modbus = null;
            try {
                ModbusMasterRtu modbusMasterRtu= new ModbusMasterRtu();
                Modbus= modbusMasterRtu.init(Port,Baud,databit,Stopbit,Parity);
            } catch (Exception e) {
                throw new RuntimeException(e);
            } finally {
                System.out.println("SETEADO MODBUS");
                latch.countDown();
            }
            try {
                latch.await(2000,TimeUnit.MILLISECONDS);
            } catch (InterruptedException e) {
              e.getMessage();
            }
            return Modbus;
        }
        public ArrayList<serviceDevice> get_balanzalistglob(){

            ArrayList<serviceDevice> ArrayBalanzas = new ArrayList<serviceDevice>();
            SharedPreferences Preferencias = activity.getSharedPreferences("devicesService", Context.MODE_PRIVATE);
            String tipo="";
            int num=0;
            int numbza=0;
            int countborrados=0;
            while(tipo!="fin") {
                serviceDevice balanza= new serviceDevice();
                tipo = Preferencias.getString("Tipo_" + num, "fin");
                if(tipo.contains("fin") && num==0){
                    tipo="Balanza";
                }
                if(tipo.equals("Borrado")){
                   numbza++;
                   countborrados++;
               }
                if(tipo.equals("Balanza")) {
//                  balanza.setNB(numbza);
                    balanza.setNB(numbza);
                    numbza++;
                    //numbza++;
                    Boolean seteo = Preferencias.getBoolean("seteo_" + num, false);
                    String Modelobza="";
                    String Salida = Preferencias.getString("Salida_" + num, "PuertoSerie 1");
                    Modelobza = Preferencias.getString("Modelo_" + num, "Optima");
                    int NumeroID = Preferencias.getInt("ID_" + num, 1);
                    //System.out.println("NumeroID "+NumeroID);
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
                        balanza.setNumborrados(countborrados);
                        balanza.setSeteo(seteo);
                        ArrayBalanzas.add(balanza);
                    }
                if(tipo == "fin" && ArrayBalanzas.size()<1){
                    serviceDevice balanx = new serviceDevice();
                    balanx.setID(-1);
                    balanx.setSalida("-1");
                    ArrayList<String> diraux= new ArrayList<String>();
                    balanx.setDireccion(diraux);
                    balanx.setTipo(0);
                    balanx.setNB(numbza-1);
                    balanx.setNumborrados(countborrados);
                    balanx.setSeteo(false);
                    ArrayBalanzas.add(balanx);
                }
                num++;
            }

            return ArrayBalanzas;
        }
//         tablayout.addTab(Balanzas);
//        tablayout.addTab(Impresoras);
//        tablayout.addTab(IO);
//        tablayout.addTab(Escaneres);
//        tablayout.addTab(Dispositivos)
        public ArrayList<serviceDevice> get_listglobindex(int TipoDevice){
            String tipodevicestr="";
            switch (TipoDevice){
                case 0:{
                    tipodevicestr="Balanza";
                    break;
                }
                case 1:{
                    tipodevicestr="Impresoras";
                    break;
                }
                case 2:{
                    tipodevicestr="IO";
                    break;
                }
                case 3:{
                    tipodevicestr="Escaner";
                    break;
                }
                case 4:{
                    tipodevicestr="Dispositivo";
                    break;
                } default:{
                    tipodevicestr="Borrado";
                    break;
                }
            }
            ArrayList<serviceDevice> ArrayBalanzas = new ArrayList<serviceDevice>();
            SharedPreferences Preferencias = activity.getSharedPreferences("devicesService", Context.MODE_PRIVATE);
            String tipo="";
            int num=0;
            int numbza=0;
            int countborrados=0;
            while(tipo!="fin") {
                serviceDevice balanza= new serviceDevice();
                tipo = Preferencias.getString("Tipo_" + num, "fin");
                if(tipo.contains("fin") && num==0){
                    tipo="Balanza";
                }
                if(tipo.equals("Borrado")){ // es probable que sea mejor (!tipo.equals(tipodevicestr))
                    numbza++;
                    countborrados++;
                }
                if(tipo.equals(tipodevicestr)) {
                    balanza.setNB(numbza);
                    numbza++;
                    balanza.setNumborrados(countborrados);
                    ArrayBalanzas.add(balanza);
                }
//                if(tipo == "fin" && ArrayBalanzas.size()<1){
//                    serviceDevice balanx = new serviceDevice();
//                    balanx.setNB(numbza-1);
//                    balanx.setNumborrados(countborrados);
//                    ArrayBalanzas.add(balanx);
//                }
                num++;
            }
            return ArrayBalanzas;
        }
        public ArrayList<serviceDevice> get_listPerPort(String Salida,int TipoDevice){
            String tipodevicestr="";
            switch (TipoDevice){
                case 0:{
                    tipodevicestr="Balanza";
                    break;
                }
                case 1:{
                    tipodevicestr="Impresoras";
                    break;
                }
                case 2:{
                    tipodevicestr="IO";
                    break;
                }
                case 3:{
                    tipodevicestr="Escaner";
                    break;
                }
                case 4:{
                    tipodevicestr="Dispositivo";
                    break;
                } default:{
                    tipodevicestr="Borrado";
                    break;
                }
            }
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
            int countborrados=0;
            while(tipo!="fin") {
                serviceDevice balanza= new serviceDevice();
                tipo = Preferencias.getString("Tipo_" + num, "fin");
                if(tipo.contains("fin") && num==0){
                    tipo="Balanza";
                }
                if(tipo.equals("Borrado")){ // es probable que sea mejor (!tipo.equals(tipodevicestr))
                    numbza++;
                    countborrados++;
                }
                if(tipo.equals(tipodevicestr)) {
//                    balanza.setNB(numbza);
                    balanza.setNB(numbza);
                    numbza++;
                    //numbza++;
                    Boolean seteo = Preferencias.getBoolean("seteo_" + num, false);
                    String Modelobza="";

                        Modelobza = Preferencias.getString("Modelo_" + num, "Optima");


                    String Salidaaux = Preferencias.getString("Salida_" + num, "PuertoSerie 1");
                    int NumeroID = Preferencias.getInt("ID_" + num, 1);
                    //System.out.println("NumeroID "+NumeroID);
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
                        balanza.setNumborrados(countborrados);
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
                   // System.out.println("NumeroID "+NumeroID);
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
                        // ESTO ESTA MAL POR QUE TODAVIA NO CONTEMPLO countborrados
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
            int countborrados=0;
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
                 if(tipo.equals("Borrado")){
                     countborrados++;
                     numbza++;
                 }
                if(tipo.equals("Balanza")) {
//                    balanza.setNB(numbza);
                    balanza.setNB(numbza);
                    numbza++;
                    //
                    Boolean seteo = Preferencias.getBoolean("seteo_" + num, false);
                    String Modelobza="";
                    if(seteo){
                         Modelobza = Preferencias.getString("Modelo_" + num, "Optima");
                    }else{
                         Modelobza = Preferencias.getString("Modelo_" + num, Modelostr);
                    }

                    String Salida = Preferencias.getString("Salida_" + num, "PuertoSerie 1");
                    int NumeroID = Preferencias.getInt("ID_" + num, 1);
                   // System.out.println("NumeroID "+NumeroID);
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
                            balanza.setNumborrados(countborrados);
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
        public Integer getID(int numBza) {
            Struct balanza = balanzas.get(numBza);
            if (balanza != null) {
                return balanza.getID(numBza);
            }else{
                return null;
            }

        }

        @Override
        public Float getNeto(int numBza) {
            Struct balanza = balanzas.get(numBza);
            if (balanza != null) {
                return balanza.getNeto(numBza);
            }
            return  null;
        }

        @Override
        public String getNetoStr(int numBza) {
            Struct balanza = balanzas.get(numBza);
            if (balanza != null) {
                return balanza.getNetoStr(numBza);
            }
            return null;
        }

        @Override
        public Float getBruto(int numBza) {
            Struct balanza = balanzas.get(numBza);
            if (balanza != null) {
                return balanza.getBruto(numBza);
            }
            return null;
        }

        @Override
        public String getBrutoStr(int numBza) {
            Struct balanza = balanzas.get(numBza);
            if (balanza != null) {
                return balanza.getBrutoStr(numBza);
            }
            return null;
        }

        @Override
        public Float getTara(int numBza) {
            Struct balanza = balanzas.get(numBza);
            if (balanza != null) {
                return balanza.getTara(numBza);
            }
            return null;
        }

        @Override
        public String getTaraStr(int numBza) {
            Struct balanza = balanzas.get(numBza);
            if (balanza != null) {
                return balanza.getTaraStr(numBza);
            }
            return null;
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
            return null;
        }

        @Override
        public Boolean getBandaCero(int numBza) {
            Struct balanza = balanzas.get(numBza);
            if (balanza != null) {
                return balanza.getBandaCero(numBza);
            }
            return null;
        }

        @Override
        public void setBandaCero(int numBza, Boolean bandaCeroi) {
            Struct balanza = balanzas.get(numBza);
            if (balanza != null) {
                balanza.setBandaCero(numBza,bandaCeroi);
            }
        }

        @Override
        public Float getBandaCeroValue(int numBza) {
            Struct balanza = balanzas.get(numBza);
            if (balanza != null) {
                return balanza.getBandaCeroValue(numBza);
            }
            return null;
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
            return null;
        }

        @Override
        public String format(int numero, String peso) {
            Struct balanza = balanzas.get(numero);
            if (balanza != null) {
                return balanza.format(numero,peso);
            }
            return null;
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
            return null;

        }

        @Override
        public String getPicoStr(int numBza) {
            Struct balanza = balanzas.get(numBza);
            if (balanza != null) {
                return balanza.getPicoStr(numBza);
            }
        return null;
        }

        @Override
        public Float getPico(int numBza) {
            Struct balanza = balanzas.get(numBza);
            if (balanza != null) {
                return balanza.getPico(numBza);
            }
            return null;
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
            return null;
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
            return null;
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
            return null;
        }
        @Override
        public String getFiltro2(int numBza) {
            return null;
        }
        @Override
        public String getFiltro3(int numBza) {
            return null;
        }
        @Override
        public String getFiltro4(int numBza) {
            return null;
        }
        @Override
        public Boolean getEstadoEstable(int numBza) {
            return null;
        }
        @Override
        public void onEvent() {
        }
        @Override
        public Boolean Itw410FrmSetear(int numero, String setPoint, int Salida) {
            Boolean a=null;
            Struct balanza = balanzas.get(numero);
            if(balanza!=null) {
              a =  balanza.Itw410FrmSetear(numero,setPoint,Salida);
            }
            return a;
        }
        @Override
        public String Itw410FrmGetSetPoint(int numero) { // Creas el CountDownLatch con un contador de 1
            String a = null;
            Struct balanza = balanzas.get(numero);
            if(balanza!=null) {
                a = balanza.Itw410FrmGetSetPoint(numero);
            }

            return a;
        }
        @Override
        public Integer Itw410FrmGetSalida(int numero) {
            Integer a = null;
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
        public Integer Itw410FrmGetEstado(int numero) {
            Integer a=null;
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
        public Integer Itw410FrmGetUltimoIndice(int numero) {
            Integer a=null;
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
      public static void addDevice(serviceDevice x){
//        ArrayList<serviceDevice> listaglob = BalanzaService.Balanzas.get_balanzalistglob();
        int cont =0; //get_nborrados();
        int count=0;
        int size=0;
//        for (int i=0;i<listaglob.size();i++) {
//            serviceDevice j =listaglob.get(i);
//            if(j.getSalida().equals(x.getSalida())&&j.getSeteo()==x.getSeteo()&&j.getTipo()==x.getTipo()&&j.getNB()==x.getNB()&&j.getID()==x.getID()&&j.getDireccion().equals(x.getDireccion())&&j.getModelo()==x.getModelo()){
//                System.out.println("REMOVE"+i+" IN "+listaglob.size());
//               size=i;
//                System.out.println("SIZE ACTUALIZATE"+listaglob.size());
//            }
//        }
     /*ArrayList<String> direccion= x.getDireccion();
        for (serviceDevice y :List) {
            if(y.getModelo()==x.getModelo()&&count==0){
                    direccion=y.getDireccion();
                    count++;
            }

        }*/

       // System.out.println("NUMBER BZA"+x.getNB());
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

        int balanzalenght= (x.getNB()+cont); //pos;//List.size();
       System.out.println("limite cambiado"+x.getNB());
        //  System.out.println("NB set device "+ balanzalenght);
        SharedPreferences Preferencias=Service.activity.getSharedPreferences("devicesService",Context.MODE_PRIVATE);
        SharedPreferences.Editor ObjEditor=Preferencias.edit();
        /*String tipo="";
        int num=0;
        while(num<=limite+1) {
            tipo = Preferencias.getString("Tipo_" + num,"fin");
            System.out.println(num);
            if (tipo=="Borrado"){
                System.out.println("cambiado BORRADO");
                cont++;
            }
            num++;
        }*/
        balanzalenght= (x.getNB()); //pos;//List.size();
        System.out.println("NB dato cambiado "+balanzalenght);
        System.out.println("tipo :( "+Tipo+ "   "
                +x.getSalida());
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
           x.setSalida(salida); // System.out.println("salida"+salida);

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
            if(tipo!="fin"&& tipo!="Borrado"){
                Arraydevicesaux.add(x);
                System.out.println("NB listglob "+ x.getNB());
            }
           num++;
        }
       ;
         return Arraydevicesaux;
    }
}
