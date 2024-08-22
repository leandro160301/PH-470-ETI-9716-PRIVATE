package com.service.Balanzas.Clases;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.Image;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import com.service.Balanzas.BalanzaService;
import com.service.Balanzas.Interfaz.Balanza;
import com.service.Comunicacion.OnFragmentChangeListener;
import com.service.Modbus.Req.ModbusReqRtuMaster;
import com.service.PuertosSerie.PuertosSerie2;
import com.zgkxzx.modbus4And.requset.OnRequestBack;

import java.io.Serializable;
import java.math.BigDecimal;
import java.text.DecimalFormat;
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
    public String format(String numero) {
        String formato = "0.";

        try {
            StringBuilder capacidadBuilder = new StringBuilder(formato);
            for (int i = 0; i <puntoDecimal; i++) {
                capacidadBuilder.append("0");
            }
            formato = capacidadBuilder.toString();
            DecimalFormat df = new DecimalFormat(formato);
            String str = df.format(Double.parseDouble(numero));
            return str;
        } catch (NumberFormatException e) {
            System.err.println("Error: El número no es válido.");
            e.printStackTrace();
            return "0";
        }
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
        SharedPreferences preferences=mainActivity.getSharedPreferences(NOMBRE, Context.MODE_PRIVATE);
        return (preferences.getString(String.valueOf(numBza)+"_"+"unidad","kg"));
    }

    @Override
    public void init(int numBza) {
        System.out.println("OLA");
        estado=M_MODO_BALANZA;
        GET_PESO_cal_bza.run();
        pesoBandaCero= getBandaCeroValue(numBza);
        puntoDecimal=get_PuntoDecimal();
        ultimaCalibracion=get_UltimaCalibracion();

       // receiverinit();
        //if(serialPort!=null){

       // }
    }


