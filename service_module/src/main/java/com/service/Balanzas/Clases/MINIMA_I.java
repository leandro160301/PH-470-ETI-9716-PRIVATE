package com.service.Balanzas.Clases;

import static com.service.Utils.Mensaje;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import com.service.Balanzas.Fragments.CalibracionMinimaFragment;
import com.service.Balanzas.Interfaz.Balanza;
import com.service.PuertosSerie.PuertosSerie;
import com.service.PuertosSerie.PuertosSerie2;
import com.service.R;
import com.service.Utils;

import java.io.IOException;
import java.io.Serializable;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Objects;

import com.service.Comunicacion.OnFragmentChangeListener;

public class MINIMA_I implements Balanza.Struct, Serializable {



    Boolean PorDemandaBool=false;
    boolean imgbool=true;
    Boolean estadoCentroCero = false;
    Boolean estadoSobrecarga = false;
    Boolean estadoNeto = false;
    Boolean estadoPesoNeg = false;
    Boolean estadoBajoCero = false;
    Boolean estadoBzaEnCero = false;
    Boolean estadoBajaBat = false;
    public int numBza=0;

    public OnFragmentChangeListener fragmentChangeListener;
    Boolean estadoEstable = false;
    private MINIMA_I returnthiscontext(){
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
        estado=M_VERIFICANDO_MODO;
        pesoBandaCero= getBandaCeroValue(numBza);
        puntoDecimal=get_PuntoDecimal();
        ultimaCalibracion=get_UltimaCalibracion();
        receiverinit();
        if(serialPort!=null){
            GET_PESO_cal_bza.run();
        }
    }

    @Override
    public void escribir(String msj,int numBza) {
        serialPort.write(msj);
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
        System.out.println("WOLOOOOOLOOOOOO");
        CalibracionMinimaFragment fragment = CalibracionMinimaFragment.newInstance(context, fragmentChangeListener);
        Bundle args = new Bundle();
        args.putSerializable("instance", context);
        fragmentChangeListener.openFragmentService(fragment,args);

    }

    @Override
    public Boolean getSobrecarga(int numBza) {
        return null;
    }


    private final MINIMA_I context;
    PuertosSerie2.PuertosSerie2Listener receiver =null;
    public final PuertosSerie2 serialPort;
    public AppCompatActivity mainActivity;
    Handler mHandler= new Handler();
    String NOMBRE="MINIMA";

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
    public int numeroid =0;

    public MINIMA_I(PuertosSerie2 serialPort, int numero, AppCompatActivity activity, OnFragmentChangeListener fragmentChangeListener) {
        this.serialPort = serialPort;
        this.numBza = numero;
        this.mainActivity = activity;
        this.fragmentChangeListener=fragmentChangeListener;
        context=this;
    }


