package com.service.Balanzas.Clases;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import com.service.Balanzas.Interfaz.Balanza;
import com.service.Comunicacion.OnFragmentChangeListener;
import com.service.PuertosSerie.PuertosSerie2;
import com.service.Utils;
import com.service.PuertosSerie.PuertosSerie;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.DecimalFormat;

public class R31P30_I implements Balanza, Balanza.Struct{


    /** si ponemos tara digital, entonces toma la tara como tara digital,
     * si le damos a tara normal la tara digital pasa a cero y la tara es la tara
     *
     *
     */
    //PARA CONTADORA #005P03000102050000#013 TERMINAL
    private final PuertosSerie2 serialPort;
    public int numeroid=0;
    Handler mHandler= new Handler();
    public static final String NOMBRE="R31P30";
    public String estado="VERIFICANDO_MODO";
    private static final String CONSULTA_PUNITARIO="XU2\r";//??????
    private static final String CONSULTA_PIEZAS="PU\r\n";
    private static final String CONSULTA_BRUTO="P\r\n";
    private static final String CONSULTA_NETO="XN2\r";
    public static final String M_VERIFICANDO_MODO="VERIFICANDO_MODO";
    public static final String M_MODO_BALANZA="MODO_BALANZA";
    public static final String M_MODO_CALIBRACION="MODO_CALIBRACION";
    public static final String M_ERROR_COMUNICACION="M_ERROR_COMUNICACION";
    public float taraDigital=0,Bruto=0,Tara=0,Neto=0,pico=0;
    float pesoUnitario=0.5F;
    float pesoBandaCero=0F;
    public int piezas=0;
    public Boolean contador =false;
    int runnableIndice=0;
    public Boolean bandaCero =true,stopcomunicacion=false;
    public Boolean inicioBandaPeso=false;
    public int puntoDecimal=1;
    public String ultimaCalibracion="",calculoPesoUnitario="Error";
    public String brutoStr="0",netoStr="0",taraStr="0",taraDigitalStr="0",picoStr="0";
    public int acumulador=0,numeroBZA=1;
    public String unidad="gr";
    public Boolean estable=false;
    private AppCompatActivity activity;
    private OnFragmentChangeListener fragmentChangeListener;

    public R31P30_I(PuertosSerie2 serialPort, int numeroBZA, AppCompatActivity activity, OnFragmentChangeListener fragmentChangeListener) {
        this.serialPort = serialPort;
        this.numeroBZA=numeroBZA;
        this.activity = activity;
    }

    public void setTara(float tara){
        Tara=tara;
        taraStr=String.valueOf(tara);
    }

    public void setTaraDigital(float tara){
        taraDigital=tara;
        taraDigitalStr=String.valueOf(tara);

    }
    public void sendPuntoDecimal(){
        String punto=String.valueOf(get_PuntoDecimal());
        System.out.println("ENVIANDO PUNTO DECIMAL: "+punto);
        serialPort.write("Pdu"+punto+"\r");

    }


    public void setTaraDigitalSerialPort(float tara){
        String pesostr =floatToStringFormat(tara,puntoDecimal);
        pesostr=completarFormato(pesostr,6);
        pesostr=pesostr.replace(".","");
        serialPort.write("YTD"+pesostr+"\r");

    }

    public void setPesoBandaCero(float peso){
        pesoBandaCero=peso;
        SharedPreferences preferencias=activity.getSharedPreferences(NOMBRE, Context.MODE_PRIVATE);
        SharedPreferences.Editor ObjEditor=preferencias.edit();
        ObjEditor.putFloat("pbandacero", peso);
        ObjEditor.apply();
    }

    public float getPesoBandaCero() {
        SharedPreferences preferences=activity.getSharedPreferences(NOMBRE, Context.MODE_PRIVATE);
        return (preferences.getFloat("pbandacero",5.0F));
    }