Runnable GET_PESO_cal_bza = new Runnable() {

    @Override
    public void run() {
        if(subnombre==1){
            System.out.println("bucle works");
            try{
                final float[] response = {0.0F};
                OnRequestBack<short[]> callback = new OnRequestBack<short[]>() {
                    @Override
                    public void onSuccess(short[] result) {
                        // Maneja el resultado exitoso aquí
                        Bruto = result[0];
                        System.out.println("ITW410 Bruto:"+result[0]);
                        brutoStr= String.valueOf(Bruto);
                        Neto = result[1];
                        System.out.println("ITW410 Neto:"+result[1]);
                        netoStr= String.valueOf(Neto);
                        Tara = result[2];
                        System.out.println("ITW410 Tara:"+result[2]);
                        taraStr= String.valueOf(Tara);
                        estado410=result[3];
                        System.out.println("ESTADO 410:"+result[3]);
                    }

                    @Override
                    public void onFailed(String error) {
                        // Maneja el error aquí


                    }
                };
                ModbusRtuMaster.readHoldingRegisters(callback, numeroSlave, 20, 4);

                callback = new OnRequestBack<short[]>() {
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
                ModbusRtuMaster.readHoldingRegisters(callback, numeroSlave, 8, 3);
            }catch(Exception e){

            };
        }else if(subnombre==2){

            try{
                final float[] response = {0.0F};
                OnRequestBack<short[]> callback = new OnRequestBack<short[]>() {
                    @Override
                    public void onSuccess(short[] result) {
                        // Maneja el resultado exitoso aquí
                        Bruto = result[0];
                        System.out.println("ITW410 Bruto:"+Bruto);
                        brutoStr= String.valueOf(Bruto);
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
                        System.out.println("ITW410 neto:"+result[0]);
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
                        System.out.println("ITW410 Tara:"+Tara);
                        taraStr= String.valueOf(Tara);
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

            }catch(Exception e){

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
    public void openCalibracion(int numero) {
   //     mainActivity.MainClass.openFragment(new CalibracionOptimaFragment2_minima());
        //CalibracionMinimaFragment fragment = CalibracionMinimaFragment.newInstance(context, fragmentChangeListener);
        Bundle args = new Bundle();
        args.putSerializable("instance", context);
      //  fragmentChangeListener.openFragmentService(fragment,args);

    }

    @Override
    public Boolean getSobrecarga(int numBza) {
        return null;
    }


    private final ITW410 context;
    PuertosSerie2.PuertosSerie2Listener receiver =null;
    public final ModbusReqRtuMaster ModbusRtuMaster;
    public AppCompatActivity mainActivity;
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

    public ITW410(ModbusReqRtuMaster ModbusRtuMaster, int numBza, AppCompatActivity activity, OnFragmentChangeListener fragmentChangeListener, int numbza410) {
        this.ModbusRtuMaster = ModbusRtuMaster;
        this.numerobza = numBza;
        this.mainActivity = activity;
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
        SharedPreferences preferencias=mainActivity.getSharedPreferences("ITW410", Context.MODE_PRIVATE);
        SharedPreferences.Editor ObjEditor=preferencias.edit();
        ObjEditor.putFloat(String.valueOf(numeroSlave)+"_"+"pbandacero", peso);
        ObjEditor.apply();
    }

    public float getPesoBandaCero() {
        SharedPreferences preferences=mainActivity.getSharedPreferences("ITW410", Context.MODE_PRIVATE);
        return (preferences.getFloat(String.valueOf(numeroSlave)+"_"+"pbandacero",5.0F));
    }

    public String Cero(){
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

        return "KZERO\r\n";
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
        return "KTARE\r\n";
    }

    private  String Param(String str){ // NUEVO
        //85 = 10000101
        //String binario = "10000101"; // Número binario
        Integer decimal = Integer.parseInt(str, 2); // Convertir binario a decimal
        String hexadecimal = Integer.toHexString(decimal).toUpperCase(); // Convertir decimal a hexadecimal
        System.out.println("MINIMA ERRRR"+hexadecimal);
        while(hexadecimal.length()<2){
            hexadecimal="0"+hexadecimal;
        }
        return hexadecimal;
    }

    public void Pedirparam() { // NUEVO

        System.out.println("MINIMA ENVIADO");
        //algo se envia la wea esta y dsp => Thread.sleep(2500);
    }
    public String LeerParam1(){ // NUEVO
        String read="";
        String promvar="0";
        String offvar="0";
        String acuvar="0";
        String binario="00000000";

        System.out.println("MINIMA READING");

     /*   try {

            if(mainActivity.Puerto_A().HabilitadoLectura()){
                read=serialPort.read_2();
                System.out.println("MINIMA sa:"+read);
                if(read!=null){
                    ArrayList<String> listErr = Errores2(read);

                    if(listErr!=null){
                        for (int i = 0; i < listErr.size(); i++) {
                            System.out.println("MINIMA EERRRR"+listErr.get(i));
                           // mainActivity.Mensaje(listErr.get(i), R.layout.item_customtoasterror);
                        }
                    }
                    if(read.contains("\u0006O 13")){

                        String hex= read.substring(read.indexOf("\u0006O 13")+5,read.indexOf("\u0006O 13")+7);
                        promvar = read.substring(read.indexOf("\u0006O 13")+34,read.indexOf("\u0006O 13")+35);
                        offvar = read.substring(read.indexOf("\u0006O 13")+33,read.indexOf("\u0006O 13")+34);
                        acuvar = read.substring(read.indexOf("\u0006O 13")+35,read.indexOf("\u0006O 13")+36);

                        System.out.println("MINIMA:"+hex+ " "+ promvar);// necesito leer de la pos 2 sin contar el #006  asta pos 4
                        int decimal = Integer.parseInt(hex, 16); // Convertir hexadecimal a decimal
                        binario = Integer.toBinaryString(decimal); // Convertir decimal a binario
                        while (binario.length() < 8) {
                            binario = "0" + binario;
                        }

                    }


                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }*/

        return  binario+" "+promvar+offvar+acuvar;
    }

    public String EnviarParametros(String param1, String param2,String sp_prostr,String sp_offstr,String sp_acustr){ //NUEVO
        String Parametros = "\u0005P";
        Parametros+=Param(param1); // Param1 Pf
        System.out.println("MINIMA:"+Parametros.toString());
        Parametros+=Param(param2); // Param2 pd
        Parametros+=sp_offstr;//"0";//#005P8200F000050000#013 1382000027100172E0001C3F810900F000050000 off
        Parametros+=sp_prostr;//classcalibradora.sp_pro.getSelectedItem().toString();//"0";//#005P82000F00050000#013 1382000027100172E0001C3F8109000F00050000 pro
        Parametros+=sp_acustr;//"0";//#005P820000F0050000#013 13820000// 27100172E0001C3F81090000F0050000 acu
        Parametros+="0";//#005P8200000F050000#013 1382000027100172E0001C3F810900000F050000 bf1
        Parametros+="0";//#005P82000000F50000#013 1382000027100172E0001C3F8109000000F50000 dat
        Parametros+="5";//#005P820000000F0000#013 1382000027100172E0001C3F81090000000F0000 bps
        Parametros+="0";//#005P8200000005F000#013 1382000027100172E0001C3F810900000005F000 ano
        Parametros+="0";//#005P82000000050F00#013 1382000027100172E0001C3F8109000000050F00 uni
        Parametros+="0";//#005P820000000500F0#013 1382000027100172E0001C3F81090000000500F0 reg
        Parametros+="0";//#005P8200000005000F#013 1382000027100172E0001C3F810900000005000F bot
        Parametros+="\r";
        System.out.println("MINIMA:"+Parametros.toString());


        return  Parametros;
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
            ModbusRtuMaster.writeCoil(callback,numeroSlave,11,true);

        return "\u0005S\r";
    }
    public String reset(){
        return "\u0005T\r";
    }
    public String Consultar_configuracion_memoria(){
        return "\u0005O\r";
    }


    public String ReAjusteCero(){
        return "\u0005M\r";
    }





    public String Peso_conocido(String pesoconocido,String PuntoDecimal){
        String pesoConocido = "?????";
        if(subnombre==1) {
            OnRequestBack<String> callback = new OnRequestBack<String>() {
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
            ModbusRtuMaster.writeCoil(callback, numeroSlave, 6, true);
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
            ModbusRtuMaster.writeCoil(callback,numeroSlave,9,true);
        }

        return "\u0005L"+pesoConocido+"\r";
    }
    public String Cero_cal(){
        if(subnombre==1) {
            OnRequestBack<String> callback = new OnRequestBack<String>() {
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
            ModbusRtuMaster.writeCoil(callback, numeroSlave, 5, true);
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
            ModbusRtuMaster.writeCoil(callback,numeroSlave,8,true);
        }
        return "\u0005U\r";
    }
    public String Recero_cal(){
        if(subnombre==1) {
            OnRequestBack<String> callback = new OnRequestBack<String>() {
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
            ModbusRtuMaster.writeCoil(callback, numeroSlave, 7, true);
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
            ModbusRtuMaster.writeCoil(callback,numeroSlave,10,true);
        }
        return "\u0005Z\r";
    }
    public String CapacidadMax_DivMin_PDecimal(String capacidad, String DivMin, String PuntoDecimal){
        if(capacidad.length()+Integer.parseInt(PuntoDecimal)>5){
            return null;
        }
        StringBuilder capacidadBuilder = new StringBuilder(capacidad);
        for(int i=0;i<Integer.parseInt(PuntoDecimal);i++){
            capacidadBuilder.append("0");
        }
        capacidad = capacidadBuilder.toString();
        if(capacidad.length()<5){
            StringBuilder capacidadBuilder1 = new StringBuilder(capacidad);
            while(capacidadBuilder1.length()!=5){
                capacidadBuilder1.insert(0, "0");
            }
            capacidad = capacidadBuilder1.toString();
        }
        return "\u0005D"+capacidad+"0"+DivMin+""+PuntoDecimal+"\r";
    }

    public void setPesoUnitario(float peso){
        pesoUnitario=peso;
        SharedPreferences preferencias=mainActivity.getSharedPreferences("ITW410", Context.MODE_PRIVATE);
        SharedPreferences.Editor ObjEditor=preferencias.edit();
        ObjEditor.putFloat(String.valueOf(numeroSlave)+"_"+"punitario", 0.5F);
        ObjEditor.apply();
    }

    public float getPesoUnitario() {
        SharedPreferences preferences=mainActivity.getSharedPreferences("ITW410", Context.MODE_PRIVATE);
        return (preferences.getFloat(String.valueOf(numeroSlave)+"_"+"punitario",0.5F));
    }


    public void setUnidad(String Unidad){
        SharedPreferences preferencias=mainActivity.getSharedPreferences("ITW410", Context.MODE_PRIVATE);
        SharedPreferences.Editor ObjEditor=preferencias.edit();
        ObjEditor.putString(String.valueOf(numeroSlave)+"_"+"unidad",Unidad);
        ObjEditor.apply();
    }

    public String getUnidad() {
        //Trae la unidad producto guardado en memoria
        SharedPreferences preferences=mainActivity.getSharedPreferences("ITW410", Context.MODE_PRIVATE);

        //System.out.println("MINIMA ERR UNIDAD:"+preferences.toString()+" "+numero);
        return (preferences.getString(String.valueOf(numeroSlave)+"_"+"unidad","kg"));
    }

    public void set_DivisionMinima(int divmin){

        SharedPreferences Preferencias=mainActivity.getSharedPreferences("ITW410",Context.MODE_PRIVATE);
        SharedPreferences.Editor ObjEditor=Preferencias.edit();
        ObjEditor.putInt(String.valueOf(numeroSlave)+"_"+"div",divmin);
        ObjEditor.apply();

    }
    public void set_PuntoDecimal(int puntoDecimal){

        SharedPreferences Preferencias=mainActivity.getSharedPreferences("ITW410",Context.MODE_PRIVATE);
        SharedPreferences.Editor ObjEditor=Preferencias.edit();
        ObjEditor.putInt(String.valueOf(numeroSlave)+"_"+"pdecimal",puntoDecimal);
        ObjEditor.apply();

    }
    public void set_UltimaCalibracion(String ucalibracion){

        SharedPreferences Preferencias=mainActivity.getSharedPreferences("ITW410",Context.MODE_PRIVATE);
        SharedPreferences.Editor ObjEditor=Preferencias.edit();
        ObjEditor.putString(String.valueOf(numeroSlave)+"_"+"ucalibracion",ucalibracion);
        ObjEditor.apply();

    }
    public String get_UltimaCalibracion(){
        SharedPreferences Preferencias=mainActivity.getSharedPreferences("ITW410",Context.MODE_PRIVATE);
        return Preferencias.getString(String.valueOf(numeroSlave)+"_"+"ucalibracion","");

    }
    public void set_CapacidadMax(String capacidad){

        SharedPreferences Preferencias=mainActivity.getSharedPreferences("ITW410",Context.MODE_PRIVATE);
        SharedPreferences.Editor ObjEditor=Preferencias.edit();
        ObjEditor.putString(String.valueOf(numeroSlave)+"_"+"capacidad",capacidad);
        ObjEditor.apply();

    }
    public void set_PesoConocido(String pesoConocido){

        SharedPreferences Preferencias=mainActivity.getSharedPreferences("ITW410",Context.MODE_PRIVATE);
        SharedPreferences.Editor ObjEditor=Preferencias.edit();
        ObjEditor.putString(String.valueOf(numeroSlave)+"_"+"pconocido",pesoConocido);
        ObjEditor.apply();

    }

    public int get_DivisionMinima(){
        SharedPreferences Preferencias=mainActivity.getSharedPreferences("ITW410",Context.MODE_PRIVATE);
        return Preferencias.getInt(String.valueOf(numeroSlave)+"_"+"div",0);

    }
    public int get_PuntoDecimal(){
        SharedPreferences Preferencias=mainActivity.getSharedPreferences("ITW410",Context.MODE_PRIVATE);
        int lea=Preferencias.getInt(String.valueOf(numeroSlave)+"_"+"pdecimal",1);
        System.out.println("MINIMA CALIBRACION PUNTO DECIMAL: "+String.valueOf(lea));
        return lea;

    }
    public String get_CapacidadMax(){
        SharedPreferences Preferencias=mainActivity.getSharedPreferences("ITW410",Context.MODE_PRIVATE);
        return Preferencias.getString(String.valueOf(numeroSlave)+"_"+"capacidad","100");
    }
    public String get_PesoConocido(){
        SharedPreferences Preferencias=mainActivity.getSharedPreferences("ITW410",Context.MODE_PRIVATE);
        return Preferencias.getString(String.valueOf(numeroSlave)+"_"+"pconocido","100");
    }

    public void stopRuning(){
        estado=M_MODO_CALIBRACION;
    }
    public void startRuning(){
        estado=M_MODO_BALANZA;
    }

    public float redondear(float numero) {
        float factor = (float) Math.pow(10, puntoDecimal);
        return Math.round(numero * factor) / factor;
    }

    public static String removeLeadingZeros(BigDecimal number) {
        String formatted = number.toPlainString();
        if (formatted.contains(".")) {
            // Elimina los ceros innecesarios al principio antes del punto decimal
            formatted = formatted.replaceFirst("^0+(?!\\.)", "");
            // Si todos los dígitos antes del punto decimal eran ceros, agrega uno
            if (formatted.matches("^\\..*")) {
                formatted = "0" + formatted;
            }
        }
        return formatted;
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
        return false;
    }

    @Override
    public void onEvent() {

    }

    @Override
    public void Itw410FrmSetear(int numero, String setPoint, int Salida) {
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
        ModbusRtuMaster.writeRegister(callback,numeroSlave,29,Integer.parseInt(setPoint));

    }

    @Override
    public String Itw410FrmGetSetPoint(int numero) {
        final short[][] res = {new short[0]};

        CountDownLatch latch = new CountDownLatch(1);
        OnRequestBack<short[]> callback = new OnRequestBack<short[]>() {
            @Override
            public void onSuccess(short[] result) {
                // Maneja el resultado exitoso aquí
                res[0] =result;
                latch.countDown();
                System.out.println("ITW410 setpoint:"+result[0]);
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
        return Integer.toString(res[0][0]);
    }

    @Override
    public int Itw410FrmGetSalida(int numero) {
        final short[][] res = {new short[0]};
        CountDownLatch latch = new CountDownLatch(1);
        OnRequestBack<short[]> callback = new OnRequestBack<short[]>() {
            @Override
            public void onSuccess(short[] result) {
                // Maneja el resultado exitoso aquí
                res[0] =result;
                latch.countDown();
                System.out.println("ITW410 salida:"+result[0]);
            }

            @Override
            public void onFailed(String error) {
                // Maneja el error aquí
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
        return res[0][0];
    }

    @Override
    public void Itw410FrmStart(int numero) {
        final String[] res = {""};
        OnRequestBack<String> callback = new OnRequestBack<String>() {
            @Override
            public void onSuccess(String result) {
                // Maneja el resultado exitoso aquí
                System.out.println("itw 410 arranque");
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
        OnRequestBack<short[]> callback = new OnRequestBack<short[]>() {
            @Override
            public void onSuccess(short[] result) {
                // Maneja el resultado exitoso aquí
                res[0] =result;
                latch.countDown();
                System.out.println("ITW410 ultimo peso:"+result);
            }

            @Override
            public void onFailed(String error) {
                // Maneja el error aquí

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

        return Integer.toString(res[0][0]);
    }

    @Override
    public int Itw410FrmGetUltimoIndice(int numero) {
        final short[][] res = {new short[0]};

        final CountDownLatch latch = new CountDownLatch(1);
        OnRequestBack<short[]> callback = new OnRequestBack<short[]>() {
            @Override
            public void onSuccess(short[] result) {
                // Maneja el resultado exitoso aquí
                res[0] =result;
                latch.countDown(); // Libera el lock
                System.out.println("ITW410 ultimo indice:"+result);
            }

            @Override
            public void onFailed(String error) {
                // Maneja el error aquí

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

        return res[0][0];
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
        System.out.println("FUNCA ITW410");
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
        System.out.println("TARA? "+1+numeroSlave+subnombre);
        if(ModbusRtuMaster !=null){
            if(subnombre==1) {
                OnRequestBack<String> callback = new OnRequestBack<String>() {
                    @Override
                    public void onSuccess(String result) {
                        // Maneja el resultado exitoso aquí
                        System.out.println("ITW410 TARADA:"+result);

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

    @Override
    public void setCero(int numBza) {
        if(ModbusRtuMaster !=null){


         }
        taraDigitalStr="0";
    }

    @Override
    public void setTaraDigital(int numBza, float TaraDigital) {
        setTara(numBza);
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
        SharedPreferences preferences=mainActivity.getSharedPreferences(NOMBRE, Context.MODE_PRIVATE);
        return (preferences.getFloat(String.valueOf(numBza)+"_"+"pbandacero",5.0F));
    }
    @Override
    public void setBandaCeroValue(int numBza, float bandaCeroValue) {
        pesoBandaCero=bandaCeroValue;
        SharedPreferences preferencias=mainActivity.getSharedPreferences(NOMBRE, Context.MODE_PRIVATE);
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