    public void receiverinit(){

        int contador = 0;
        String filtro = "\r\n";
        receiver = new PuertosSerie2.PuertosSerie2Listener() {
            @Override
            public void onMsjPort(String data) {
                System.out.println("ESTAMO EN MINIMA WN"+ data);
                String[] array= new ArrayList<>().toArray(new String[0]);
                if(estado==M_MODO_BALANZA) {
                    String data2 = "";
                    //  System.out.println("MINIMA: "+data);
                    if (data.toLowerCase().contains(filtro.toLowerCase())) {

                        if (data.toLowerCase().contains("E".toLowerCase())) {
                            estable = "E";
                        } else if (data.toLowerCase().contains("S".toLowerCase())) {
                            estable = "S";
                        } else {
                            estable = "";
                        }
                        array = data.split(filtro);

                        System.out.println("ESTAMO EN MINIMA arrlenght"+array.length);
                        if (array.length > 0) {
                            data2 = array[0];
                            data2 = data2.replace(" ", "");
                            data2 = data2.replace("\r\n", "");
                            data2 = data2.replace("\r", "");
                            data2 = data2.replace("\n", "");
                            data2 = data2.replace("\\u0007", "");
                            data2 = data2.replace("O", "");
                            data2 = data2.replace("E", "");
                            data2 = data2.replace("kg", "");
                            data = data2.replace(".", "");

                            System.out.println("ESTAMO EN MINIMA data"+ data);


                            if (Utils.isNumeric(data2)) {

                                System.out.println("ESTAMO EN MINIMA num"+ data2);

                                int index = data2.indexOf('.'); // Busca el índice del primer punto en la cadena
                                puntoDecimal = data.length() - index;
                                // uso bigdecimal porque si restaba me daba numeros raros detras de la coma
                                brutoStr = data2;
                                BigDecimal number = new BigDecimal(brutoStr);
                                brutoStr = removeLeadingZeros(number);

                                System.out.println("ESTAMO EN MINIMA brutostr"+ brutoStr);

                                Bruto = Float.parseFloat(data2);

                                //Bruto= redondear(Bruto);
                                //muestreoinstantaneo= bbruto.floatValue();
                                if (taraDigital == 0) {
                                    Neto = Bruto - Tara;
                                    //Neto= redondear(Neto);
                                    netoStr = String.valueOf(Neto);
                                    if (index == -1) {
                                        netoStr = netoStr.replace(".0", "");
                                    }
                                } else {
                                    Neto = Bruto - taraDigital;
                                    //Neto= redondear(Neto);
                                    netoStr = String.valueOf(Neto);

                                    System.out.println("ESTAMO EN MINIMA indice"+ index);

                                    if (index == -1) {
                                        netoStr = netoStr.replace(".0", "");

                                        System.out.println("ESTAMO EN MINIMA netostr"+ netoStr);
                                    }
                                }
                                if (index != -1 && puntoDecimal > 0) {
                                    String formato = "0.";

                                    StringBuilder capacidadBuilder = new StringBuilder(formato);
                                    for (int i = 0; i < puntoDecimal; i++) {
                                        capacidadBuilder.append("0");
                                    }
                                    formato = capacidadBuilder.toString();
                                    DecimalFormat df = new DecimalFormat(formato);
                                    netoStr = df.format(Neto);
                                    taraDigitalStr = df.format(taraDigital);
                                    System.out.println("ESTAMO EN MINIMA tarastr"+ taraDigitalStr);

                                    //taraStr = df.format(ta);
                                }
                                if (Neto > pico) {
                                    pico = Neto;
                                    picoStr = netoStr;
                                }

                                if (Bruto < pesoBandaCero) {
                                    bandaCero = true;
                                } else {
                                    if (inicioBandaPeso) {
                                        bandaCero = false;
                                    }

                                }
                                acumulador++;

                            }
                            //    System.out.println("MINIMA:jijiji"+read);
                            data = "";

                        }
                    }
                }
            }
        };


        readers = new PuertosSerie2.SerialPortReader(serialPort.getInputStream(), receiver);

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
        SharedPreferences preferencias=mainActivity.getSharedPreferences("MINIMA", Context.MODE_PRIVATE);
        SharedPreferences.Editor ObjEditor=preferencias.edit();
        ObjEditor.putFloat(String.valueOf(numBza)+"_"+"pbandacero", peso);
        ObjEditor.apply();
    }

    public float getPesoBandaCero() {
        SharedPreferences preferences=mainActivity.getSharedPreferences("MINIMA", Context.MODE_PRIVATE);
        return (preferences.getFloat(String.valueOf(numBza)+"_"+"pbandacero",5.0F));
    }

