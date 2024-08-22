package com.service.Impresora;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;


import com.service.Impresora.Tipos.ImprimirBluetooth;
import com.service.Impresora.Tipos.ImprimirRS232;
import com.service.Impresora.Tipos.ImprimirRed;
import com.service.PuertosSerie.PuertosSerie;
import com.service.PuertosSerie.PuertosSerie2;
import com.service.R;
import com.service.Utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class ImprimirEstandar {
    private final Context context;
    private AppCompatActivity mainActivity;
    private  String etiqueta;
    private  int num;
    PuertosSerie2 serialPort;
    private List<String> imprimiblesPredefinidas = new ArrayList<>();
    public ImprimirEstandar(Context context, AppCompatActivity activity, String etiqueta, Integer num, PuertosSerie2 port) {
        this.context = context;
        this.mainActivity = activity;
        this.etiqueta = etiqueta;
        this.num = num;
        this.serialPort = port;
        imprimiblesPredefinidas.add("");
        imprimiblesPredefinidas.add("Bruto");//w0001
        imprimiblesPredefinidas.add("Tara");//w0002
        imprimiblesPredefinidas.add("Neto");//w0003
        imprimiblesPredefinidas.add("Operador");//w0004
        imprimiblesPredefinidas.add("Fecha");//w0005
        imprimiblesPredefinidas.add("Hora");//w0006
        imprimiblesPredefinidas.add("Ingresar texto (fijo)"); //si agregamos nuevas mantener estas ultimas dos ultimas
        imprimiblesPredefinidas.add("Concatenar datos");//si agregamos nuevas mantener estas ultimas dos ultimas

    }


    public void EnviarEtiqueta(){
        int modo=consultaModo(num);
        switch (modo+1){
            case 1:{ // PORTSERIE A
                if(serialPort!=null){
                    ImprimirRS232 imprimirRS232= new ImprimirRS232(serialPort);
                    imprimirRS232.Imprimir(etiqueta);
                }else {
                    Utils.Mensaje("Error para imprimir por puerto serie A", R.layout.item_customtoasterror,mainActivity);
                }
                break;
            }
            case 2:{ //PORTSERIE B
                if(serialPort!=null){
                    ImprimirRS232 imprimirRS232= new ImprimirRS232(serialPort);
                    imprimirRS232.Imprimir(etiqueta);
                }else {
                    Utils.Mensaje("Error para imprimir por puerto serie B", R.layout.item_customtoasterror,mainActivity);
                }
                break;
            }
            case 3: { //PORTSERIE C
                if (serialPort != null) {
                    ImprimirRS232 imprimirRS232 = new ImprimirRS232(serialPort);
                    imprimirRS232.Imprimir(etiqueta);
                } else {
                    Utils.Mensaje("Error para imprimir por puerto serie C", R.layout.item_customtoasterror, mainActivity);
                }
                break;
            }
            case 4: { // USB
              //  ImprimirUSB imprimirUSB= new ImprimirUSB(mainActivity);
             //   imprimirUSB.Imprimir(etiqueta,context,false,null);
                break;
            }
            case 5:{ // RED
                //verificamos si el dato guardado es una ip
                String patronIP = "^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\."
                        + "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\."
                        + "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\."
                        + "([01]?\\d\\d?|2[0-4]\\d|25[0-5])$";
                Pattern pattern = Pattern.compile(patronIP);
                Matcher matcher = pattern.matcher(consultaIP(num));
                if (matcher.matches()) {
                    ImprimirRed imprimirRed= new ImprimirRed();
                    imprimirRed.Imprimir(consultaIP(num),etiqueta);
                }else {
                    Utils.Mensaje("Error para imprimir, IP no valida", R.layout.item_customtoasterror,mainActivity);
                }
                break;
            }
            case 6:{ //BT
                ImprimirBluetooth imprimirBluetooth= new ImprimirBluetooth(context,mainActivity,consultaMAC(num));
                imprimirBluetooth.Imprimir(etiqueta);
                break;
            }
            default:{
                Utils.Mensaje("OPCION DESHABILITADA O NO CONFIGURADA",R.layout.item_customtoasterror,mainActivity);
            }
        }


    }

   /* public void EnviarUltimaEtiqueta(PuertosSerie serialPort){
        int modo=consultaModo();

        if(modo==0){
            ImprimirUSB imprimirUSB= new ImprimirUSB(mainActivity);
            imprimirUSB.Imprimir(getultimaEtiqueta(),context,false,null);
        }
        if(modo==1){
            //verificamos si el dato guardado es una ip
            String patronIP = "^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\."
                    + "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\."
                    + "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\."
                    + "([01]?\\d\\d?|2[0-4]\\d|25[0-5])$";
            Pattern pattern = Pattern.compile(patronIP);

            Matcher matcher = pattern.matcher(consultaIP(num));
            if (matcher.matches()) {
                ImprimirRed imprimirRed= new ImprimirRed();
                imprimirRed.Imprimir(consultaIP(num),getultimaEtiqueta());
            }else {
                Utils.Mensaje("Error para imprimir, IP no valida", R.layout.item_customtoasterror,mainActivity);
            }

        }
        if(modo==2){
            if(serialPort!=null){
                ImprimirRS232 imprimirRS232= new ImprimirRS232(serialPort);
                imprimirRS232.Imprimir(getultimaEtiqueta());
            }else {
                Utils.Mensaje("Error para imprimir por puerto serie B", R.layout.item_customtoasterror,mainActivity);
            }

        }
        if(modo==3){
            ImprimirBluetooth imprimirBluetooth= new ImprimirBluetooth(context,mainActivity,consultaMAC());
            imprimirBluetooth.Imprimir(getultimaEtiqueta());

        }

    }

    public void EnviarEtiquetaManual(PuertosSerie serialPort,String etiqueta){
        int modo=consultaModo();

        if(modo==0){
            ImprimirUSB imprimirUSB= new ImprimirUSB(mainActivity);
            imprimirUSB.Imprimir(etiqueta,context,false,null);
        }
        if(modo==1){
            //verificamos si el dato guardado es una ip
            String patronIP = "^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\."
                    + "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\."
                    + "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\."
                    + "([01]?\\d\\d?|2[0-4]\\d|25[0-5])$";
            Pattern pattern = Pattern.compile(patronIP);

            Matcher matcher = pattern.matcher(consultaIP(num));
            if (matcher.matches()) {
                ImprimirRed imprimirRed= new ImprimirRed();
                imprimirRed.Imprimir(consultaIP(num),etiqueta);
            }else {
                Utils.Mensaje("Error para imprimir, IP no valida", R.layout.item_customtoasterror,mainActivity);
            }

        }
        if(modo==2){
            if(serialPort!=null){
                ImprimirRS232 imprimirRS232= new ImprimirRS232(serialPort);
                imprimirRS232.Imprimir(etiqueta);
            }else {
                Utils.Mensaje("Error para imprimir por puerto serie B", R.layout.item_customtoasterror,mainActivity);
            }

        }
        if(modo==3){
            ImprimirBluetooth imprimirBluetooth= new ImprimirBluetooth(context,mainActivity,consultaMAC());
            imprimirBluetooth.Imprimir(etiqueta);

        }

    }






    public String replaceEtiqueta(List<String> ListElementsFinales,String etiqueta){
        try {
            List<String> antiguos=new ArrayList<>();
            String[] arr = etiqueta.split("\\^FN");
            String[] prueba = etiqueta.split("\\^DFE");
            String eliminar="";
            if(prueba.length>1){
                String []asa= prueba[1].split("\\^FS");
                if(asa.length>1){
                    eliminar="^DFE"+asa[0]+"^FS\n";
                }
            }
            for(int i=1;i<arr.length;i++){
                String []arr2= arr[i].split("\\^FS");
                if(arr2.length>1){
                    System.out.println("var campo:"+arr2[0]);
                    antiguos.add(arr2[0]);
                }

            }
            if(antiguos.size()==ListElementsFinales.size()){
                for(int h=0;h<ListElementsFinales.size();h++){
                    etiqueta=etiqueta.replace(antiguos.get(h),ListElementsFinales.get(h));
                }
            }
            String resultado=etiqueta.replace("^FN","^FD").replace(eliminar,"");

            System.out.println("etiqueta:"+resultado);
            Log.d("etiqueta:",resultado);
            return resultado;
        } catch (Exception e) {
            e.printStackTrace();
            Utils.Mensaje("Ocurrió un error al procesar la etiqueta:"+e.getMessage(), R.layout.item_customtoasterror,mainActivity);
            return ""; // O devolver un valor predeterminado en caso de error
        }
    }

    public String openAndReadFile(String archivo) {
        // Ruta del archivo que quieres leer
        String filePath = "/storage/emulated/0/Memoria/"+archivo;

        File file = new File(filePath);
        if (!file.exists()) {
            Utils.Mensaje("La etiqueta ya no esta disponible",R.layout.item_customtoasterror,mainActivity);
            return "";
        }else{
            String fileContent="";
            FileInputStream fis = null;
            InputStreamReader isr = null;
            BufferedReader br = null;

            try {
                fis = new FileInputStream(file);
                isr = new InputStreamReader(fis);
                br = new BufferedReader(isr);
                StringBuilder stringBuilder = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    stringBuilder.append(line).append("\n");
                }
                fileContent = stringBuilder.toString();

                // Ahora tienes el contenido del archivo en la variable fileContent
                // Puedes mostrarlo en un TextView, en un Toast, etc.
                //mainActivity.Mensaje(fileContent,R.layout.item_customtoastok);

            } catch (IOException e) {
                e.printStackTrace();
                Utils.Mensaje("Error al intentar leer la etiqueta"+ e.toString(),R.layout.item_customtoasterror,mainActivity);
            } finally {
                try {
                    if (br != null) br.close();
                    if (isr != null) isr.close();
                    if (fis != null) fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return fileContent;
            }
        }

    }*/

    public int consultaModo(Integer num) {
        int modo =0;
        SharedPreferences Preferencias=context.getSharedPreferences("Impresoras", Context.MODE_PRIVATE);
        modo =(Preferencias.getInt("Modo_"+num,0));
        return modo;
    }
    public String consultaIP(Integer num) {
        String  modo ="10.41.0.109";
        SharedPreferences Preferencias=context.getSharedPreferences("Impresoras", Context.MODE_PRIVATE);
        modo =(Preferencias.getString("IP_"+num,modo));
        return modo;
    }
    public String consultaMAC(Integer num) {
        SharedPreferences Preferencias=context.getSharedPreferences("Impresoras", Context.MODE_PRIVATE);
        String mac=Preferencias.getString("MAC_"+num,"60:95:32:0C:D6:DE");
        return mac;
    }




/*
    public String getultimaEtiqueta() {
        SharedPreferences Preferencias=context.getSharedPreferences("Impresoras", Context.MODE_PRIVATE);
        String mac=Preferencias.getString("Ultimaetiqueta","");
        return mac;
    }
    public void setUltimaEtiqueta(String MAC) {
        SharedPreferences Preferencias2=context.getSharedPreferences("Impresoras",Context.MODE_PRIVATE);
        SharedPreferences.Editor ObjEditor2=Preferencias2.edit();
        ObjEditor2.putString("Ultimaetiqueta",MAC);
        ObjEditor2.apply();
    }*/


}