    public String Cero(){
        if(serialPort!=null){
            serialPort.write("Z\r\n");
        }

        return "Z\r\n";
    }
    public String Tara(){
        if(serialPort!=null){
            serialPort.write("T\r\n");
        }
        return "T\r\n";
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


    @Override
    public Boolean Itw410FrmSetear(int numero, String setPoint, int Salida) {
        return false;
    }

    @Override
    public String Itw410FrmGetSetPoint(int numero) {
        return null;
    }

    @Override
    public int Itw410FrmGetSalida(int numero) {
        return -1;
    }

    @Override
    public void Itw410FrmStart(int numero) {

    }

    @Override
    public int Itw410FrmGetEstado(int numero) {
        return -1;
    }

    @Override
    public String Itw410FrmGetUltimoPeso(int numero) {
        return "null";
    }

    @Override
    public int Itw410FrmGetUltimoIndice(int numero) {
        return -1;
    }

    @Override
    public void itw410FrmPause(int numero) {

    }

    @Override
    public void itw410FrmStop(int numero) {

    }

    @Override
    public void Itw410FrmSetTiempoEstabilizacion(int numero, int Tiempo) {

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

    public String getUnidad() {
        return unidad;
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

    public Runnable GET_PESO_cal_bza = new Runnable() {

        int contador=0,puntoDecimal=0;
        String read="",read2="";
        Boolean bruto=false;

        String[] array;
        @Override
        public void run() {

                try {
                    if(serialPort.HabilitadoLectura()){
                        acumulador=0;
                        read=serialPort.read_2();
                        String filtro="\r\n";

                        if(read!=null){
                            System.out.println("OHAUS : "+read);

                            if((read.toLowerCase().contains(filtro.toLowerCase()))){
                                estado=M_MODO_BALANZA;

                                if(read.toLowerCase().contains("S".toLowerCase())){
                                    estable=false;
                                }else if(read.toLowerCase().contains("?".toLowerCase())){
                                    estable=true;
                                }

                                array= read.split(filtro);

                                if(array.length>0){

                                    if(read2.contains("g")){
                                        unidad="gr";
                                    }
                                    if(read2.contains("kg")){
                                        unidad="kg";
                                    }
                                    if(read2.contains("k")){
                                        unidad="kg";
                                    }
                                    read2=array[0];
                                    read2=read2.replace(" ","");
                                    read2=read2.replace("\r\n","");
                                    read2=read2.replace("\r","");
                                    read2=read2.replace("\n","");
                                    read2=read2.replace("\\u0007","");
                                    read2=read2.replace("O","");
                                    read2=read2.replace("E","");
                                    read2=read2.replace("kg","");
                                    read2=read2.replace("g","");
                                    read2=read2.replace("gr","");
                                    read2=read2.replace("?","");
                                    if(read2.contains("Err")){
                                        netoStr="Error";
                                        brutoStr="Error";
                                    }
                                    if(bruto&&read2.contains("N")){
                                        setTaraDigital(Bruto);
                                        bruto=false;
                                    }
                                    if(!read2.contains("N")){
                                        bruto=true;
                                        setTaraDigital(0);
                                    }
                                    read2=read2.replace("N","");
                                    read=read2.replace(".","");


                                    if(Utils.isNumeric(read2)){
                                        int index = read2.indexOf('.'); // Busca el índice del primer punto en la cadena
                                        puntoDecimal = read.length() - index;
                                        // uso bigdecimal porque si restaba me daba numeros raros detras de la coma
                                        if(taraDigital!=0){
                                            netoStr=read2;
                                            BigDecimal number = new BigDecimal(netoStr);
                                            netoStr = removeLeadingZeros(number);
                                            Neto=Float.parseFloat(read2);
                                            Bruto=Neto+taraDigital;
                                            brutoStr=String.valueOf(Bruto);
                                            if(index==-1){
                                                brutoStr=brutoStr.replace(".0","");
                                            }
                                        }else{
                                            brutoStr=read2;
                                            BigDecimal number = new BigDecimal(brutoStr);
                                            brutoStr = removeLeadingZeros(number);
                                            Bruto=Float.parseFloat(read2);
                                            Neto=Bruto;
                                            netoStr=brutoStr;
                                        }


                                        //Bruto= redondear(Bruto);
                                        //muestreoinstantaneo= bbruto.floatValue();
                                        /*if(taraDigital==0){
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
                                        }*/
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


                                    }
                                    read="";

                                }


                            }
                        }
                    }else{
                        if(acumulador==5){
                           // mainActivity.Mensaje(M_ERROR_COMUNICACION, R.layout.item_customtoasterror);
                        }
                        netoStr="000000";
                        brutoStr="000000";
                        //serialPort.write(CONSULTA_PIEZAS);
                        acumulador++;
                    }


                } catch (IOException e) {
                    //System.out.println("ERROR DE MINIMA, TRY-CATCH: "+e);
                    e.printStackTrace();
                }
                System.out.println(" OHAUS CONSULTA BRUTO");
                //serialPort.write(CONSULTA_BRUTO);
                mHandler.postDelayed(GET_PESO_cal_bza,200);

        }
    };


    @Override
    public void setID(int numID, int numBza) {
        numeroid=numID;
    }

    @Override
    public int getID( int numBza) {
        return numeroid;
    }

    @Override
    public float getNeto(int numBza) {
        return Neto;
    }

    @Override
    public String getNetoStr(int numBza) {
        return netoStr;
    }

    @Override
    public float getBruto(int numBza) {
        return Bruto;
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
        if(serialPort!=null){
            serialPort.write("T\r\n");
        }
    }

    @Override
    public void setCero(int numBza) {
        if(serialPort!=null){
            serialPort.write("Z\r\n");
        }
        taraDigitalStr="0";
        taraDigital=0;
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
    public Boolean getBandaCero(int numBza) {
        return bandaCero;
    }

    @Override
    public void setBandaCero(int numBza, Boolean bandaCeroi) {
        bandaCero=bandaCeroi;
    }

    @Override
    public float getBandaCeroValue(int numBza) {
        SharedPreferences preferences=activity.getSharedPreferences(NOMBRE, Context.MODE_PRIVATE);
        return (preferences.getFloat(String.valueOf(numBza)+"_"+"pbandacero",5.0F));
    }

    @Override
    public void setBandaCeroValue(int numBza, float bandaCeroValue) {
        pesoBandaCero=bandaCeroValue;
        SharedPreferences preferencias=activity.getSharedPreferences(NOMBRE, Context.MODE_PRIVATE);
        SharedPreferences.Editor ObjEditor=preferencias.edit();
        ObjEditor.putFloat(String.valueOf(numBza)+"_"+"pbandacero",bandaCeroValue);
        ObjEditor.apply();
    }

    @Override
    public Boolean getEstable(int numBza) {
        return estable;
    }

    @Override
    public String format(int numero, String peso) {
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
        SharedPreferences preferences=activity.getSharedPreferences(NOMBRE, Context.MODE_PRIVATE);
        return (preferences.getString(String.valueOf(numBza)+"_"+"unidad","kg"));
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
    public void escribir(String msj,int numBza) {
        serialPort.write(msj);
    }
    @Override
    public void init(int numBza) {
        estado=M_MODO_BALANZA;
        pesoUnitario=getPesoUnitario();
        pesoBandaCero=getPesoBandaCero();
        puntoDecimal=get_PuntoDecimal();
        ultimaCalibracion=get_UltimaCalibracion();
        if(serialPort!=null){
            GET_PESO_cal_bza.run();
        }
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
    public void openCalibracion(int numBza) {

    }

    @Override
    public Boolean getSobrecarga(int numBza) {
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

    @Override
    public void onEvent() {

    }
}
