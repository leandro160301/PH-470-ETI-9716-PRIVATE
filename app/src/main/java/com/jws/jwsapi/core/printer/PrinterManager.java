package com.jws.jwsapi.core.printer;

import static com.jws.jwsapi.core.storage.Storage.openAndReadFile;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.jws.jwsapi.MainActivity;
import com.jws.jwsapi.core.user.UserManager;
import com.jws.jwsapi.core.label.LabelManager;
import com.jws.jwsapi.utils.ToastHelper;
import com.jws.jwsapi.utils.Utils;
import com.jws.jwsapi.R;
import com.service.PuertosSerie.PuertosSerie2;

import java.util.ArrayList;
import java.util.List;

public class PrinterManager {
    private final Context context;
    private final MainActivity mainActivity;
    UserManager userManager;
    PrinterPreferences printerPreferences;
    LabelManager labelManager;
    PrinterCode printerCode;

    public PrinterManager(Context context, MainActivity activity, UserManager userManager, PrinterPreferences printerPreferences, LabelManager labelManager) {
        this.context = context;
        this.mainActivity = activity;
        this.printerPreferences = printerPreferences;
        this.labelManager=labelManager;
        this.userManager = userManager;
        printerCode = new PrinterCode(activity,printerPreferences,labelManager,userManager);
    }

    public void EnviarEtiqueta(PuertosSerie2 serialPort, int numetiqueta){
        int modo= printerPreferences.getMode();
        if(modo==0){
            UsbPrinter usbPrinter = new UsbPrinter(mainActivity);
            usbPrinter.Imprimir(printerCode.getLabelCode(numetiqueta),context,false,null);
        }
        if(modo==1){

            if (Utils.isValidIp(printerPreferences.getIp())) {
                NetworkPrinter networkPrinter = new NetworkPrinter();
                networkPrinter.Imprimir(printerPreferences.getIp(), printerCode.getLabelCode(numetiqueta));
            }else {
                ToastHelper.message("Error para imprimir, IP no valida", R.layout.item_customtoasterror,mainActivity);
            }

        }
        if(modo==2){
            if(serialPort!=null){
                SerialPortPrinter serialPortPrinter = new SerialPortPrinter(serialPort);
                serialPortPrinter.Imprimir(printerCode.getLabelCode(numetiqueta));
            }else {
                ToastHelper.message("Error para imprimir por puerto serie B", R.layout.item_customtoasterror,mainActivity);
            }
        }
    }

    public void EnviarUltimaEtiqueta(PuertosSerie2 serialPort){
        int modo= printerPreferences.getMode();
        if(modo==0){
            UsbPrinter usbPrinter = new UsbPrinter(mainActivity);
            usbPrinter.Imprimir(printerPreferences.getLastLabel(),context,false,null);
        }
        if(modo==1){
            if (Utils.isValidIp(printerPreferences.getIp())) {
                NetworkPrinter networkPrinter = new NetworkPrinter();
                networkPrinter.Imprimir(printerPreferences.getIp(), printerPreferences.getLastLabel());
            }else {
                ToastHelper.message("Error para imprimir, IP no valida", R.layout.item_customtoasterror,mainActivity);
            }

        }
        if(modo==2){
            if(serialPort!=null){
                SerialPortPrinter serialPortPrinter = new SerialPortPrinter(serialPort);
                serialPortPrinter.Imprimir(printerPreferences.getLastLabel());
            }else {
                ToastHelper.message("Error para imprimir por puerto serie B", R.layout.item_customtoasterror,mainActivity);
            }

        }

    }

    public void EnviarEtiquetaManual(PuertosSerie2 serialPort,String etiqueta){
        int modo= printerPreferences.getMode();

        if(modo==0){
            UsbPrinter usbPrinter = new UsbPrinter(mainActivity);
            usbPrinter.Imprimir(etiqueta,context,false,null);
        }
        if(modo==1){
            if (Utils.isValidIp(printerPreferences.getIp())) {
                NetworkPrinter networkPrinter = new NetworkPrinter();
                networkPrinter.Imprimir(printerPreferences.getIp(),etiqueta);
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

    }

}
