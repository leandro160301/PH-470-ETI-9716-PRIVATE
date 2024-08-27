package com.service.Balanzas.Clases;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

import com.service.Balanzas.BalanzaService;
import com.service.Balanzas.Fragments.CalibracionItw410Fragment;
import com.service.Balanzas.Interfaz.Balanza;
import com.service.Comunicacion.OnFragmentChangeListener;
import com.service.Modbus.ModbusMasterRtu;
import com.service.Modbus.Req.ModbusReqRtuMaster;
import com.service.PuertosSerie.PuertosSerie2;
import com.zgkxzx.modbus4And.requset.OnRequestBack;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class ITW410 implements Balanza.Struct, Serializable {


    Boolean PorDemandaBool=false;
    boolean imgbool=true;
    Boolean estadoCentroCero = false;
    Boolean estadoSobrecarga = false;
    Boolean estadoNeto = false;
    Boolean estadoPesoNeg = false;
    Boolean estadoBajoCero = false;
    Boolean estadoBzaEnCero = false;
    int estado410=0;
    Boolean estadoBajaBat = false;
    int numeroSlave=1;
    public OnFragmentChangeListener fragmentChangeListener;
    Boolean estadoEstable = false;
    private ITW410 returnthiscontext(){
        return this;
    }
    public float formatpuntodec(int numero) {
        float respuesta =  numero / (float) Math.pow(10, puntoDecimal);
       return respuesta;
    }
    @Override
    public String format(int numero,String peso) {
        String formato = "0.";
        try {
            StringBuilder capacidadBuilder = new StringBuilder(formato);
            for (int i = 0; i <puntoDecimal; i++) {
                capacidadBuilder.append("0");
            }
            formato = capacidadBuilder.toString();
            DecimalFormat df = new DecimalFormat(formato);
            String str = df.format(Double.parseDouble(peso));
            return str;
        } catch (NumberFormatException e) {
            System.err.println("Error: El número no es válido.");
            e.printStackTrace();
            return "0";
        }
    }

    @Override
    public String getUnidad(int numBza) {
        SharedPreferences preferences=Service.activity.getSharedPreferences(NOMBRE, Context.MODE_PRIVATE);
        return (preferences.getString(String.valueOf(numBza)+"_"+"unidad","kg"));
    }

    @Override
    public void init(int numBza) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                ModbusReqRtuMaster Modbus;
                try {
                    ModbusMasterRtu modbusmasterinit= new ModbusMasterRtu();

                    // Modbus = modbusmasterinit.init(finalPort,modbus.getBaud(),modbus.getDatabit(),modbus.getStopbit(),modbus.getParity());
                    Modbus =modbusmasterinit.init(Service.PUERTO_A,115200,8,1,0);
                    System.out.println("INITIALIZATING");
                    if (Modbus != null) {
                        ModbusRtuMaster = Modbus;
                        estado=M_MODO_BALANZA;
                        GET_PESO_cal_bza.run();
                        pesoBandaCero= getBandaCeroValue(numBza);
                        puntoDecimal=get_PuntoDecimal();
                        ultimaCalibracion=get_UltimaCalibracion();
                    }

                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }).start();
    }
