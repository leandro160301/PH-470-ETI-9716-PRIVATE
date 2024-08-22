package com.service.Balanzas.Clases;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;
import com.service.Balanzas.BalanzaService;
import com.service.Balanzas.Fragments.CalibracionOptimaFragment;
import com.service.Balanzas.Fragments.ServiceFragment;
import com.service.Balanzas.Interfaz.Balanza;
import com.service.PuertosSerie.PuertosSerie;
import com.service.Utils;
import java.io.IOException;
import java.io.Serializable;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Objects;

public class OPTIMA_I implements Balanza, Balanza.Struct, Serializable {

    /** si ponemos tara digital, entonces toma la tara como tara digital,
     * si le damos a tara normal la tara digital pasa a cero
     */
    public PuertosSerie serialPort;
    Handler mHandler= new Handler();
    public String estado="VERIFICANDO_MODO";
    public static final String NOMBRE="OPTIMA";
    public static final String M_VERIFICANDO_MODO="VERIFICANDO_MODO";
    public static final String M_MODO_BALANZA="MODO_BALANZA";
    public static final String M_MODO_CALIBRACION="MODO_CALIBRACION";
    public static final String M_ERROR_COMUNICACION="M_ERROR_COMUNICACION";
    public static final String M_DETENIDO="M_DETENIDO";
    public float taraDigital=0,Bruto=0,Tara=0,Neto=0,pico=0;
    public String estableStr ="";
    float pesoBandaCero=0F;
    public Boolean bandaCero =true;
    public Boolean estable=false;
    public Boolean inicioBandaPeso=false;
    public int puntoDecimal=1;
    public String ultimaCalibracion="";
    public String brutoStr="0",netoStr="0",taraStr="0",taraDigitalStr="0",picoStr="0";
    public int acumulador=0;
    public int numero=1;
    public AppCompatActivity activity;
    public OPTIMA_I context;
    public BalanzaService service;

    public OPTIMA_I(PuertosSerie serialPort, int numero, AppCompatActivity activity, BalanzaService service) {
        this.serialPort = serialPort;
        this.numero= numero;
        this.activity = activity;
        this.service = service;
        context=this;
    }

    public void start(){
        estado=M_MODO_BALANZA;
    }

    public void stop(){
        estado=M_DETENIDO;
    }


