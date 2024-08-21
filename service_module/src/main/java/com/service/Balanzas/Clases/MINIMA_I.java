package com.service.Balanzas.Clases;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;

import com.service.Balanzas.BalanzaService;
import com.service.Balanzas.Fragments.CalibracionMinimaFragment;
import com.service.Balanzas.Fragments.CalibracionOptimaFragment;
import com.service.Balanzas.Fragments.ServiceFragment;
import com.service.Balanzas.Interfaz.Balanza;
import com.service.Utils;
import com.service.PuertosSerie.PuertosSerie;
import java.io.IOException;
import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.Objects;

public class MINIMA_I implements Balanza, Balanza.Struct, Serializable {


    /** si ponemos tara digital, entonces toma la tara como tara digital,
     * si le damos a tara normal la tara digital pasa a cero y la tara es la tara
     *
     *
     */
    //PARA CONTADORA #005P03000102050000#013 TERMINAL
    public PuertosSerie serialPort;
    Handler mHandler= new Handler();
    public static final String NOMBRE="MINIMA";
    public String estado="VERIFICANDO_MODO";
    private static final String CONSULTA_PUNITARIO="XU2\r";//??????
    private static final String CONSULTA_PIEZAS="XP2\r";
    private static final String CONSULTA_BRUTO="XG2\r";
    private static final String CONSULTA_NETO="XN2\r";
    public static final String M_VERIFICANDO_MODO="VERIFICANDO_MODO";
    public static final String M_MODO_BALANZA="MODO_BALANZA";
    public static final String M_MODO_CALIBRACION="MODO_CALIBRACION";
    public static final String M_ERROR_COMUNICACION="M_ERROR_COMUNICACION";
    public static final String M_DETENIDO="M_DETENIDO";
    public float taraDigital=0,Bruto=0,Tara=0,Neto=0;
    public String estableStr ="";
    public Boolean estable=false;
    float pesoUnitario=0.5F;
    float pesoBandaCero=0F;
    public int piezas=0;
    public Boolean contador =false;
    int runnableIndice=0;
    public Boolean bandaCero =true,stopcomunicacion=false;
    public Boolean inicioBandaPeso=false;
    public int puntoDecimal=1;
    public String ultimaCalibracion="",calculoPesoUnitario="Error";
    public String brutoStr="0",netoStr="0",taraStr="0",taraDigitalStr="0";
    public int acumulador=0;
    public int numero=1;
    public AppCompatActivity activity;
    public MINIMA_I context;
    public BalanzaService service;

    public MINIMA_I( PuertosSerie serialPort, int numero, AppCompatActivity activity, BalanzaService service) {
        this.serialPort = serialPort;
        this.numero= numero;
        this.activity = activity;
        this.service = service;
        context=this;
    }

    public void init(){
        estado=M_VERIFICANDO_MODO;
        pesoUnitario=getPesoUnitario();
        pesoBandaCero= getBandaCeroValue(numero);
        puntoDecimal=get_PuntoDecimal();
        ultimaCalibracion=get_UltimaCalibracion();
        if(serialPort!=null){
            if(contador){
                setPesoUnitario(pesoUnitario);
                sendPuntoDecimal();
                GET_PESO_cal_bza_Contador.run();
            }else{
                GET_PESO_cal_bza.run();
            }

        }
    }
    public void start(){
        estado=M_MODO_BALANZA;
    }

    public void stop(){
        estado=M_DETENIDO;
    }

    public void sendPuntoDecimal(){
        String punto=String.valueOf(get_PuntoDecimal());
        System.out.println("ENVIANDO PUNTO DECIMAL: "+punto);
        serialPort.write("Pdu"+punto+"\r");

    }

