package com.jws.jwsapi.general.printer;

import static com.jws.jwsapi.general.files.Storage.openAndReadFile;

import android.content.Context;
import android.util.Log;
import com.jws.jwsapi.MainActivity;
import com.jws.jwsapi.general.printer.preferences.PreferencesPrinterManager;
import com.jws.jwsapi.general.printer.types.SerialPortPrinter;
import com.jws.jwsapi.general.printer.types.NetworkPrinter;
import com.jws.jwsapi.general.printer.types.UsbPrinter;
import com.jws.jwsapi.general.user.UserManager;
import com.jws.jwsapi.general.label.LabelManager;
import com.jws.jwsapi.general.data.local.PreferencesManager;
import com.jws.jwsapi.general.utils.ToastHelper;
import com.jws.jwsapi.general.utils.Utils;
import com.jws.jwsapi.R;
import com.service.PuertosSerie.PuertosSerie2;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class PrinterManager {
    private final Context context;
    private MainActivity mainActivity;
    private PreferencesPrinterManager preferencesPrinterManager;
    UserManager userManager;
    PreferencesManager preferencesManager;
    LabelManager labelManager;

    public PrinterManager(Context context, MainActivity activity, UserManager userManager, PreferencesManager preferencesManager, LabelManager labelManager) {
        this.context = context;
        this.mainActivity = activity;
        this.preferencesManager=preferencesManager;
        this.labelManager=labelManager;
        preferencesPrinterManager=new PreferencesPrinterManager(context);
        this.userManager = userManager;
    }

    public void EnviarEtiqueta(PuertosSerie2 serialPort, int numetiqueta){
        int modo=preferencesPrinterManager.consultaModo();
        if(modo==0){
            UsbPrinter usbPrinter = new UsbPrinter(mainActivity);
            usbPrinter.Imprimir(etiqueta(numetiqueta),context,false,null);
        }
        if(modo==1){

            if (Utils.isIP(preferencesPrinterManager.consultaIP())) {
                NetworkPrinter networkPrinter = new NetworkPrinter();
                networkPrinter.Imprimir(preferencesPrinterManager.consultaIP(),etiqueta(numetiqueta));
            }else {
                ToastHelper.message("Error para imprimir, IP no valida", R.layout.item_customtoasterror,mainActivity);
            }

        }
        if(modo==2){
            if(serialPort!=null){
                SerialPortPrinter serialPortPrinter = new SerialPortPrinter(serialPort);
                serialPortPrinter.Imprimir(etiqueta(numetiqueta));
            }else {
                ToastHelper.message("Error para imprimir por puerto serie B", R.layout.item_customtoasterror,mainActivity);
            }

        }
        if(modo==3){
            //imprimir bluetooth

        }

    }

    public void EnviarUltimaEtiqueta(PuertosSerie2 serialPort){
        int modo=preferencesPrinterManager.consultaModo();
        if(modo==0){
            UsbPrinter usbPrinter = new UsbPrinter(mainActivity);
            usbPrinter.Imprimir(preferencesPrinterManager.getultimaEtiqueta(),context,false,null);
        }
        if(modo==1){
            if (Utils.isIP(preferencesPrinterManager.consultaIP())) {
                NetworkPrinter networkPrinter = new NetworkPrinter();
                networkPrinter.Imprimir(preferencesPrinterManager.consultaIP(),preferencesPrinterManager.getultimaEtiqueta());
            }else {
                ToastHelper.message("Error para imprimir, IP no valida", R.layout.item_customtoasterror,mainActivity);
            }

        }
        if(modo==2){
            if(serialPort!=null){
                SerialPortPrinter serialPortPrinter = new SerialPortPrinter(serialPort);
                serialPortPrinter.Imprimir(preferencesPrinterManager.getultimaEtiqueta());
            }else {
                ToastHelper.message("Error para imprimir por puerto serie B", R.layout.item_customtoasterror,mainActivity);
            }

        }
        if(modo==3){
            //imprimir bluetooth

        }

    }

    public void EnviarEtiquetaManual(PuertosSerie2 serialPort,String etiqueta){
        int modo=preferencesPrinterManager.consultaModo();

        if(modo==0){
            UsbPrinter usbPrinter = new UsbPrinter(mainActivity);
            usbPrinter.Imprimir(etiqueta,context,false,null);
        }
        if(modo==1){
            if (Utils.isIP(preferencesPrinterManager.consultaIP())) {
                NetworkPrinter networkPrinter = new NetworkPrinter();
                networkPrinter.Imprimir(preferencesPrinterManager.consultaIP(),etiqueta);
            }else {
                ToastHelper.message("Error para imprimir, IP no valida", R.layout.item_customtoasterror,mainActivity);
            }

        }
        if(modo==2){
            if(serialPort!=null){
                SerialPortPrinter serialPortPrinter = new SerialPortPrinter(serialPort);
                serialPortPrinter.Imprimir(etiqueta);
            }else {
                ToastHelper.message("Error para imprimir por puerto serie B", R.layout.item_customtoasterror,mainActivity);
            }

        }
        if(modo==3){
            //imprimir bluetooth

        }

    }



    public String etiqueta(int numetiqueta){
        try {
            String etiquetaActual=preferencesManager.getEtiqueta(numetiqueta);
            String etiqueta =openAndReadFile(etiquetaActual,mainActivity);
            if(etiqueta!=null&& !etiqueta.equals("") && !Objects.equals(etiquetaActual, "")){
                List<Integer>ListElementsInt=preferencesManager.getListSpinner(etiquetaActual);
                List<String>ListElementsFijo=preferencesManager.getListFijo(etiquetaActual);
                List<String>ListElementsFinales=new ArrayList<>();
                if(ListElementsInt!=null&&ListElementsFijo!=null){
                    String[] arr = etiqueta.split("\\^FN");
                    System.out.println("largo 1:"+arr.length);
                    System.out.println("largo 2:"+ListElementsInt.size());
                    System.out.println("largo 3:"+ListElementsFijo.size());
                    if(arr.length-1==ListElementsInt.size()&&arr.length-1==ListElementsFijo.size()){
                        for(int i=0;i<arr.length-1;i++){
                            System.out.println("var array:"+arr[i]);
                            String []arr2= arr[i+1].split("\\^FS");
                            if(arr2.length>1){
                                System.out.println("var campo:"+arr2[0]);//este string luego debemos reemplazar por FS+valorvariable con el .replace
                                if(ListElementsInt.get(i)<labelManager.imprimiblesPredefinidas.size()){
                                    if(ListElementsInt.get(i)==0){
                                        ListElementsFinales.add("");
                                    }
                                    if(ListElementsInt.get(i)==1){
                                        ListElementsFinales.add(mainActivity.mainClass.bza.getBrutoStr(mainActivity.mainClass.nBza)+mainActivity.mainClass.bza.getUnidad(mainActivity.mainClass.nBza));
                                    }
                                    if(ListElementsInt.get(i)==2){
                                        ListElementsFinales.add(mainActivity.mainClass.bza.getTaraDigital(mainActivity.mainClass.nBza)+mainActivity.mainClass.bza.getUnidad(mainActivity.mainClass.nBza));
                                    }
                                    if(ListElementsInt.get(i)==3){
                                        ListElementsFinales.add(mainActivity.mainClass.bza.getNetoStr(mainActivity.mainClass.nBza)+mainActivity.mainClass.bza.getUnidad(mainActivity.mainClass.nBza));
                                    }
                                    if(ListElementsInt.get(i)==4){
                                        ListElementsFinales.add(userManager.getUsuarioActual());
                                    }
                                    if(ListElementsInt.get(i)==5){
                                        ListElementsFinales.add(Utils.getFecha());
                                    }
                                    if(ListElementsInt.get(i)==6){
                                        ListElementsFinales.add(Utils.getHora());
                                    }
                                    if(ListElementsInt.get(i)==labelManager.imprimiblesPredefinidas.size()-1){//concat
                                        List<Integer>ListElementsConcat= preferencesManager.getListConcat(etiquetaActual,i);
                                        String separador= preferencesManager.getSeparador(etiquetaActual,i);
                                        String concat="";

                                        if(ListElementsConcat!=null){
                                            for(int j=0;j<ListElementsConcat.size();j++){
                                                if(labelManager.imprimiblesPredefinidas.size()+labelManager.variablesImprimibles.size()>ListElementsConcat.get(j)){
                                                    if(ListElementsConcat.get(j)<labelManager.imprimiblesPredefinidas.size()){
                                                        if(ListElementsConcat.get(j)==0){
                                                            concat=concat.concat(""+separador);
                                                        }
                                                        if(ListElementsConcat.get(j)==1){
                                                            concat=concat.concat(mainActivity.mainClass.bza.getBrutoStr(mainActivity.mainClass.nBza)+separador);
                                                        }
                                                        if(ListElementsConcat.get(j)==2){
                                                            concat=concat.concat(mainActivity.mainClass.bza.getTaraDigital(mainActivity.mainClass.nBza)+separador);
                                                        }
                                                        if(ListElementsConcat.get(j)==3){
                                                            concat=concat.concat(mainActivity.mainClass.bza.getNetoStr(mainActivity.mainClass.nBza)+separador);
                                                        }
                                                        if(ListElementsConcat.get(j)==4){
                                                            concat=concat.concat(userManager.getUsuarioActual()+separador);
                                                        }
                                                        if(ListElementsConcat.get(j)==5){
                                                            concat=concat.concat(Utils.getFecha()+separador);
                                                        }
                                                        if(ListElementsConcat.get(j)==6){
                                                            concat=concat.concat(Utils.getHora()+separador);
                                                        }

                                                    }else if(ListElementsConcat.get(j)<labelManager.variablesImprimibles.size()+labelManager.imprimiblesPredefinidas.size()){
                                                        for(int o = 0; o<labelManager.variablesImprimibles.size(); o++){
                                                            if(ListElementsConcat.get(j)-labelManager.imprimiblesPredefinidas.size()==o){
                                                                concat=concat.concat(labelManager.variablesImprimibles.get(o).value()+separador);
                                                                //  System.out.println("esta entrando a concatenar variables");
                                                            }
                                                        }
                                                    }

                                                }
                                            }
                                        }
                                        StringBuilder stringBuilder = new StringBuilder(concat);
                                        int lastCommaIndex = stringBuilder.lastIndexOf(separador);
                                        if (lastCommaIndex >= 0) {
                                            stringBuilder.deleteCharAt(lastCommaIndex);
                                        }
                                        String resultado = stringBuilder.toString();


                                        ListElementsFinales.add(resultado);
                                    }
                                    if(ListElementsInt.get(i)==labelManager.imprimiblesPredefinidas.size()-2){//fijo
                                        ListElementsFinales.add(ListElementsFijo.get(i));
                                    }
                                }else if(ListElementsInt.get(i)<labelManager.variablesImprimibles.size()+labelManager.imprimiblesPredefinidas.size()){
                                    for(int j = 0; j<labelManager.variablesImprimibles.size(); j++){
                                        if(ListElementsInt.get(i)-labelManager.imprimiblesPredefinidas.size()==j){
                                            ListElementsFinales.add(labelManager.variablesImprimibles.get(j).value());
                                        }
                                    }
                                }

                            }

                        }
                        if(ListElementsFinales.size()== arr.length-1){
                            String etique=replaceEtiqueta(ListElementsFinales,etiqueta).replace("Ñ","\\A5").replace("ñ","\\A4").replace("á","\\A0").replace("é","\\82").replace("í","\\A1").replace("ó","\\A2").replace("Á","\\B5").replace("É","\\90").replace("Í","\\D6").replace("Ó","\\E3") ;
                            preferencesPrinterManager.setUltimaEtiqueta(etique);
                            return etique;
                        }

                    }else{
                        ToastHelper.message("Error,faltan campos por configurar", R.layout.item_customtoasterror,mainActivity);
                    }

                }else{
                    ToastHelper.message("Error,la etiqueta no esta configurada",R.layout.item_customtoasterror,mainActivity);
                }

            }
            return "";
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error etiqueta:"+e.getMessage());
            ToastHelper.message("Ocurrió un error al procesar la etiqueta:"+e.getMessage(), R.layout.item_customtoasterror,mainActivity);
            return ""; // O devolver un valor predeterminado en caso de error
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
            ToastHelper.message("Ocurrió un error al procesar la etiqueta:"+e.getMessage(), R.layout.item_customtoasterror,mainActivity);
            return "";
        }
    }



}