    public String Guardar_cal(){
        return "\u0005S\r";
    }
    public String Consultar_configuracion_memoria(){
        return "\u0005O\r";
    }
    public String ReAjusteCero(){
        return "\u0005M\r";
    }
    public String Peso_conocido(String pesoconocido,String PuntoDecimal){
            if(pesoconocido.length()+Integer.parseInt(PuntoDecimal)>5){
                return null;
            }
            StringBuilder capacidadBuilder = new StringBuilder(pesoconocido);
            for(int i=0;i<Integer.parseInt(PuntoDecimal);i++){
                capacidadBuilder.append("0");
            }
            pesoconocido = capacidadBuilder.toString();
            if(pesoconocido.length()<5){
                StringBuilder capacidadBuilder1 = new StringBuilder(pesoconocido);
                while(capacidadBuilder1.length()!=5){
                    capacidadBuilder1.insert(0, "0");
                }
                pesoconocido = capacidadBuilder1.toString();
            }
            System.out.println("OPTIMA:PESO CONOCIDO:"+pesoconocido);
            return "\u0005L"+pesoconocido+"\r";
        }
    public String Cero_cal(){
        return "\u0005U\r";
    }
    public String Recero_cal(){
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
        System.out.println("OPTIMA:CAPMAXDIVMINPDECIMAL:"+capacidad+"0"+DivMin+""+PuntoDecimal);
        return "\u0005D"+capacidad+"0"+DivMin+""+PuntoDecimal+"\r";
    }
    public static String Salir_cal(){
        return "\u0005E \r";
    }
    public String Errores(String lectura){
        if(lectura!=null){
            if(lectura.charAt(0)==6&&lectura.charAt(2)!=32){
                String Error="";
                switch (lectura.charAt(1)) {
                    case 'C':
                        Error="C_CAL_";
                        break;
                    case 'S':
                        Error="S_SAVE_";
                        break;
                    case 'P':
                        Error="P_PARAM_";
                        break;
                    case 'D':
                        Error="D_CAPMAX_";
                        break;
                    case 'U':
                        Error="U_CERO_";
                        break;
                    case 'L':
                        Error="L_CARGA";
                        break;
                    case 'Z':
                        Error="Z_FIN";
                        break;
                    case 'M':
                        Error="M_RECERO";
                        break;
                    case 'R':
                        Error="R_RELOJ";
                        break;
                    case 'A':
                        Error="A_DAC";
                        break;
                    case 'I':
                        Error="I_I.D_";
                        break;
                    case 'O':
                        Error="O_OPTIONS";
                        break;
                    default:
                        return null;
                }

                switch (lectura.charAt(2)) {
                    case 'a':
                        Error=Error+"ERR AJUSTE";
                        break;
                    case 'b':
                        Error=Error+"BAD LEN COMMNAD";
                        break;
                    case 'c':
                        Error=Error+"ERR CERO";
                        break;
                    case 'd':
                        Error=Error+"ERR PARTES";
                        break;
                    case 'e':
                        Error=Error+"ERR ESCRITURA EEPROM";
                        break;
                    case 'f':
                        Error=Error+"BAD ASCII_CHARACTER";
                        break;
                    case 'g':
                        Error=Error+"NOT CAP.MAX.";
                        break;
                    case 'h':
                        Error=Error+"NOT CAP.MAX./INICIAL";
                        break;
                    case 'i':
                        Error=Error+"NOT CAP.MAX./INICIAL/PES.PAT./SPAN_FINAL";
                        break;
                    case 'j':
                        Error=Error+"NOT END CALIB";
                        break;
                    case 'k':
                        Error=Error+"NOT DEVICE HABILITADO";
                        break;
                    case 'l':
                        Error=Error+"ERR LECTURA EEPROM";
                        break;
                    case 'p':
                        Error=Error+"ERR PESO PATRON";
                        break;
                    default:
                        return null;
                }
                return Error;
            }
        }
        return null;
    }
    public String Error_a(){
        return "ERR AJUSTE";
    }
    public String Error_b(){
        return "BAD LEN COMMNAD";
    }
    public String Error_c(){
        return "ERR CERO";
    }
    public String Error_d(){
        return "ERR PARTES";
    }
    public String Error_e(){
        return "ERR ESCRITURA EEPROM";
    }
    public String Error_f(){
        return "BAD ASCII_CHARACTER";
    }
    public String Error_g(){
        return "NOT CAP.MAX.";
    }
    public String Error_h(){
        return "NOT CAP.MAX./INICIAL";
    }
    public String Error_i(){
        return "NOT CAP.MAX./INICIAL/PES.PAT./SPAN_FINAL";
    }
    public String Error_j(){
        return "NOT END CALIB";
    }
    public String Error_k(){
        return "NOT DEVICE HABILITADO";
    }
    public String Error_l(){
        return "ERR LECTURA EEPROM";
    }
    public String Error_P(){
        return "ERR PESO PATRON";
    }



    public void setUnidad(String Unidad){
        SharedPreferences preferencias=activity.getSharedPreferences(NOMBRE, Context.MODE_PRIVATE);
        SharedPreferences.Editor ObjEditor=preferencias.edit();
        ObjEditor.putString(String.valueOf(numero)+"_"+"unidad",Unidad);
        ObjEditor.apply();
    }