    public void ConsultaPesoUnitario(){
        serialPort.write(CONSULTA_PUNITARIO);
    }
    public String Guardar_cal(){
        return "\u0005S\r";
    }
    public String Consultar_configuracion_memoria(){
        return "\u0005O\r";
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
        return "\u0005D"+capacidad+"0"+DivMin+""+PuntoDecimal+"\r";
    }
    public String Salir_cal(){
        return "\u0005E \r";
    }
    public void setStopcomunicacion() throws IOException {
        stopcomunicacion=true;
        if(serialPort.HabilitadoLectura()){
            serialPort.read_2();
        }
    }
    public String ReAjusteCero(){
        return "\u0005M\r";
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



    public void setCantPiezas(int piezas){
        String muestras=completarFormato2(String.valueOf(piezas));
        serialPort.write("YMU"+muestras+"\r");
    }


    public void setPesoUnitario(float peso){
        System.out.println("EL PESO UNITARIO"+peso);
        float pesox1000=peso*1000;
        String pesostr="";
        if(puntoDecimal==0||puntoDecimal==1){
            pesostr =floatToStringFormat(pesox1000,puntoDecimal);
        }else{
            pesostr =floatToStringFormat(pesox1000,puntoDecimal-1);
        }

        pesostr=completarFormato(pesostr,7);
        System.out.println("EL PESO UNITARIO enviado a minima:"+pesostr);
        serialPort.write("YPU"+pesostr+"\r");
        pesoUnitario=peso;
        SharedPreferences preferencias=activity.getSharedPreferences(NOMBRE, Context.MODE_PRIVATE);
        SharedPreferences.Editor ObjEditor=preferencias.edit();
        ObjEditor.putFloat("punitario", peso);
        ObjEditor.apply();
    }

    public float getPesoUnitario() {
        SharedPreferences preferences=activity.getSharedPreferences(NOMBRE, Context.MODE_PRIVATE);
        return (preferences.getFloat("punitario",0.5F));
    }


    public void setUnidad(String Unidad){
        SharedPreferences preferencias=activity.getSharedPreferences(NOMBRE, Context.MODE_PRIVATE);
        SharedPreferences.Editor ObjEditor=preferencias.edit();
        ObjEditor.putString("unidad",Unidad);
        ObjEditor.apply();
    }

    public void set_DivisionMinima(int divmin){

        SharedPreferences Preferencias=activity.getSharedPreferences(NOMBRE,Context.MODE_PRIVATE);
        SharedPreferences.Editor ObjEditor=Preferencias.edit();
        ObjEditor.putInt("div",divmin);
        ObjEditor.apply();

    }
    public void set_PuntoDecimal(int puntoDecimal){

        SharedPreferences Preferencias=activity.getSharedPreferences(NOMBRE,Context.MODE_PRIVATE);
        SharedPreferences.Editor ObjEditor=Preferencias.edit();
        ObjEditor.putInt("pdecimal",puntoDecimal);
        ObjEditor.apply();

    }
    public void set_UltimaCalibracion(String ucalibracion){

        SharedPreferences Preferencias=activity.getSharedPreferences(NOMBRE,Context.MODE_PRIVATE);
        SharedPreferences.Editor ObjEditor=Preferencias.edit();
        ObjEditor.putString("ucalibracion",ucalibracion);
        ObjEditor.apply();

    }
    public String get_UltimaCalibracion(){
        SharedPreferences Preferencias=activity.getSharedPreferences(NOMBRE,Context.MODE_PRIVATE);
        return Preferencias.getString("ucalibracion","");

    }
    public void set_CapacidadMax(String capacidad){

        SharedPreferences Preferencias=activity.getSharedPreferences(NOMBRE,Context.MODE_PRIVATE);
        SharedPreferences.Editor ObjEditor=Preferencias.edit();
        ObjEditor.putString("capacidad",capacidad);
        ObjEditor.apply();

    }
    public void set_PesoConocido(String pesoConocido){

        SharedPreferences Preferencias=activity.getSharedPreferences(NOMBRE,Context.MODE_PRIVATE);
        SharedPreferences.Editor ObjEditor=Preferencias.edit();
        ObjEditor.putString("pconocido",pesoConocido);
        ObjEditor.apply();

    }

    public int get_DivisionMinima(){
        SharedPreferences Preferencias=activity.getSharedPreferences(NOMBRE,Context.MODE_PRIVATE);
        return Preferencias.getInt("div",0);

    }
    public int get_PuntoDecimal(){
        SharedPreferences Preferencias=activity.getSharedPreferences(NOMBRE,Context.MODE_PRIVATE);
        return Preferencias.getInt("pdecimal",1);

    }
    public String get_CapacidadMax(){
        SharedPreferences Preferencias=activity.getSharedPreferences(NOMBRE,Context.MODE_PRIVATE);
        return Preferencias.getString("capacidad","100");
    }
    public String get_PesoConocido(){
        SharedPreferences Preferencias=activity.getSharedPreferences(NOMBRE,Context.MODE_PRIVATE);
        return Preferencias.getString("pconocido","100");
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
    public static String floatToStringFormat(float numero, int puntodecimal) {
        // Crear el formato de cadena con la cantidad de decimales deseada
        String format = "%." + puntodecimal + "f";

        // Formatear el número y convertirlo a una cadena
        String resultado = String.format(format, numero);

        return resultado;
    }

    public static String completarFormato2(String numero) {

            // Si es menor, calcular cuántos ceros se deben agregar
            int cerosFaltantes = 5 - numero.length();

            // Construir la cadena completa con ceros a la izquierda
            StringBuilder resultado = new StringBuilder();
            for (int i = 0; i < cerosFaltantes; i++) {
                resultado.append("0");
            }
            resultado.append(numero);

            return resultado.toString();

    }


    public static String completarFormato(String numero,int cantidad) {
        // Verificar si la longitud de la cadena es mayor a 6
        if (numero.length() > cantidad) {
            // Si es mayor, eliminar caracteres de la izquierda hasta que tenga 6 caracteres
            numero = numero.substring(numero.length() - cantidad-1);
        } else {
            // Si es menor, calcular cuántos ceros se deben agregar
            int cerosFaltantes = cantidad - numero.length();

            // Construir la cadena completa con ceros a la izquierda
            StringBuilder resultado = new StringBuilder();
            for (int i = 0; i < cerosFaltantes; i++) {
                resultado.append("0");
            }
            resultado.append(numero);

            return resultado.toString();
        }

        return numero;
    }
    public String getCalculoPesoUnitario(){
        String copia=calculoPesoUnitario;
        calculoPesoUnitario="Error";
        return copia;
    }

    public Runnable GET_PESO_cal_bza_Contador= new Runnable() {

        int contador=0;
        String read="",read2="";

        String[] array;
        @Override
        public void run() {

            if(!Objects.equals(estado, M_MODO_CALIBRACION)){
                try {
                  //  System.out.println("empieza runnable 1");
                    if(serialPort.HabilitadoLectura()){
                      //  System.out.println("MINIMA HABILITADO LECTURA");
                     //   System.out.println("punto decimal: "+puntoDecimal);
                        read=serialPort.read_2();
                        String filtro=" ";
                        // read=read.replace("\r\n","");

                        if(read!=null){
                       //     System.out.println("peso unit: "+getPesoUnitario());
                        //    System.out.println("MINIMA INDICE: "+runnableIndice);

                            if((read.toLowerCase().contains(filtro.toLowerCase())||stopcomunicacion)&&!read.contains("\u0006C \r")&&!read.contains("\u0005C \r")){
                                estado=M_MODO_BALANZA;

                                if(runnableIndice==2&&!stopcomunicacion){
                                    array= read.split(filtro);

                                    if(array.length>0){

                                        read2=read;//array[1];
                                        read2=read2.replace(filtro,"");
                                        read2=read2.replace("\r\n","");
                                        read2=read2.replace("\r","");
                                        read2=read2.replace(" ","");
                                        read2=read2.replace("\n","");
                                        read2=read2.replace("\\u0007","");
                                        read2=read2.replace("O","");
                                        read2=read2.replace("E","");
                                        read2=read2.replace("kg","");
                                        if(read2.toLowerCase().contains("-".toLowerCase())){
                                            read2=read2.replace("-","");
                                        }
                                        read=read2.replace(".","");

                                        if(Utils.isNumeric(read2)){
                                            piezas= (int) Float.parseFloat(read2);

                                        }
                                        read="";

                                    }
                                    runnableIndice=-1;
                                    serialPort.write(CONSULTA_BRUTO);
                                }
                                if(runnableIndice==1&&!stopcomunicacion){
                                    if(read.toLowerCase().contains("E".toLowerCase())){
                                        estableStr ="E";
                                    }else if(read.toLowerCase().contains("S".toLowerCase())){
                                        estableStr ="S";
                                    }else{
                                        estableStr ="";
                                    }


                                    array= read.split(filtro);

                                    if(array.length>0){

                                        read2=read;//array[1];
                                        read2=read2.replace(filtro,"");
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
                                            netoStr=read2;
                                            Neto=Float.parseFloat(read2);
                                            //Neto=Neto+10;
                                            Neto= redondear(Neto);
                                            acumulador++;
                                        }
                                        read="";

                                    }
                                    serialPort.write(CONSULTA_PIEZAS);
                                }

                                if(runnableIndice==0&&!stopcomunicacion){
                                    if(read.toLowerCase().contains("E".toLowerCase())){
                                        estableStr ="E";
                                    }else if(read.toLowerCase().contains("S".toLowerCase())){
                                        estableStr ="S";
                                    }else{
                                        estableStr ="";
                                    }
                                //    System.out.println("verificando bruto");
                                    array= read.split(filtro);
                                //    System.out.println("tengo"+read);
                                    if(array.length>0){
                                    //    System.out.println("mayor a 1");
                                        read2=read;//array[1];
                                    //    System.out.println("en la cero"+array[0]);
                                    //    System.out.println("me quede con"+read2);
                                        read2=read2.replace(filtro,"");
                                        read2=read2.replace("\r\n","");
                                        read2=read2.replace(" ","");
                                        read2=read2.replace("\r","");
                                        read2=read2.replace("\n","");
                                        read2=read2.replace("\\u0007","");
                                        read2=read2.replace("O","");
                                        read2=read2.replace("E","");
                                        read2=read2.replace("kg","");
                                        read=read2.replace(".","");

                                        if(Utils.isNumeric(read2)){
                                       //     System.out.println("bruto es un numero");
                                            int index = read2.indexOf('.'); // Busca el índice del primer punto en la cadena
                                            puntoDecimal = read.length() - index;
                                            // uso bigdecimal porque si restaba me daba numeros raros detras de la coma
                                            brutoStr=read2;
                                            Bruto=Float.parseFloat(read2);
                                            Bruto= redondear(Bruto);
                                            //muestreoinstantaneo= bbruto.floatValue();
                                       /* if(taraDigital1==0){
                                            Neto1=Bruto1-Tara1;
                                            Neto1= redondear(Neto1);
                                            neto1Str=String.valueOf(Neto1);
                                        }else{
                                            Neto1=Bruto1-taraDigital1;
                                            Neto1= redondear(Neto1);
                                            neto1Str=String.valueOf(Neto1);
                                        }*/

                                            if(Bruto<pesoBandaCero){
                                                bandaCero =true;
                                            }
                                            else{
                                                if(inicioBandaPeso){
                                                    bandaCero =false;
                                                }

                                            }

                                        }
                                        read="";

                                    }
                                    serialPort.write(CONSULTA_NETO);

                                }
                                if(stopcomunicacion){

                                    array= read.split(filtro);

                                    if(array.length>0){

                                        read2=read;//array[1];
                                        read2=read2.replace(filtro,"");
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
                                            calculoPesoUnitario=read2;
                                            System.out.println("nuevo peso unitario: "+read2);
                                        }
                                        read="";
                                        runnableIndice=0;

                                    }
                                }


                                runnableIndice++;



                                //mHandler.postDelayed(GET_PESO_cal_bza_Contador,200);





                            }else if (read.contains("\u0006C \r")){
                                //entro a calibracion
                                openCalibracion(numero);
                            }
                            else if(Objects.equals(estado, M_MODO_BALANZA)){
                                if(!stopcomunicacion){
                                    System.out.println("MINIMA:ESPERANDO RESPUESTA BALANZA");
                                    serialPort.write(CONSULTA_BRUTO);
                                    runnableIndice=0;
                                }

                                //mHandler.postDelayed(GET_PESO_cal_bza_Contador,200);
                            }
                        }
                    }
                    else if (contador<=8&& Objects.equals(estado, M_VERIFICANDO_MODO)) {
                        if (contador <= 3) {
                          //  System.out.println("MINIMA:BUSCANDO CALIBRACION");
                            serialPort.write("\u0006C \r");
                        } else {
                          //  System.out.println("MINIMA:BUSCANDO CALIBRACION");
                            serialPort.write("\u0005C \r");
                        }

                        contador++;
                    }
                    else if (contador>8|| Objects.equals(estado, M_MODO_BALANZA)) {
                   // } else if (contador>8&& Objects.equals(estado, M_MODO_BALANZA)) {
                        if(!stopcomunicacion){
                            serialPort.write(CONSULTA_BRUTO);
                            System.out.println("MINIMA: NO HAY NADA PARA LEER EN EL PUERTO, CONSULTO BRUTO NUEVAMENTE Y ARRANCA EN CERO");
                        }

                        runnableIndice=0;
                    }
                    if (contador==8){
                       // mainActivity.Mensaje(M_ERROR_COMUNICACION, R.layout.item_customtoasterror);
                        serialPort.write(CONSULTA_BRUTO);
                        runnableIndice=0;
                        contador++;
                    }


                } catch (IOException e) {
                    //System.out.println("ERROR DE MINIMA, TRY-CATCH: "+e);
                    e.printStackTrace();
                }
                //System.out.println("termina runnable 1");
                mHandler.postDelayed(GET_PESO_cal_bza_Contador,200);

            }
            else {
              //  System.out.println("termina runnable 2");
                mHandler.postDelayed(GET_PESO_cal_bza_Contador,50);
            }
        }
    };