    public String Cero(){
        serialPort.write("KZERO\r\n");
        Tara=0;
        setTaraDigital(0);
        return "KZERO\r\n";
    }
    public String Tara(){
        if(serialPort!=null){
            serialPort.write("KTARE\r");
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

        serialPort.write( "\u0005O\r");
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
        String pesoConocido="";
        if(pesoconocido.length()>0) {
            pesoConocido = pesoconocido.replace(",", "");
            pesoConocido = pesoConocido.replace(".", "");
            int cerocount = 0;


            System.out.println("PESOCONDEB" + pesoConocido.toString() + PuntoDecimal);
            int enter = pesoConocido.length();
            int end = 0;
            if (pesoconocido.contains(".")) {
                enter = pesoconocido.indexOf('.');
                end = pesoconocido.length() - (enter + 1);
            }

            if (end != 0) {
                if (end > Integer.parseInt(PuntoDecimal)) {

                    pesoConocido = String.format("%." + PuntoDecimal + "f", Double.parseDouble(pesoconocido));

                    pesoConocido = pesoConocido.replace(".", "");
                    System.out.println(pesoConocido);

                }

                if (enter + Integer.parseInt(PuntoDecimal) > 5 && end != 0) {
                    System.out.println("err1");
                    return null;
                }



                if (end < Integer.parseInt(PuntoDecimal) && end != 0) {
                    StringBuilder capacidadBuilder = new StringBuilder(pesoConocido);
                    for (int i = 0; i < Integer.parseInt(PuntoDecimal) - end; i++) {
                        capacidadBuilder.append("0");
                        System.out.println("PESOCONDEB 1 " + capacidadBuilder);
                    }
                    pesoConocido = capacidadBuilder.toString();
                }

                if (pesoConocido.length() - cerocount < 5 && enter < (5 - Integer.parseInt(PuntoDecimal))) {
                    StringBuilder capacidadBuilder1 = new StringBuilder(pesoConocido);
                    while (capacidadBuilder1.length() - cerocount < 5) {
                        capacidadBuilder1.insert(0, "0");
                        System.out.println("PESOCONDEB 2" + capacidadBuilder1);
                    }
                    pesoConocido = capacidadBuilder1.toString();
                }
                if (pesoConocido.length() > 5) {
                    StringBuilder capacidadBuilder1 = new StringBuilder(pesoConocido);
                    while (capacidadBuilder1.length() > 5) {
                        System.out.println("PESOCONDEB 3" + capacidadBuilder1.length() + "-" + Integer.parseInt(PuntoDecimal));
                        char lastChar = capacidadBuilder1.charAt(capacidadBuilder1.length() - 1);
                        if (lastChar == '0' && (capacidadBuilder1.length() - 1) - enter > Integer.parseInt(PuntoDecimal)) {
                            capacidadBuilder1 = new StringBuilder(capacidadBuilder1.substring(0, capacidadBuilder1.length() - 1));
                        } else {
                            return null;
                        }

                        System.out.println("PESOCONDEB 3 " + capacidadBuilder1);
                    }
                    pesoConocido = capacidadBuilder1.toString();
                }

            } else {
                if (pesoconocido.length() + Integer.parseInt(PuntoDecimal) > 5) { // PROBABLE PROBLEM
                    return null;
                }
                StringBuilder capacidadBuilder = new StringBuilder(pesoconocido);
                for (int i = 0; i < Integer.parseInt(PuntoDecimal); i++) {
                    capacidadBuilder.append("0");
                }
                pesoConocido = capacidadBuilder.toString();
                if (pesoconocido.length() < 5) {
                    StringBuilder capacidadBuilder1 = new StringBuilder(pesoConocido);
                    while (capacidadBuilder1.length() < 5) {
                        capacidadBuilder1.insert(0, "0");
                    }
                    pesoConocido = capacidadBuilder1.toString();
                }

            }

            System.out.println("PESOCONDEB final" + pesoConocido);
            String pesocon = String.format("%." + PuntoDecimal + "f", Double.parseDouble(pesoconocido));

        }else{
            return null;
        }

        return "\u0005L"+pesoConocido+"\r";
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

    private  ArrayList<Character> initlist2(){
        ArrayList<Character> lis = new ArrayList<>();
        lis.add('a');lis.add('b');
        lis.add('c');lis.add('d');
        lis.add('e');lis.add('f');
        lis.add('g');lis.add('h');
        lis.add('i');lis.add('j');
        lis.add('k');lis.add('p');
        return lis;
    }
    private ArrayList<Character> initlist(){
        ArrayList<Character> list =new ArrayList<>();
        list.add('C');
        list.add('S');
        list.add('P');
        list.add('D');
        list.add('U');
        list.add('L');
        list.add('Z');
        list.add('M');
        list.add('R');
        list.add('A');
        list.add('I');
        list.add('O');
        return list;
    }
    public ArrayList<String> Errores2(String lectura){

        ArrayList<String> listErr =new ArrayList<>();

        if(lectura!=null){

            //System.out.println("MINIMA EERRRR"+lectura);
            ArrayList<Character> list =new ArrayList<>();
            ArrayList<Character> list2=new ArrayList<>();
            list = initlist();
            list2 = initlist2();
            int lastindex=0;
            ArrayList<String> listlect =new ArrayList<>();
            Boolean bol=false;
            for (int i=0;i<list.size();i++){
                if(lectura.indexOf(list2.get(i))!=-1){
                    bol=true;
                }
            }
            if(bol){
                for (int i=0;i<list.size();i++){

                    if(lectura.indexOf(list.get(i))!=-1){
                        try{
                            //   System.out.println("MINIMA ERR INDX "+lectura);
                            listlect.add(lectura.substring(lastindex,lectura.indexOf(list.get(i))+3));
                            //  System.out.println("MINIMA EERRRR  subst "+lectura.substring(lastindex,lectura.indexOf(list.get(i))+3));
                            lastindex= (lectura.indexOf(list.get(i))+3) ;
                        }catch(Exception e){

                        }



                    }
                }
                for (int i=0;i<listlect.size();i++){

                    if(listlect.get(i).charAt(0)==6&&listlect.get(i).charAt(2)!=32){
                        System.out.println("MINIMA  paso weon"+listlect.get(i).charAt(0)+" "+listlect.get(i).charAt(1)+" "+listlect.get(i).charAt(2)
                        );

                        String Error="";

                        switch (listlect.get(i).charAt(1)) {

                            case 'C':
                                Error="C_CAL-";
                                break;
                            case 'S':
                                Error="S_SAVE-";
                                break;
                            case 'P':
                                Error="P_PARAM-";
                                break;
                            case 'D':
                                Error="D_CAPMAX-";
                                break;
                            case 'U':
                                Error="U_CERO-";
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
                                Error="X_UNKNOWN";
                                break;
                        }

                        switch (listlect.get(i).charAt(2)) {
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
                                Error=Error+"ERR UNKNOWN";
                                break;
                        }
                        switch (listlect.get(i).charAt(2)) {
                            case 'a':
                                Error=Error+": Peso patrón colocado mayor al máximo permitido";
                                break;
                            case 'c':
                                Error=Error+": Valor de señal, durante la toma del cero; es menor al mínimo permitido";
                                break;
                            case 'd':
                                Error=Error+": (capacidad máxima/división mínima) es mayor a 5000 divisiones de display. Esta limitación es sólo para el modo rápido";
                                break;
                            case 'e':
                                Error=Error+": Error interno. comunicarse con soporte";
                                break;
                            case 'f':
                                Error=Error+"";
                                break;
                            case 'g':
                                Error=Error+"";
                                break;
                            case 'h':
                                Error=Error+"";
                                break;
                            case 'i':
                                Error=Error+"";
                                break;
                            case 'j':
                                Error=Error+": No termino la calibracion";
                                break;
                            case 'k':
                                Error=Error+": Dispositivo no habilitado";
                                break;
                            case 'l':
                                Error=Error+"";
                                break;
                            case 'p':
                                Error=Error+": Peso patrón colocado, es menor o igual al valor de la toma del cero";
                                break;
                            default:
                                Error=Error+"";
                                break;
                        }

                        listErr.add(Error);
                    }
                }
            }
        }
        if(listErr.size()>=1){
            //System.out.println("MINIMA EERRRR"+ listErr.size());
            return listErr;
        }else{
            return null;
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


    public void setPesoUnitario(float peso){
        pesoUnitario=peso;
        SharedPreferences preferencias=mainActivity.getSharedPreferences("MINIMA", Context.MODE_PRIVATE);
        SharedPreferences.Editor ObjEditor=preferencias.edit();
        ObjEditor.putFloat(String.valueOf(numBza)+"_"+"punitario", 0.5F);
        ObjEditor.apply();
    }

    public float getPesoUnitario() {
        SharedPreferences preferences=mainActivity.getSharedPreferences("MINIMA", Context.MODE_PRIVATE);
        return (preferences.getFloat(String.valueOf(numBza)+"_"+"punitario",0.5F));
    }


    public void setUnidad(String Unidad){
        SharedPreferences preferencias=mainActivity.getSharedPreferences("MINIMA", Context.MODE_PRIVATE);
        SharedPreferences.Editor ObjEditor=preferencias.edit();
        ObjEditor.putString(String.valueOf(numBza)+"_"+"unidad",Unidad);
        ObjEditor.apply();
    }

    public String getUnidad() {
        //Trae la unidad producto guardado en memoria
        SharedPreferences preferences=mainActivity.getSharedPreferences("MINIMA", Context.MODE_PRIVATE);

        //System.out.println("MINIMA ERR UNIDAD:"+preferences.toString()+" "+numero);
        return (preferences.getString(String.valueOf(numBza)+"_"+"unidad","kg"));
    }

    public void set_DivisionMinima(int divmin){

        SharedPreferences Preferencias=mainActivity.getSharedPreferences("MINIMA",Context.MODE_PRIVATE);
        SharedPreferences.Editor ObjEditor=Preferencias.edit();
        ObjEditor.putInt(String.valueOf(numBza)+"_"+"div",divmin);
        ObjEditor.apply();

    }
    public void set_PuntoDecimal(int puntoDecimal){

        SharedPreferences Preferencias=mainActivity.getSharedPreferences("MINIMA",Context.MODE_PRIVATE);
        SharedPreferences.Editor ObjEditor=Preferencias.edit();
        ObjEditor.putInt(String.valueOf(numBza)+"_"+"pdecimal",puntoDecimal);
        ObjEditor.apply();

    }
    public void set_UltimaCalibracion(String ucalibracion){

        SharedPreferences Preferencias=mainActivity.getSharedPreferences("MINIMA",Context.MODE_PRIVATE);
        SharedPreferences.Editor ObjEditor=Preferencias.edit();
        ObjEditor.putString(String.valueOf(numBza)+"_"+"ucalibracion",ucalibracion);
        ObjEditor.apply();

    }
    public String get_UltimaCalibracion(){
        SharedPreferences Preferencias=mainActivity.getSharedPreferences("MINIMA",Context.MODE_PRIVATE);
        return Preferencias.getString(String.valueOf(numBza)+"_"+"ucalibracion","");

    }
    public void set_CapacidadMax(String capacidad){

        SharedPreferences Preferencias=mainActivity.getSharedPreferences("MINIMA",Context.MODE_PRIVATE);
        SharedPreferences.Editor ObjEditor=Preferencias.edit();
        ObjEditor.putString(String.valueOf(numBza)+"_"+"capacidad",capacidad);
        ObjEditor.apply();

    }
    public void set_PesoConocido(String pesoConocido){

        SharedPreferences Preferencias=mainActivity.getSharedPreferences("MINIMA",Context.MODE_PRIVATE);
        SharedPreferences.Editor ObjEditor=Preferencias.edit();
        ObjEditor.putString(String.valueOf(numBza)+"_"+"pconocido",pesoConocido);
        ObjEditor.apply();

    }

    public int get_DivisionMinima(){
        SharedPreferences Preferencias=mainActivity.getSharedPreferences("MINIMA",Context.MODE_PRIVATE);
        return Preferencias.getInt(String.valueOf(numBza)+"_"+"div",0);

    }
    public int get_PuntoDecimal(){
        SharedPreferences Preferencias=mainActivity.getSharedPreferences("MINIMA",Context.MODE_PRIVATE);
        int lea=Preferencias.getInt(String.valueOf(numBza)+"_"+"pdecimal",1);
        System.out.println("MINIMA CALIBRACION PUNTO DECIMAL: "+String.valueOf(lea));
        return lea;

    }
    public String get_CapacidadMax(){
        SharedPreferences Preferencias=mainActivity.getSharedPreferences("MINIMA",Context.MODE_PRIVATE);
        return Preferencias.getString(String.valueOf(numBza)+"_"+"capacidad","100");
    }
    public String get_PesoConocido(){
        SharedPreferences Preferencias=mainActivity.getSharedPreferences("MINIMA",Context.MODE_PRIVATE);
        return Preferencias.getString(String.valueOf(numBza)+"_"+"pconocido","100");
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
    public void Itw410FrmSetear(int numero, String setPoint, int Salida) {

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

    @Override
    public String getPicoStr(int numBza) {
        return picoStr;
    }

    @Override
    public float getPico(int numBza) {
        return pico;
    }




    Runnable GET_PESO_cal_bza = new Runnable(){
        String[] array;
        int contador = 0;
        boolean intern=true;
        String filtro = "\r\n";

        @Override
        public void run() {

            try {
                if (contador <= 12 && Objects.equals(estado, M_VERIFICANDO_MODO)) {
                    if (contador == 0) {
                        System.out.println("MINIMA:BUSCANDO CALIBRACION");
                        serialPort.write("\u0006C \r");
                        mHandler.postDelayed(GET_PESO_cal_bza,800);

                    } else {
                        System.out.println("MINIMA:BUSCANDO CALIBRACION");
                        serialPort.write("\u0005C \r");
                        mHandler.postDelayed(GET_PESO_cal_bza,800);
                    }
                    //System.out.println("MINIMA:jijiji"+read);
                    contador++;
                }

                if(serialPort.HabilitadoLectura()  && Objects.equals(estado, M_VERIFICANDO_MODO)){
                    System.out.println("MINIMA HABILITADO LECTURA");
                    String read=serialPort.read_2();
                    String filtro="\r\n";
                    // read=read.replace("\r\n","");
                    if(read!=null){

                           if (read.contains("\u0006C")){

                         ArrayList<String> Listerr = Errores2(read);//Errores2(Mensaje);//
                            if (Listerr!=null &&Listerr.size()>0 && intern) {
                                intern=false;
                                for (int i = 0; i < Listerr.size(); i++) {
                                    Mensaje(Listerr.get(i), R.layout.item_customtoasterror,mainActivity);
                                }
                        }else{
                                if(intern){
                                    System.out.println("MINIMA:CALIBRACION");
                                    estado=M_MODO_CALIBRACION;
                                    openCalibracion(numBza);

                                    //  mainActivity.MainClass.openFragment(new CalibracionMINIMAFragment2_minima());
                                    //}
                                    contador=16;
                                }
                            }
                        }

                    }
                    if(read.toLowerCase().contains(filtro.toLowerCase())) {
                        estado = M_MODO_BALANZA;
                        readers.startReading();
                        if (read.toLowerCase().contains("E".toLowerCase())) {
                            estable = "E";
                        } else if (read.toLowerCase().contains("S".toLowerCase())) {
                            estable = "S";
                        } else {
                            estable = "";
                        }


                        array = read.split(filtro);

                        if (contador == 12) {
                            // System.out.println("MINIMA:jijiji"+data);
                           // mainActivity.Mensaje(M_ERROR_COMUNICACION, R.layout.item_customtoasterror);
                            contador++;
                        }


                    }}



                } catch (IOException e) {
                    throw new RuntimeException(e);
                    }

        };
    };

    @Override
    public void setID(int numID,int numBza) {
        numeroid=numID;

    }

    @Override
    public int getID(int numBza) {
        return numeroid;
    }

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
            serialPort.write("KTARE\r");
        }
    }

    @Override
    public void setCero(int numBza) {
        if(serialPort!=null){
            serialPort.write("KZERO\r\n");
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
  /* public String format(String numero) {
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
    }*/
/*public class MINIMA  {



     si ponemos tara digital, entonces toma la tara como tara digital,
      si le damos a tara normal la tara digital pasa a cero y la tara es la tara

    //PARA CONTADORA #005P03000102050000#013 TERMINAL
    private final Context context;
    private final PuertosSerie serialPort;
    private MainActivity mainActivity;
    Handler mHandler= new Handler();
    final String nombre="MINIMA";
    public String estado="VERIFICANDO_MODO";
    private static final String CONSULTA_PUNITARIO="XU2\r";//??????
    private static final String CONSULTA_PIEZAS="XP2\r";
    private static final String CONSULTA_BRUTO="XG2\r";
    private static final String CONSULTA_NETO="XN2\r";
    public static final String M_VERIFICANDO_MODO="VERIFICANDO_MODO";
    public static final String M_MODO_BALANZA="MODO_BALANZA";
    public static final String M_MODO_CALIBRACION="MODO_CALIBRACION";
    public static final String M_ERROR_COMUNICACION="M_ERROR_COMUNICACION";
    public float taraDigital=0,Bruto=0,Tara=0,Neto=0;
    public String estable="";
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

    public MINIMA(Context context, PuertosSerie serialPort, MainActivity activity) {
        this.context = context;
        this.serialPort = serialPort;
        this.mainActivity = activity;
    }

    public void init(){
        estado=M_VERIFICANDO_MODO;
        pesoUnitario=getPesoUnitario();
        pesoBandaCero=getPesoBandaCero();
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

    public void setTara(float tara){
        Tara=tara;
        taraStr=String.valueOf(tara);
    }


    public void setTaraDigital(float tara){
        taraDigital=tara;
        taraDigitalStr=String.valueOf(tara);

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
        SharedPreferences preferencias=context.getSharedPreferences(nombre, Context.MODE_PRIVATE);
        SharedPreferences.Editor ObjEditor=preferencias.edit();
        ObjEditor.putFloat("pbandacero", peso);
        ObjEditor.apply();
    }

    public float getPesoBandaCero() {
        SharedPreferences preferences=context.getSharedPreferences(nombre, Context.MODE_PRIVATE);
        return (preferences.getFloat("pbandacero",5.0F));
    }

    public String Cero(){
        if(serialPort!=null){
            serialPort.write("KZERO\r");
        }

        return "KZERO\r";
    }
    public String Tara(){
        if(serialPort!=null){
            serialPort.write("KTARE\r");
        }
        return "KTARE\r";
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
        SharedPreferences preferencias=context.getSharedPreferences(nombre, Context.MODE_PRIVATE);
        SharedPreferences.Editor ObjEditor=preferencias.edit();
        ObjEditor.putFloat("punitario", peso);
        ObjEditor.apply();
    }

    public float getPesoUnitario() {
        SharedPreferences preferences=context.getSharedPreferences(nombre, Context.MODE_PRIVATE);
        return (preferences.getFloat("punitario",0.5F));
    }


    public void setUnidad(String Unidad){
        SharedPreferences preferencias=context.getSharedPreferences(nombre, Context.MODE_PRIVATE);
        SharedPreferences.Editor ObjEditor=preferencias.edit();
        ObjEditor.putString("unidad",Unidad);
        ObjEditor.apply();
    }

    public String getUnidad() {
        //Trae la unidad producto guardado en memoria
        SharedPreferences preferences=context.getSharedPreferences(nombre, Context.MODE_PRIVATE);
        return (preferences.getString("unidad","kg"));
    }

    public void set_DivisionMinima(int divmin){

        SharedPreferences Preferencias=context.getSharedPreferences(nombre,Context.MODE_PRIVATE);
        SharedPreferences.Editor ObjEditor=Preferencias.edit();
        ObjEditor.putInt("div",divmin);
        ObjEditor.apply();

    }
    public void set_PuntoDecimal(int puntoDecimal){

        SharedPreferences Preferencias=context.getSharedPreferences(nombre,Context.MODE_PRIVATE);
        SharedPreferences.Editor ObjEditor=Preferencias.edit();
        ObjEditor.putInt("pdecimal",puntoDecimal);
        ObjEditor.apply();

    }
    public void set_UltimaCalibracion(String ucalibracion){

        SharedPreferences Preferencias=context.getSharedPreferences(nombre,Context.MODE_PRIVATE);
        SharedPreferences.Editor ObjEditor=Preferencias.edit();
        ObjEditor.putString("ucalibracion",ucalibracion);
        ObjEditor.apply();

    }
    public String get_UltimaCalibracion(){
        SharedPreferences Preferencias=context.getSharedPreferences(nombre,Context.MODE_PRIVATE);
        return Preferencias.getString("ucalibracion","");

    }
    public void set_CapacidadMax(String capacidad){

        SharedPreferences Preferencias=context.getSharedPreferences(nombre,Context.MODE_PRIVATE);
        SharedPreferences.Editor ObjEditor=Preferencias.edit();
        ObjEditor.putString("capacidad",capacidad);
        ObjEditor.apply();

    }
    public void set_PesoConocido(String pesoConocido){

        SharedPreferences Preferencias=context.getSharedPreferences(nombre,Context.MODE_PRIVATE);
        SharedPreferences.Editor ObjEditor=Preferencias.edit();
        ObjEditor.putString("pconocido",pesoConocido);
        ObjEditor.apply();

    }

    public int get_DivisionMinima(){
        SharedPreferences Preferencias=context.getSharedPreferences(nombre,Context.MODE_PRIVATE);
        return Preferencias.getInt("div",0);

    }
    public int get_PuntoDecimal(){
        SharedPreferences Preferencias=context.getSharedPreferences(nombre,Context.MODE_PRIVATE);
        return Preferencias.getInt("pdecimal",1);

    }
    public String get_CapacidadMax(){
        SharedPreferences Preferencias=context.getSharedPreferences(nombre,Context.MODE_PRIVATE);
        return Preferencias.getString("capacidad","100");
    }
    public String get_PesoConocido(){
        SharedPreferences Preferencias=context.getSharedPreferences(nombre,Context.MODE_PRIVATE);
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
                                        estable="E";
                                    }else if(read.toLowerCase().contains("S".toLowerCase())){
                                        estable="S";
                                    }else{
                                        estable="";
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
                                            Neto= redondear(Neto);
                                            acumulador++;
                                        }
                                        read="";

                                    }
                                    serialPort.write(CONSULTA_PIEZAS);
                                }

                                if(runnableIndice==0&&!stopcomunicacion){
                                    if(read.toLowerCase().contains("E".toLowerCase())){
                                        estable="E";
                                    }else if(read.toLowerCase().contains("S".toLowerCase())){
                                        estable="S";
                                    }else{
                                        estable="";
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
                                      //if(taraDigital1==0){
                                        //    Neto1=Bruto1-Tara1;
                                         //   Neto1= redondear(Neto1);
                                         //   neto1Str=String.valueOf(Neto1);
                                        //}else{
                                         //   Neto1=Bruto1-taraDigital1;
                                          //  Neto1= redondear(Neto1);
                                         //   neto1Str=String.valueOf(Neto1);
                                        //}

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
                              //  System.out.println("MINIMA:CALIBRACION");
                            //    mainActivity.MainClass.openFragment(new CalibracionMINIMAFragment());
                                estado=M_MODO_CALIBRACION;
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
                                        estable="E";
                                    }else if(read.toLowerCase().contains("S".toLowerCase())){
                                        estable="S";
                                    }else{
                                        estable="";
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
                                    System.out.println("MINIMA:CALIBRACION");
                               //     mainActivity.MainClass.openFragment(new CalibracionMINIMAFragment());
                                    estado=M_MODO_CALIBRACION;
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
                        else if (contador==8){
                            mainActivity.Mensaje(M_ERROR_COMUNICACION, R.layout.item_customtoasterror);
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
}*/