    public void set_DivisionMinima(int divmin){

        SharedPreferences Preferencias=activity.getSharedPreferences(NOMBRE,Context.MODE_PRIVATE);
        SharedPreferences.Editor ObjEditor=Preferencias.edit();
        ObjEditor.putInt(String.valueOf(numero)+"_"+"div",divmin);
        ObjEditor.apply();

    }
    public void set_PuntoDecimal(int puntoDecimal){

        SharedPreferences Preferencias=activity.getSharedPreferences(NOMBRE,Context.MODE_PRIVATE);
        SharedPreferences.Editor ObjEditor=Preferencias.edit();
        ObjEditor.putInt(String.valueOf(numero)+"_"+"pdecimal",puntoDecimal);
        ObjEditor.apply();

    }
    public void set_UltimaCalibracion(String ucalibracion){

        SharedPreferences Preferencias=activity.getSharedPreferences(NOMBRE,Context.MODE_PRIVATE);
        SharedPreferences.Editor ObjEditor=Preferencias.edit();
        ObjEditor.putString(String.valueOf(numero)+"_"+"ucalibracion",ucalibracion);
        ObjEditor.apply();

    }
    public String get_UltimaCalibracion(){
        SharedPreferences Preferencias=activity.getSharedPreferences(NOMBRE,Context.MODE_PRIVATE);
        return Preferencias.getString(String.valueOf(numero)+"_"+"ucalibracion","");

    }
    public void set_CapacidadMax(String capacidad){

        SharedPreferences Preferencias=activity.getSharedPreferences(NOMBRE,Context.MODE_PRIVATE);
        SharedPreferences.Editor ObjEditor=Preferencias.edit();
        ObjEditor.putString(String.valueOf(numero)+"_"+"capacidad",capacidad);
        ObjEditor.apply();

    }
    public void set_PesoConocido(String pesoConocido){

        SharedPreferences Preferencias=activity.getSharedPreferences(NOMBRE,Context.MODE_PRIVATE);
        SharedPreferences.Editor ObjEditor=Preferencias.edit();
        ObjEditor.putString(String.valueOf(numero)+"_"+"pconocido",pesoConocido);
        ObjEditor.apply();

    }