    public Runnable GET_PESO_cal_bza= new Runnable() {

        int contador=0;
        String read="",read2="";
        String[] array;
        @Override
        public void run() {

            if(!Objects.equals(estado, M_MODO_CALIBRACION)){
                    try {
                        if(serialPort.HabilitadoLectura()){
                            System.out.println("MINIMA HABILITADO LECTURA");
                            read=serialPort.read_2();
                            String filtro="\r\n";
                            // read=read.replace("\r\n","");

                            if(read!=null){
                                System.out.println("MINIMA: "+read);
                                if(read.toLowerCase().contains(filtro.toLowerCase())){
                                    estado=M_MODO_BALANZA;
                                    if(read.toLowerCase().contains("E".toLowerCase())){
                                        estableStr ="E";
                                        estable=true;
                                    }else if(read.toLowerCase().contains("S".toLowerCase())){
                                        estableStr ="S";
                                        estable=false;
                                    }else{
                                        estableStr ="";
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
                                            Bruto=Float.parseFloat(read2);
                                            Bruto= redondear(Bruto);
                                            //muestreoinstantaneo= bbruto.floatValue();
                                            if(taraDigital==0){
                                                Neto=Bruto-Tara;
                                                Neto= redondear(Neto);
                                                netoStr=String.valueOf(Neto);
                                            }else{
                                                Neto=Bruto-taraDigital;
                                                Neto= redondear(Neto);
                                                netoStr=String.valueOf(Neto);
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
                                    openService();
                                }
                            }
                        }
                        else if (contador<=8&& Objects.equals(estado, M_VERIFICANDO_MODO)) {
                            if (contador == 0) {
                                System.out.println("MINIMA:BUSCANDO CALIBRACION");
                                serialPort.write("\u0006C \r");
                            } else {
                                System.out.println("MINIMA:BUSCANDO CALIBRACION");
                                serialPort.write("\u0005C \r");
                            }

                            contador++;
                        }
                        if (contador==8){
                      //      mainActivity.Mensaje(M_ERROR_COMUNICACION, R.layout.item_customtoasterror);
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
    public Boolean getBandaCero(int numero) {
        return bandaCero;
    }

    @Override
    public void setBandaCero(int numero,Boolean bandaCeroi) {
        bandaCero=bandaCeroi;
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
    public String getTaraDigital(int numero) {
        return taraDigitalStr;
    }

    @Override
    public String getPicoStr(int numero) {
        return "0";
    }

    @Override
    public float getPico(int numero) {
        return 0;
    }

    @Override
    public void init(int numero) {
        estado=M_VERIFICANDO_MODO;
        context=this;
        pesoUnitario=getPesoUnitario();
        pesoBandaCero= getBandaCeroValue(numero);
        puntoDecimal=get_PuntoDecimal();
        ultimaCalibracion=get_UltimaCalibracion();
        if(serialPort!=null){
            if(contador){
                setPesoUnitario(pesoUnitario);
                sendPuntoDecimal();
                GET_PESO_cal_bza_Contador.run();
            }else{
                GET_PESO_cal_bza.run();
            }

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
        CalibracionMinimaFragment fragment = CalibracionMinimaFragment.newInstance(context,service);
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
        return 0;
    }

    @Override
    public String Itw410FrmGetUltimoPeso(int numero) {
        return null;
    }

    @Override
    public int Itw410FrmGetUltimoIndice(int numero) {
        return 0;
    }

}