Runnable GET_PESO_cal_bza = new Runnable() {

    @Override
    public void run() {
        if(estado==M_MODO_BALANZA) {
            if (subnombre == 1) {
                System.out.println("bucle works");
                try {
                    final float[] response = {0.0F};
                    OnRequestBack<short[]> callback = new OnRequestBack<short[]>() {
                        @Override
                        public void onSuccess(short[] result) {
                            // Maneja el resultado exitoso aquí
                            Bruto = formatpuntodec(result[0]);
                            System.out.println("ITW410 Bruto:" + result[0]);
                            brutoStr = String.valueOf(Bruto);
                            Neto = formatpuntodec(result[1]);
                            System.out.println("ITW410 Neto:" + result[1]);
                            netoStr = String.valueOf(Neto);
                            Tara = formatpuntodec(result[3]);
                            System.out.println("ITW410 Tara:" + result[2]);
                            taraStr = String.valueOf(Tara);
                            estado410 = result[3];
                            System.out.println("ESTADO 410:" + result[3]);
                        }

                        @Override
                        public void onFailed(String error) {
                            // Maneja el error aquí


                        }
                    };
                    ModbusRtuMaster.readHoldingRegisters(callback, numeroSlave, 20, 4);

              /*  callback = new OnRequestBack<short[]>() {
                    @Override
                    public void onSuccess(short[] result) {
                        // Maneja el resultado exitoso aquí
                        //int peso_cal = result[0];
                        //int div_min = result[1];
                        int filter1 = result[0];
                        int filter2 = result[1];
                        int filter3 = result[2];
                        System.out.println("filters"+ filter1+filter2+filter3);
                    }

                    @Override
                    public void onFailed(String error) {
                        // Maneja el error aquí


                    }
                };
                ModbusRtuMaster.readHoldingRegisters(callback, numeroSlave, 8, 3);*/
                } catch (Exception e) {

                }
                ;

            } else if (subnombre == 2) {

                try {
                    final float[] response = {0.0F};
                    OnRequestBack<short[]> callback = new OnRequestBack<short[]>() {
                        @Override
                        public void onSuccess(short[] result) {
                            // Maneja el resultado exitoso aquí
                            Bruto = result[0];
                            System.out.println("ITW410 Bruto:" + Bruto);
                            brutoStr = String.valueOf(Bruto);
                        }

                        @Override
                        public void onFailed(String error) {
                            // Maneja el error aquí


                        }
                    };
                    ModbusRtuMaster.readHoldingRegisters(callback, numeroSlave, 3, 2);
                    Thread.sleep(200);
                    callback = new OnRequestBack<short[]>() {
                        @Override
                        public void onSuccess(short[] result) {
                            // Maneja el resultado exitoso aquí
                            Neto = result[0];
                            System.out.println("ITW410 neto:" + result[0]);
                        }

                        @Override
                        public void onFailed(String error) {
                            // Maneja el error aquí


                        }
                    };
                    ModbusRtuMaster.readHoldingRegisters(callback, numeroSlave, 4, 1);
                    Thread.sleep(200);
                    callback = new OnRequestBack<short[]>() {
                        @Override
                        public void onSuccess(short[] result) {
                            // Maneja el resultado exitoso aquí
                            Tara = result[0];
                            System.out.println("ITW410 Tara:" + Tara);
                            taraStr = String.valueOf(Tara);
                        }

                        @Override
                        public void onFailed(String error) {
                            // Maneja el error aquí


                        }
                    };
                    ModbusRtuMaster.readHoldingRegisters(callback, numeroSlave, 6, 1);
                    Thread.sleep(200);
                    callback = new OnRequestBack<short[]>() {
                        @Override
                        public void onSuccess(short[] result) {
                            // Maneja el resultado exitoso aquí
                            int peso_cal = result[0];
                            int div_min = result[1];
                            int filter1 = result[2];
                            int filter2 = result[3];
                            int filter3 = result[4];

                        }

                        @Override
                        public void onFailed(String error) {
                            // Maneja el error aquí


                        }
                    };
                    ModbusRtuMaster.readHoldingRegisters(callback, numeroSlave, 12, 5);
                    Thread.sleep(500);

                } catch (Exception e) {

                }
            };
        }
        mHandler.postDelayed(GET_PESO_cal_bza,300);

    }

};
    @Override
    public void escribir(String msj,int numBza) {

    }


    @Override
    public void stop(int numBza) {

    }

    @Override
    public void start(int numBza) {
        estado=M_MODO_BALANZA;
    }

    @Override
    public Boolean calibracionHabilitada(int numBza) {
        return false;
    }

    @Override
    public void Itw410FrmSetTiempoEstabilizacion(int numero, int Tiempo) {
        final String[] res = {""};
        OnRequestBack<String> callback2 = new OnRequestBack<String>() {
            @Override
            public void onSuccess(String result) {
                // Maneja el resultado exitoso aquí
                res[0] =result;
                System.out.println("ITW410 setear:"+result);
            }

            @Override
            public void onFailed(String error) {
                // Maneja el error aquí
                res[0] =error;

            }
        };
        ModbusRtuMaster.writeRegister(callback2,numeroSlave,31,Tiempo);
    }

    @Override
    public void openCalibracion(int numero) {
        estado=M_MODO_CALIBRACION;
        CalibracionItw410Fragment fragment = CalibracionItw410Fragment.newInstance(context, Service);
        Bundle args = new Bundle();
        args.putSerializable("instance", context);
        args.putSerializable("instanceService",Service);

        fragmentChangeListener.openFragmentService(fragment,args);
    }

    @Override
    public Boolean getSobrecarga(int numBza) {
        return null;
    }


    private final ITW410 context;
    PuertosSerie2.PuertosSerie2Listener receiver =null;
    public ModbusReqRtuMaster ModbusRtuMaster;
    public BalanzaService Service;
    Handler mHandler= new Handler();
    String NOMBRE="ITW410";

    public String estado="VERIFICANDO_MODO";
    public static final String M_VERIFICANDO_MODO="VERIFICANDO_MODO";
    public static final String M_MODO_BALANZA="MODO_BALANZA";
    public static final String M_MODO_CALIBRACION="MODO_CALIBRACION";
    public static final String M_ERROR_COMUNICACION="M_ERROR_COMUNICACION";
    public float taraDigital=0,Bruto=0,Tara=0,Neto=0,pico=0;
    public String estable="";
    float pesoUnitario=0.5F;
    public PuertosSerie2.SerialPortReader readers=null;
    float pesoBandaCero=0F;
    public Boolean bandaCero =true;
    public Boolean inicioBandaPeso=false;
    public int puntoDecimal=1;
    public String ultimaCalibracion="";
    public String brutoStr="0",netoStr="0",taraStr="0",taraDigitalStr="0",picoStr="0";
    public int acumulador=0;

    public  int subnombre;
    private int numerobza= 0;

    public ITW410( int numBza, BalanzaService activity, OnFragmentChangeListener fragmentChangeListener, int numbza410) {
        this.numerobza = numBza;
        this.Service = activity;
        this.fragmentChangeListener=fragmentChangeListener;
        context=this;
        this.subnombre =numbza410;

    }


    public void setTara(float tara){


        Tara=tara;
        taraStr=String.valueOf(tara);
    }

    public void setTaraDigital(float tara){
        taraDigital=tara;
        taraDigitalStr=String.valueOf(tara);

    }

    public void setPesoBandaCero(float peso){
        pesoBandaCero=peso;
        SharedPreferences preferencias=Service.activity.getSharedPreferences("ITW410", Context.MODE_PRIVATE);
        SharedPreferences.Editor ObjEditor=preferencias.edit();
        ObjEditor.putFloat(String.valueOf(numeroSlave)+"_"+"pbandacero", peso);
        ObjEditor.apply();
    }

    public float getPesoBandaCero() {
        SharedPreferences preferences=Service.activity.getSharedPreferences("ITW410", Context.MODE_PRIVATE);
        return (preferences.getFloat(String.valueOf(numeroSlave)+"_"+"pbandacero",5.0F));
    }


    public String Tara(){
        if(subnombre==1){

            OnRequestBack<String> callback = new OnRequestBack<String>() {
                @Override
                public void onSuccess(String result) {
                    // Maneja el resultado exitoso aquí
                    System.out.println("ITW410 tare:"+result);

                }

                @Override
                public void onFailed(String error) {
                    // Maneja el error aquí


                }
            };
            ModbusRtuMaster.writeCoil(callback,numeroSlave,1,true);

        } else if (subnombre==2) {
            OnRequestBack<String>  callback = new OnRequestBack<String>() {
                @Override
                public void onSuccess(String result) {
                    // Maneja el resultado exitoso aquí


                }

                @Override
                public void onFailed(String error) {
                    // Maneja el error aquí


                }
            };
            ModbusRtuMaster.writeCoil(callback,numeroSlave,4,true);

        }
        return "";
    }
    public ArrayList<String> Pedirparam() { // NUEVO
        ArrayList<String> list = new ArrayList<>();
        CountDownLatch latch = new CountDownLatch(1);
        OnRequestBack<short[]> callback = new OnRequestBack<short[]>() {
            @Override
            public void onSuccess(short[] result) {
                // Maneja el resultado exitoso aquí
                list.add(Integer.toString(result[0]));
                System.out.println("ITW410 pesocon:"+result[0]);
                list.add(Integer.toString(result[1]));

                System.out.println("ITW410 divmin:"+result[1]);
                list.add(Integer.toString(result[2]));

                System.out.println("ITW410 filtro1:"+result[2]);
                list.add(Integer.toString(result[3]));

                System.out.println("ITW410 filtro2:"+result[3]);
                list.add(Integer.toString(result[4]));

                System.out.println("ITW410 filtro3:"+result[4]);
                latch.countDown();

            }

            @Override
            public void onFailed(String error) {
                // Maneja el error aquí
                list.add("");
                list.add("");
                list.add("");
                list.add("");
                list.add("");
                list.add("");
                latch.countDown();


            }
        };
        ModbusRtuMaster.readHoldingRegisters(callback, numeroSlave, 6, 5);
        try {
            latch.await(1000, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        return list;
    }

    public void set_filtros(int filtro1,int filtro2,int filtro3){
        SharedPreferences Preferencias=Service.activity.getSharedPreferences("ITW410",Context.MODE_PRIVATE);
        SharedPreferences.Editor ObjEditor=Preferencias.edit();
        ObjEditor.putInt(String.valueOf(numeroSlave)+"_"+"filtro1",filtro1);
        ObjEditor.putInt(String.valueOf(numeroSlave)+"_"+"filtro2",filtro2);
        ObjEditor.putInt(String.valueOf(numeroSlave)+"_"+"filtro3",filtro3);
        ObjEditor.apply();
    }
    String get_filtro1(){
        SharedPreferences Preferencias=Service.activity.getSharedPreferences("ITW410",Context.MODE_PRIVATE);
        return Preferencias.getString(String.valueOf(numeroSlave)+"_"+"filtro1","null");
    }
    String get_filtro2(){
        SharedPreferences Preferencias=Service.activity.getSharedPreferences("ITW410",Context.MODE_PRIVATE);
        return Preferencias.getString(String.valueOf(numeroSlave)+"_"+"filtro2","null");
    }
    String get_filtro3(){
        SharedPreferences Preferencias=Service.activity.getSharedPreferences("ITW410",Context.MODE_PRIVATE);
        return Preferencias.getString(String.valueOf(numeroSlave)+"_"+"filtro3","null");
    }
    public void enviarParametros(ArrayList<Integer> listavalores){ //Division minim,Pesoconocido,filtro1,filtro2,filtro3 5vals
        if(ModbusRtuMaster!=null) {
            CountDownLatch latch = new CountDownLatch(1);
            OnRequestBack<String>  callback5 = new OnRequestBack<String>() {
                @Override
                public void onSuccess(String result) {
                    // Maneja el resultado exitoso aquí
                    System.out.println("ITW410 filtro3"+result);
                    latch.countDown();

                }

                @Override
                public void onFailed(String error) {
                    // Maneja el error aquí
                    latch.countDown();

                }
            };
            OnRequestBack<String>  callback4 = new OnRequestBack<String>() {
                @Override
                public void onSuccess(String result) {
                    // Maneja el resultado exitoso aquí
                    ModbusRtuMaster.writeRegister(callback5,numeroSlave,10,listavalores.get(4));
                    System.out.println("ITW410 filtro2"+result);


                }

                @Override
                public void onFailed(String error) {
                    // Maneja el error aquí


                }
            };
            OnRequestBack<String>  callback3 = new OnRequestBack<String>() {
                @Override
                public void onSuccess(String result) {
                    // Maneja el resultado exitoso aquí
                    ModbusRtuMaster.writeRegister(callback4,numeroSlave,9,listavalores.get(3));
                    System.out.println("ITW410 filtro1"+result);


                }

                @Override
                public void onFailed(String error) {
                    // Maneja el error aquí


                }
            };
            OnRequestBack<String>  callback2 = new OnRequestBack<String>() {
                @Override
                public void onSuccess(String result) {
                    // Maneja el resultado exitoso aquí
                    ModbusRtuMaster.writeRegister(callback3,numeroSlave,8,listavalores.get(2));
                    System.out.println("ITW410 pesoconocido"+result+listavalores.get(1).toString());


                }

                @Override
                public void onFailed(String error) {
                    // Maneja el error aquí


                }
            };
            OnRequestBack<String>  callback = new OnRequestBack<String>() {
                @Override
                public void onSuccess(String result) {
                    // Maneja el resultado exitoso aquí
                    ModbusRtuMaster.writeRegister(callback2,numeroSlave,6,listavalores.get(1));
                    System.out.println("ITW410 divmin"+result);

                }

                @Override
                public void onFailed(String error) {
                    // Maneja el error aquí


                }
            };
            ModbusRtuMaster.writeRegister(callback,numeroSlave,7,listavalores.get(0));

            try {
                latch.await(1000, TimeUnit.MILLISECONDS);
            } catch (Exception e) {
                Thread.currentThread().interrupt();
            }
        }
    }
   public String Guardar_cal(){
            OnRequestBack<String>  callback = new OnRequestBack<String>() {
                @Override
                public void onSuccess(String result) {
                    // Maneja el resultado exitoso aquí
                    String peso_cal = result;

                }

                @Override
                public void onFailed(String error) {
                    // Maneja el error aquí


                }
            };
            ModbusRtuMaster.writeCoil(callback,numeroSlave,10,true);

        return "\u0005S\r";
    }
    public String PesoConFormat(String pesoconocido, int puntoDecimalx){
        try{
            String valueFormateado="";
            if(pesoconocido.contains(".")){
                valueFormateado= formatearNumero(Double.parseDouble(pesoconocido),puntoDecimal);
                int enter = valueFormateado.length();
                int end = 0;
                if (valueFormateado.contains(".")) {
                    enter = valueFormateado.indexOf('.');
                    end = valueFormateado.length() - (enter + 1);
                }
                while (end<puntoDecimal){
                    valueFormateado=valueFormateado+"0";
                    end++;
                }

            }else{
                StringBuilder x =new StringBuilder(pesoconocido);
                for (int i=0;i<puntoDecimal;i++){
                    x.append(0);
                }
                valueFormateado= String.valueOf(x);
            }
            return valueFormateado;

        }catch (Exception e){
            return null;
        }
    }
    public String Peso_conocido(String pesoconocido,String PuntoDecimal){
        String pesoConocido="";
        if(subnombre==1) {
            OnRequestBack<String> callback = new OnRequestBack<String>() {
                @Override
                public void onSuccess(String result) {
                }

                @Override
                public void onFailed(String error) {
                }
            };
            ModbusRtuMaster.writeCoil(callback, numeroSlave, 5, true);
        }else if (subnombre==2) {
            OnRequestBack<String>  callback = new OnRequestBack<String>() {
                @Override
                public void onSuccess(String result) {
                }

                @Override
                public void onFailed(String error) {
                }
            };
            ModbusRtuMaster.writeCoil(callback,numeroSlave,8,true);
        }

        return pesoConocido;
    }
    public String Cero_cal(){
        if(subnombre==1) {
            OnRequestBack<String> callback = new OnRequestBack<String>() {
                @Override
                public void onSuccess(String result) {
                }
                @Override
                public void onFailed(String error) {
                }
            };
            ModbusRtuMaster.writeCoil(callback, numeroSlave, 4, true);
        }else if (subnombre==2) {
            OnRequestBack<String>  callback = new OnRequestBack<String>() {
                @Override
                public void onSuccess(String result) {
                }
                @Override
                public void onFailed(String error) {
                }
            };
            ModbusRtuMaster.writeCoil(callback,numeroSlave,7,true);
        }
        return "";
    }
    public String Recero_cal(){

        CountDownLatch latch = new
                CountDownLatch(1);
        if(subnombre==1) {
            OnRequestBack<String> callback = new OnRequestBack<String>() {
                @Override
                public void onSuccess(String result) {
                    latch.countDown();
                }
                @Override
                public void onFailed(String error) {
                    latch.countDown();
                }
            };
            ModbusRtuMaster.writeCoil(callback, numeroSlave, 6, true);
        }else if (subnombre==2) {
            OnRequestBack<String>  callback = new OnRequestBack<String>() {
                @Override
                public void onSuccess(String result) {
                    latch.countDown();
                }

                @Override
                public void onFailed(String error) {
                    latch.countDown();
                }
            };
            ModbusRtuMaster.writeCoil(callback,numeroSlave,9,true);
        }
        try{
            latch.await(1000,TimeUnit.MILLISECONDS);
        }catch (Exception e){
            Thread.currentThread().interrupt();
        }
        return "";
    }
    public void setPesoUnitario(float peso){
        pesoUnitario=peso;
        SharedPreferences preferencias=Service.activity.getSharedPreferences("ITW410", Context.MODE_PRIVATE);
        SharedPreferences.Editor ObjEditor=preferencias.edit();
        ObjEditor.putFloat(String.valueOf(numeroSlave)+"_"+"punitario", 0.5F);
        ObjEditor.apply();
    }

    public float getPesoUnitario() {
        SharedPreferences preferences=Service.activity.getSharedPreferences("ITW410", Context.MODE_PRIVATE);
        return (preferences.getFloat(String.valueOf(numeroSlave)+"_"+"punitario",0.5F));
    }


    public void setUnidad(String Unidad){
        SharedPreferences preferencias=Service.activity.getSharedPreferences("ITW410", Context.MODE_PRIVATE);
        SharedPreferences.Editor ObjEditor=preferencias.edit();
        ObjEditor.putString(String.valueOf(numeroSlave)+"_"+"unidad",Unidad);
        ObjEditor.apply();
    }

    public String getUnidad() {
        //Trae la unidad producto guardado en memoria
        SharedPreferences preferences=Service.activity.getSharedPreferences("ITW410", Context.MODE_PRIVATE);

        //System.out.println("MINIMA ERR UNIDAD:"+preferences.toString()+" "+numero);
        return (preferences.getString(String.valueOf(numeroSlave)+"_"+"unidad","kg"));
    }

    public void set_DivisionMinima(int divmin){

        SharedPreferences Preferencias=Service.activity.getSharedPreferences("ITW410",Context.MODE_PRIVATE);
        SharedPreferences.Editor ObjEditor=Preferencias.edit();
        ObjEditor.putInt(String.valueOf(numeroSlave)+"_"+"div",divmin);
        ObjEditor.apply();

    }
    public void set_PuntoDecimal(int puntoDecimal){
        this.puntoDecimal=puntoDecimal;
        SharedPreferences Preferencias=Service.activity.getSharedPreferences("ITW410",Context.MODE_PRIVATE);
        SharedPreferences.Editor ObjEditor=Preferencias.edit();
        ObjEditor.putInt(String.valueOf(numeroSlave)+"_"+"pdecimal",puntoDecimal);
        ObjEditor.apply();

    }
    public void set_UltimaCalibracion(String ucalibracion){
        SharedPreferences Preferencias=Service.activity.getSharedPreferences("ITW410",Context.MODE_PRIVATE);
        SharedPreferences.Editor ObjEditor=Preferencias.edit();
        ObjEditor.putString(String.valueOf(numeroSlave)+"_"+"ucalibracion",ucalibracion);
        ObjEditor.apply();

    }
    public String get_UltimaCalibracion(){
        SharedPreferences Preferencias=Service.activity.getSharedPreferences("ITW410",Context.MODE_PRIVATE);
        return Preferencias.getString(String.valueOf(numeroSlave)+"_"+"ucalibracion","");

    }
    public void set_CapacidadMax(String capacidad){

        SharedPreferences Preferencias=Service.activity.getSharedPreferences("ITW410",Context.MODE_PRIVATE);
        SharedPreferences.Editor ObjEditor=Preferencias.edit();
        ObjEditor.putString(String.valueOf(numeroSlave)+"_"+"capacidad",capacidad);
        ObjEditor.apply();

    }
    public void set_PesoConocido(String pesoConocido){

        SharedPreferences Preferencias=Service.activity.getSharedPreferences("ITW410",Context.MODE_PRIVATE);
        SharedPreferences.Editor ObjEditor=Preferencias.edit();
        ObjEditor.putString(String.valueOf(numeroSlave)+"_"+"pconocido",pesoConocido);
        ObjEditor.apply();

    }

    public int get_DivisionMinima(){
        SharedPreferences Preferencias=Service.activity.getSharedPreferences("ITW410",Context.MODE_PRIVATE);
        return Preferencias.getInt(String.valueOf(numeroSlave)+"_"+"div",0);

    }
    public int get_PuntoDecimal(){
        SharedPreferences Preferencias=Service.activity.getSharedPreferences("ITW410",Context.MODE_PRIVATE);
        int lea=Preferencias.getInt(String.valueOf(numeroSlave)+"_"+"pdecimal", 1);
        System.out.println("MINIMA CALIBRACION PUNTO DECIMAL: "+String.valueOf(lea));
        return lea;

    }
    public String get_CapacidadMax(){
        SharedPreferences Preferencias=Service.activity.getSharedPreferences("ITW410",Context.MODE_PRIVATE);
        return Preferencias.getString(String.valueOf(numeroSlave)+"_"+"capacidad","100");
    }
    public String get_PesoConocido(){
        SharedPreferences Preferencias=Service.activity.getSharedPreferences("ITW410",Context.MODE_PRIVATE);
        return Preferencias.getString(String.valueOf(numeroSlave)+"_"+"pconocido","100");
    }

    public void stopRuning(){
        estado=M_MODO_CALIBRACION;
    }
    public void startRuning(){
        estado=M_MODO_BALANZA;
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
        return get_filtro1();
    }

    @Override
    public String getFiltro2(int numBza) {
        return get_filtro2();
    }

    @Override
    public String getFiltro3(int numBza) {
        return get_filtro3();
    }

    @Override
    public String getFiltro4(int numBza) {
        return null;
    }

    @Override
    public Boolean getEstadoEstable(int numBza) {
        return false;
    }

    @Override
    public void onEvent() {

    }
    public static String pointDecimalFormat(String numero, int decimales) {
        try {
            Double.parseDouble(numero);
        } catch (NumberFormatException e) {
            return "0000";
        }

        String formato = "0";
        if (decimales > 0) {
            formato += ".";
            for (int i = 0; i < decimales; i++) {
                formato += "0";
            }
        }

        DecimalFormat decimalFormat = new DecimalFormat(formato);
        return decimalFormat.format(Double.parseDouble(numero));
    }

    String formatsp(String num,int dec){
        StringBuilder x = new StringBuilder(num);
        for (int i=0;i<dec;i++){
            if(num.contains(".")) {
                String[] z = num.split("\\.");
                if (z[1].length() < dec) {
                    x.append(0);
                }
            }else{
                x.append(0);
            }
        }
        num=x.toString();
       return num.replace(".","");
    }


        public static String formatearNumero(double numero, int decimales) {
            StringBuilder pattern = new StringBuilder("0.");
            for (int i = 0; i < decimales; i++) {
                pattern.append("#");
            }
            DecimalFormat decimalFormat = new DecimalFormat(pattern.toString());
            return decimalFormat.format(numero);
        }
    @Override
    public Boolean Itw410FrmSetear(int numero, String setPoint, int Salida) {
        String valueFormateado=format(this.numerobza,setPoint).replace(".","");
        final Boolean[] Resulte = {false};
        final String[] res = {""};

        CountDownLatch latch= new CountDownLatch(1);
        OnRequestBack<String> callback2 = new OnRequestBack<String>() {
            @Override
            public void onSuccess(String result) {
                // Maneja el resultado exitoso aquí
                res[0] =result;
                System.out.println("ITW410 setear:"+result);
                latch.countDown();
                Resulte[0] =true;
            }

            @Override
            public void onFailed(String error) {
                // Maneja el error aquí
                res[0] =error;
                latch.countDown();
                Resulte[0] =false;
            }
        };
        OnRequestBack<String> callback = new OnRequestBack<String>() {
            @Override
            public void onSuccess(String result) {
                // Maneja el resultado exitoso aquí
                res[0] =result;
                ModbusRtuMaster.writeRegister(callback2,numeroSlave,30,Salida);

            }

            @Override
            public void onFailed(String error) {
                // Maneja el error aquí
                res[0] =error;
                Resulte[0] =false;
            }
        };

        ModbusRtuMaster.writeRegister(callback,numeroSlave,29,Integer.parseInt(valueFormateado));
        try{
            latch.await(1000,TimeUnit.MILLISECONDS);
        }catch(Exception e){
            Thread.currentThread().interrupt();
            Resulte[0] =false;
        }
        return Resulte[0];
    }

    /* @Override
    public void Itw410FrmSetear(int numero, String setPoint, int Salida) {
        String valueFormateado="";
        if(setPoint.contains(".")){
             valueFormateado= formatearNumero(Double.parseDouble(setPoint),puntoDecimal).replace(".","");
        }else{
            StringBuilder x =new StringBuilder(setPoint);
            for (int i=0;i<puntoDecimal;i++){
                x.append(0);
            }
            valueFormateado= String.valueOf(x);
        }
        final String[] res = {""};
        System.out.println("antes:"+setPoint+"despues:"+valueFormateado);
        CountDownLatch latch= new CountDownLatch(1);
        OnRequestBack<String> callback2 = new OnRequestBack<String>() {
            @Override
            public void onSuccess(String result) {
                // Maneja el resultado exitoso aquí
                res[0] =result;
                System.out.println("ITW410 setear:"+result);
                latch.countDown();
            }

            @Override
            public void onFailed(String error) {
                // Maneja el error aquí
                res[0] =error;
                latch.countDown();

            }
        };
        OnRequestBack<String> callback = new OnRequestBack<String>() {
            @Override
            public void onSuccess(String result) {
                // Maneja el resultado exitoso aquí
                res[0] =result;
                ModbusRtuMaster.writeRegister(callback2,numeroSlave,30,Salida);

            }

            @Override
            public void onFailed(String error) {
                // Maneja el error aquí
                res[0] =error;

            }
        };

        ModbusRtuMaster.writeRegister(callback,numeroSlave,29,Integer.parseInt(valueFormateado));
        try{
            latch.await(1000,TimeUnit.MILLISECONDS);
        }catch(Exception e){
            Thread.currentThread().interrupt();
        }
    }
    */
    @Override
    public String Itw410FrmGetSetPoint(int numero) {
        final short[][] res = {new short[0]};

        final String[] valueformater = new String[1];
        CountDownLatch latch = new CountDownLatch(1);
        OnRequestBack<short[]> callback = new OnRequestBack<short[]>() {
            @Override
            public void onSuccess(short[] result) {
                // Maneja el resultado exitoso aquí
                int v = result[0];
                //valueformater[0] =formatearNumero(Double.parseDouble(String.valueOf(v)),puntoDecimal);
               // System.out.println("con formatearnumero antes:"+result[0]+"despues:"+valueformater[0]);
                valueformater[0]= String.valueOf(formatpuntodec(result[0]));
                System.out.println("con formatpuntodec antes:"+result[0]+"despues:"+valueformater[0]);

                latch.countDown();
            }

            @Override
            public void onFailed(String error) {
                // Maneja el error aquí
                latch.countDown();


            }
        };
        ModbusRtuMaster.readHoldingRegisters(callback,numeroSlave,29,1);
        try {
            latch.await(1000, TimeUnit.MILLISECONDS); // Espera hasta que el callback se complete
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            // Maneja la excepción si es necesario
        }
        return valueformater[0];
    }

    @Override
    public int Itw410FrmGetSalida(int numero) {
        final short[][] res = {new short[0]};
        final int[] response = {-1};
        CountDownLatch latch = new CountDownLatch(1);
        OnRequestBack<short[]> callback = new OnRequestBack<short[]>() {
            @Override
            public void onSuccess(short[] result) {
                // Maneja el resultado exitoso aquí
                res[0] =result;
                response[0] =result[0];
                latch.countDown();
                System.out.println("ITW410 salida:"+result[0]);
            }

            @Override
            public void onFailed(String error) {
                // Maneja el error aquí
                response[0] =-1;
                latch.countDown();

            }
        };
        ModbusRtuMaster.readHoldingRegisters(callback,numeroSlave,30,1);

        try {
            latch.await(1000, TimeUnit.MILLISECONDS); // Espera hasta que el callback se complete
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            // Maneja la excepción si es necesario
        }
        return response[0];
    }

    @Override
    public void Itw410FrmStart(int numero) {
        final String[] res = {""};

        OnRequestBack<String> callback = new OnRequestBack<String>() {
            @Override
            public void onSuccess(String result) {
                // Maneja el resultado exitoso aquí
                System.out.println("ITW 410 Arranque");
                res[0] =result;
            }

            @Override
            public void onFailed(String error) {
                // Maneja el error aquí
                res[0] =error;

            }
        };
        ModbusRtuMaster.writeCoil(callback,numeroSlave,11,true);
    }

    @Override
    public int Itw410FrmGetEstado(int numero) {
        return estado410;
    }

    @Override
    public String Itw410FrmGetUltimoPeso(int numero) {
        final short[][] res = {new short[0]};
        final CountDownLatch latch = new CountDownLatch(1);
        final String[] response = {"null"};
        OnRequestBack<short[]> callback = new OnRequestBack<short[]>() {
            @Override
            public void onSuccess(short[] result) {
                // Maneja el resultado exitoso aquí
                res[0] =result;
                response[0] = String.valueOf(formatpuntodec(result[0]));
                latch.countDown();
                System.out.println("ITW410 ultimo peso:"+result);
            }

            @Override
            public void onFailed(String error) {
                // Maneja el error aquí
                response[0] ="null";
                latch.countDown();


            }
        };
        ModbusRtuMaster.readHoldingRegisters(callback,numeroSlave,25,1);
        try {
            latch.await(1000, TimeUnit.MILLISECONDS); // Espera hasta que el callback se complete
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            // Maneja la excepción si es necesario
        }

        return response[0];
    }
    public void salir_cal(){
        // open principal
        Service.openServiceFragment();
    }
    @Override
    public int Itw410FrmGetUltimoIndice(int numero) {
        final short[][] res = {new short[0]};
        final int[] response = {-1};
        final CountDownLatch latch = new CountDownLatch(1);
        OnRequestBack<short[]> callback = new OnRequestBack<short[]>() {
            @Override
            public void onSuccess(short[] result) {
                // Maneja el resultado exitoso aquí
                res[0] =result;
                response[0] =result[0];
                latch.countDown(); // Libera el lock
                System.out.println("ITW410 ultimo indice:"+result);
            }

            @Override
            public void onFailed(String error) {
                // Maneja el error aquí
                response[0] =-1;
                latch.countDown();

            }
        };

        ModbusRtuMaster.readHoldingRegisters(callback,numeroSlave,24,1);
        try {
            latch.await(1000, TimeUnit.MILLISECONDS); // Espera hasta que el callback se complete
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            // Maneja la excepción si es necesario
        }

        return response[0];
    }

    @Override
    public void itw410FrmPause(int numero) {
        OnRequestBack<String> callback = new OnRequestBack<String>() {
            @Override
            public void onSuccess(String result) {
                // Maneja el resultado exitoso aquí

                System.out.println("ITW410 pause:"+result);
            }
            @Override
            public void onFailed(String error) {
                // Maneja el error aquí


            }
        };

        ModbusRtuMaster.writeCoil(callback, numeroSlave, 12, true);
    }

    @Override
    public void itw410FrmStop(int numero) {
        OnRequestBack<String> callback = new OnRequestBack<String>() {
            @Override
            public void onSuccess(String result) {
                // Maneja el resultado exitoso aquí

                OnRequestBack<String> callback = new OnRequestBack<String>() {
                    @Override
                    public void onSuccess(String result) {
                        // Maneja el resultado exitoso aquí
                        System.out.println("ITW410 stop:"+result);
                    }
                    @Override
                    public void onFailed(String error) {
                        // Maneja el error aquí


                    }
                };

                ModbusRtuMaster.writeCoil(callback, numeroSlave, 12, true);
            }
            @Override
            public void onFailed(String error) {
                // Maneja el error aquí


            }
        };

        ModbusRtuMaster.writeCoil(callback, numeroSlave, 12, true);
    }

    @Override
    public String getPicoStr(int numBza) {
        return picoStr;
    }

    @Override
    public float getPico(int numBza) {
        return pico;
    }

    @Override
    public void setID(int numID,int numBza) {
        this.numeroSlave=numID;
    }

    @Override
    public int getID(int numBza) {
        return this.numeroSlave;
    }

    public float getNeto(int numBza) {
        return Neto;
    }
    @Override
    public String getNetoStr(int numBza) {
        return netoStr;
    }

    @Override
    public float getBruto(int numBza) {return Bruto;
    }

    @Override
    public String getBrutoStr(int numBza) {
        return brutoStr;
    }

    @Override
    public float getTara(int numBza) {
        return 0;
    }

    @Override
    public String getTaraStr(int numBza) {
        return "0";
    }

    @Override
    public void setTara(int numBza) {
        if(ModbusRtuMaster !=null){

            if(subnombre==1) {
                OnRequestBack<String> callback = new OnRequestBack<String>() {
                    @Override
                    public void onSuccess(String result) {
                        // Maneja el resultado exitoso aquí
                        System.out.println("ITW410 tara:"+result);

                    }
                    @Override
                    public void onFailed(String error) {
                        // Maneja el error aquí


                    }
                };

                ModbusRtuMaster.writeCoil(callback, numeroSlave, 1, true);
            }else if (subnombre==2) {
                OnRequestBack<String>  callback = new OnRequestBack<String>() {
                    @Override
                    public void onSuccess(String result) {
                        // Maneja el resultado exitoso aquí
                        String peso_cal = result;

                    }

                    @Override
                    public void onFailed(String error) {
                        // Maneja el error aquí


                    }
                };
                ModbusRtuMaster.writeCoil(callback,numeroSlave,3,true);
        }
    }}
    public void setRecerocal(){
        if(ModbusRtuMaster !=null){
            CountDownLatch latch = new CountDownLatch(1);
            Tara=0;
            setTaraDigital(0);
            if(subnombre==1) {
                OnRequestBack<String> callback = new OnRequestBack<String>() {
                    @Override
                    public void onSuccess(String result) {
                        // Maneja el resultado exitoso aquí
                        String peso_cal = result;
                        latch.countDown();
                        System.out.println("ITW410 recero:"+result);


                    }

                    @Override
                    public void onFailed(String error) {
                        // Maneja el error aquí
                        latch.countDown();


                    }
                };
                ModbusRtuMaster.writeCoil(callback, numeroSlave, 6, true);
            }else if (subnombre==2) {
                OnRequestBack<String>  callback = new OnRequestBack<String>() {
                    @Override
                    public void onSuccess(String result) {
                        // Maneja el resultado exitoso aquí
                        String peso_cal = result;

                        latch.countDown();

                    }

                    @Override
                    public void onFailed(String error) {
                        // Maneja el error aquí
                        latch.countDown();

                    }
                };
                ModbusRtuMaster.writeCoil(callback,numeroSlave,9,true);
            }
            try{
                latch.await(1000, TimeUnit.MILLISECONDS);
            }catch (Exception e){
                Thread.currentThread().interrupt();
            }

        }

    }
    public  void setSpancal(){
        if(ModbusRtuMaster !=null){

            CountDownLatch latch = new CountDownLatch(1);
            Tara=0;
            setTaraDigital(0);
            if(subnombre==1) {
                OnRequestBack<String> callback = new OnRequestBack<String>() {
                    @Override
                    public void onSuccess(String result) {
                        // Maneja el resultado exitoso aquí
                        String peso_cal = result;
                        latch.countDown();
                        System.out.println("ITW410 span:"+result);


                    }

                    @Override
                    public void onFailed(String error) {
                        // Maneja el error aquí
                        latch.countDown();


                    }
                };
                ModbusRtuMaster.writeCoil(callback, numeroSlave, 5, true);
            }else if (subnombre==2) {
                OnRequestBack<String>  callback = new OnRequestBack<String>() {
                    @Override
                    public void onSuccess(String result) {
                        // Maneja el resultado exitoso aquí
                        String peso_cal = result;

                        latch.countDown();

                    }

                    @Override
                    public void onFailed(String error) {
                        // Maneja el error aquí
                        latch.countDown();

                    }
                };
                ModbusRtuMaster.writeCoil(callback,numeroSlave,8,true);
            }
            try{
                latch.await(1000, TimeUnit.MILLISECONDS);
            }catch (Exception e){
                Thread.currentThread().interrupt();
            }

        }

    }
    public  void setCerocal(){
        if(ModbusRtuMaster !=null){

            CountDownLatch latch = new CountDownLatch(1);
            Tara=0;
            setTaraDigital(0);
            if(subnombre==1) {
                OnRequestBack<String> callback = new OnRequestBack<String>() {
                    @Override
                    public void onSuccess(String result) {
                        // Maneja el resultado exitoso aquí
                        String peso_cal = result;
                        latch.countDown();
                        System.out.println("ITW410 Cerocal:"+result);


                    }

                    @Override
                    public void onFailed(String error) {
                        // Maneja el error aquí
                        latch.countDown();


                    }
                };
                ModbusRtuMaster.writeCoil(callback, numeroSlave, 4, true);
            }else if (subnombre==2) {
                OnRequestBack<String>  callback = new OnRequestBack<String>() {
                    @Override
                    public void onSuccess(String result) {
                        // Maneja el resultado exitoso aquí
                        String peso_cal = result;

                        latch.countDown();

                    }

                    @Override
                    public void onFailed(String error) {
                        // Maneja el error aquí
                        latch.countDown();

                    }
                };
                ModbusRtuMaster.writeCoil(callback,numeroSlave,7,true);
            }
            try{
                latch.await(1000, TimeUnit.MILLISECONDS);
            }catch (Exception e){
                Thread.currentThread().interrupt();
            }

        }

    }
    @Override
    public void setCero(int numBza) {
        if(ModbusRtuMaster !=null){

            Tara=0;
            setTaraDigital(0);
            if(subnombre==1) {
                OnRequestBack<String> callback = new OnRequestBack<String>() {
                    @Override
                    public void onSuccess(String result) {
                        // Maneja el resultado exitoso aquí
                        String peso_cal = result;
                        System.out.println("ITW410 Cero:"+result);


                    }

                    @Override
                    public void onFailed(String error) {
                        // Maneja el error aquí


                    }
                };
                ModbusRtuMaster.writeCoil(callback, numeroSlave, 0, true);
            }else if (subnombre==2) {
                OnRequestBack<String>  callback = new OnRequestBack<String>() {
                    @Override
                    public void onSuccess(String result) {
                        // Maneja el resultado exitoso aquí
                        String peso_cal = result;


                    }

                    @Override
                    public void onFailed(String error) {
                        // Maneja el error aquí

                    }
                };
                ModbusRtuMaster.writeCoil(callback,numeroSlave,3,true);
            }
         }
        taraDigitalStr="0";
    }

    @Override
    public void setTaraDigital(int numBza, float TaraDigital) {
        taraDigital = TaraDigital;
        taraDigitalStr=String.valueOf(TaraDigital);
    }

    @Override
    public String getTaraDigital(int numBza) {
        return taraDigitalStr;
    }

    @Override
    public void setBandaCero(int numBza, Boolean bandaCeroi) {
        bandaCero=bandaCeroi;
    }

    @Override
    public Boolean getBandaCero(int numBza) {
        return bandaCero;
    }

    @Override
    public float getBandaCeroValue(int numBza) {
        SharedPreferences preferences=Service.activity.getSharedPreferences(NOMBRE, Context.MODE_PRIVATE);
        return (preferences.getFloat(String.valueOf(numBza)+"_"+"pbandacero",5.0F));
    }
    @Override
    public void setBandaCeroValue(int numBza, float bandaCeroValue) {
        pesoBandaCero=bandaCeroValue;
        SharedPreferences preferencias=Service.activity.getSharedPreferences(NOMBRE, Context.MODE_PRIVATE);
        SharedPreferences.Editor ObjEditor=preferencias.edit();
        ObjEditor.putFloat(String.valueOf(numBza)+"_"+"pbandacero",bandaCeroValue);
        ObjEditor.apply();
    }

    @Override
    public Boolean getEstable(int numBza) {
        return null;
    }
    @Override
    public String getEstado(int numBza) {
        return estado;
    }
    @Override
    public void setEstado(int numBza, String estado) {
        this.estado=estado;
    }

};