    public int get_DivisionMinima(){
        SharedPreferences Preferencias=activity.getSharedPreferences(NOMBRE,Context.MODE_PRIVATE);
        return Preferencias.getInt(String.valueOf(numero)+"_"+"div",0);

    }
    public int get_PuntoDecimal(){
        SharedPreferences Preferencias=activity.getSharedPreferences(NOMBRE,Context.MODE_PRIVATE);
        int lea=Preferencias.getInt(String.valueOf(numero)+"_"+"pdecimal",1);
        System.out.println("OPTIMA CALIBRACION PUNTO DECIMAL: "+String.valueOf(lea));
        return lea;

    }
    public String get_CapacidadMax(){
        SharedPreferences Preferencias=activity.getSharedPreferences(NOMBRE,Context.MODE_PRIVATE);
        return Preferencias.getString(String.valueOf(numero)+"_"+"capacidad","100");
    }
    public String get_PesoConocido(){
        SharedPreferences Preferencias=activity.getSharedPreferences(NOMBRE,Context.MODE_PRIVATE);
        return Preferencias.getString(String.valueOf(numero)+"_"+"pconocido","100");
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


    public Runnable GET_PESO_cal_bza= new Runnable() {

        int contador=0;
        String read="",read2="";
        String[] array;
        @Override
        public void run() {

            if(!Objects.equals(estado, M_MODO_CALIBRACION)){
                    try {
                        if(serialPort.HabilitadoLectura()){
                            System.out.println("OPTIMA HABILITADO LECTURA");
                            read=serialPort.read_2();
                            String filtro="\r\n";
                            // read=read.replace("\r\n","");

                            if(read!=null){
                                System.out.println("OPTIMA: "+read);
                                if(read.toLowerCase().contains(filtro.toLowerCase())){
                                    estado=M_MODO_BALANZA;
                                    boolean es=false;
                                    if(read.toLowerCase().contains("E".toLowerCase())){
                                        estableStr ="E";
                                        estable=true;
                                        es=true;
                                    }else if(read.toLowerCase().contains("S".toLowerCase())){
                                        estableStr ="S";
                                        estable=false;
                                        es=true;
                                    }else{
                                        estableStr ="";
                                        //estable=false;
                                    }
                                    if(!es){
                                        estable=false;
                                    }


                                    array= read.split(filtro);

                                    if(array.length>0){

                                        read2=array[0];
                                        read2=read2.replace(" ","");
                                        read2=read2.replace("\r\n","");
                                        read2=read2.replace("\r","");
                                        read2=read2.replace("\n","");
                                        read2=read2.replace("\\u0007","");
                                        read2=read2.replace("O","");
                                        read2=read2.replace("E","");
                                        read2=read2.replace("kg","");
                                        read=read2.replace(".","");

                                        if(Utils.isNumeric(read2)){
                                            int index = read2.indexOf('.'); // Busca el índice del primer punto en la cadena
                                            puntoDecimal = read.length() - index;
                                            // uso bigdecimal porque si restaba me daba numeros raros detras de la coma
                                            brutoStr=read2;
                                            BigDecimal number = new BigDecimal(brutoStr);
                                            brutoStr = removeLeadingZeros(number);
                                            Bruto=Float.parseFloat(read2);

                                            //Bruto= redondear(Bruto);
                                            //muestreoinstantaneo= bbruto.floatValue();
                                            if(taraDigital==0){
                                                Neto=Bruto-Tara;
                                                //Neto= redondear(Neto);
                                                netoStr=String.valueOf(Neto);
                                                if(index==-1){
                                                    netoStr=netoStr.replace(".0","");
                                                }
                                            }else{
                                                Neto=Bruto-taraDigital;
                                                //Neto= redondear(Neto);
                                                netoStr=String.valueOf(Neto);
                                                if(index==-1){
                                                    netoStr=netoStr.replace(".0","");
                                                }
                                            }
                                            if(index!=-1&&puntoDecimal>0){
                                                String formato="0.";

                                                StringBuilder capacidadBuilder = new StringBuilder(formato);
                                                for(int i=0;i<puntoDecimal;i++){
                                                    capacidadBuilder.append("0");
                                                }
                                                formato = capacidadBuilder.toString();
                                                DecimalFormat df = new DecimalFormat(formato);
                                                netoStr = df.format(Neto);
                                                taraDigitalStr = df.format(taraDigital);
                                                //taraStr = df.format(ta);
                                            }
                                            if(Neto>pico){
                                                pico=Neto;
                                                picoStr=netoStr;
                                            }

                                            if(Bruto<pesoBandaCero){
                                                bandaCero =true;
                                            }
                                            else{
                                                if(inicioBandaPeso){
                                                    bandaCero =false;
                                                }

                                            }
                                            acumulador++;

                                        }
                                        read="";

                                    }

                                }else if (read.contains("\u0006C \r")){
                                    //entro a calibracion
                                    System.out.println("OPTIMA:CALIBRACION");
                                    openService();
                                }
                            }
                        }
                        else if (contador<=8&& Objects.equals(estado, M_VERIFICANDO_MODO)) {
                            if (contador == 0) {
                                System.out.println("OPTIMA:BUSCANDO CALIBRACION");
                                serialPort.write("\u0006C \r");
                            } else {
                                System.out.println("OPTIMA:BUSCANDO CALIBRACION");
                                serialPort.write("\u0005C \r");
                            }

                            contador++;
                        }

                        if (contador==8){

                       //     mainActivity.Mensaje(M_ERROR_COMUNICACION, R.layout.item_customtoasterror);
                            contador++;
                        }


                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    mHandler.postDelayed(GET_PESO_cal_bza,200);

            }
            else {
                mHandler.postDelayed(GET_PESO_cal_bza,50);
            }
        }
    };

    @Override
    public float getNeto(int numero) {
        return Neto;
    }

    @Override
    public String getNetoStr(int numero) {
        return netoStr;
    }

    @Override
    public float getBruto(int numero) {
        return Bruto;
    }

    @Override
    public String getBrutoStr(int numero) {
        return brutoStr;
    }

    @Override
    public float getTara(int numero) {
        return 0;
    }

    @Override
    public String getTaraStr(int numero) {
        return "0";
    }

    @Override
    public void setTara(int numero) {
        if(serialPort!=null){
            serialPort.write("KTARE\r");
        }
    }

    @Override
    public void setCero(int numero) {
        if(serialPort!=null){
            serialPort.write("KZERO\r\n");
        }
        taraDigitalStr="0";
        taraDigital=0;
    }
    @Override
    public void setTaraDigital(int numero,float TaraDigital) {
        taraDigital = TaraDigital;
        taraDigitalStr=String.valueOf(TaraDigital);
    }

    @Override
    public String getTaraDigital(int numero) {
        return taraDigitalStr;
    }

    @Override
    public void setBandaCero(int numero,Boolean bandaCeroi) {
        bandaCero=bandaCeroi;
    }

    @Override
    public Boolean getBandaCero(int numero) {
        return bandaCero;
    }

    @Override
    public float getBandaCeroValue(int numero) {
        SharedPreferences preferences=activity.getSharedPreferences(NOMBRE, Context.MODE_PRIVATE);
        return (preferences.getFloat(String.valueOf(numero)+"_"+"pbandacero",5.0F));
    }

    @Override
    public void setBandaCeroValue(int numero,float bandaCeroValue) {
        pesoBandaCero=bandaCeroValue;
        SharedPreferences preferencias=activity.getSharedPreferences(NOMBRE, Context.MODE_PRIVATE);
        SharedPreferences.Editor ObjEditor=preferencias.edit();
        ObjEditor.putFloat(String.valueOf(numero)+"_"+"pbandacero",bandaCeroValue);
        ObjEditor.apply();
    }

    @Override
    public Boolean getEstable(int numero) {
        return estable;
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
    public String getUnidad(int numero) {
        SharedPreferences preferences=activity.getSharedPreferences(NOMBRE, Context.MODE_PRIVATE);
        return (preferences.getString(String.valueOf(numero)+"_"+"unidad","kg"));
    }

    @Override
    public String getPicoStr(int numero) {
        return picoStr;
    }

    @Override
    public float getPico(int numero) {
        return pico;
    }

    @Override
    public void init(int numero) {
        context=this;
        estado=M_VERIFICANDO_MODO;
        pesoBandaCero= getBandaCeroValue(numero);
        puntoDecimal=get_PuntoDecimal();
        ultimaCalibracion=get_UltimaCalibracion();
        if(serialPort!=null){
            GET_PESO_cal_bza.run();
        }
    }

    @Override
    public void stop(int numero) {

    }

    @Override
    public void start(int numero) {
        estado=M_MODO_BALANZA;
    }

    @Override
    public void openCalibracion(int numero) {
        estado=M_MODO_CALIBRACION;
        CalibracionOptimaFragment fragment = CalibracionOptimaFragment.newInstance(context,service);
        Bundle args = new Bundle();
        args.putSerializable("instance", context);
        args.putSerializable("instanceService", service);
        service.fragmentChangeListener.openFragmentService(fragment,args);

    }

    public void openService() {
        estado=M_MODO_CALIBRACION;
        ServiceFragment fragment = ServiceFragment.newInstance(service);
        Bundle args = new Bundle();
        args.putSerializable("instanceService", service);
        service.fragmentChangeListener.openFragmentService(fragment,args);

    }

    @Override
    public Boolean getSobrecarga(int numero) {
        return false;
    }

    @Override
    public String getEstado(int numero) {
        return estado;
    }

    @Override
    public void setEstado(int numero, String estado) {
        this.estado=estado;
    }

    @Override
    public void itw410FrmSetear(int numero, String setPoint, int Salida) {

    }

    @Override
    public String itw410FrmGetSetPoint(int numero) {
        return null;
    }

    @Override
    public int itw410FrmGetSalida(int numero) {
        return 0;
    }

    @Override
    public void itw410FrmStart(int numero) {

    }

    @Override
    public void itw410FrmPause(int numero) {

    }

    @Override
    public void itw410FrmStop(int numero) {

    }

    @Override
    public int itw410FrmGetEstado(int numero) {
        return 0;
    }


    @Override
    public String itw410FrmGetUltimoPeso(int numero) {
        return null;
    }

    @Override
    public int itw410FrmGetUltimoIndice(int numero) {
        return 0;
    }

}
