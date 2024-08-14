package com.jws.jwsapi.common.impresora;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.jws.jwsapi.R;
import com.jws.jwsapi.base.activities.MainActivity;
import com.jws.jwsapi.common.impresora.preferences.PreferencesPrinterManager;
import com.jws.jwsapi.feature.formulador.data.preferences.PreferencesManager;
import com.jws.jwsapi.utils.Utils;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class PrinterHelper {
    File[] fileArray;
    private MainActivity mainActivity;
    private PreferencesPrinterManager preferencesPrinterManager;
    public PrinterHelper(MainActivity activity,PreferencesPrinterManager preferencesPrinterManager) {
        this.mainActivity = activity;
        this.preferencesPrinterManager=preferencesPrinterManager;
    }


    public List<String> getEtiquetasPRN() {
        String tipo = ".prn";
        List<String >ListElementsArrayListeti=new ArrayList<>();

        File root = new File(Environment.getExternalStorageDirectory().toString()+"/Memoria");

        if(root.exists()){
            fileArray = root.listFiles((dir, filename) -> filename.toLowerCase().endsWith(tipo));
            StringBuilder f = new StringBuilder();
            if(fileArray.length>0){
                for(int i=0; i < fileArray.length; i++){
                    f.append(fileArray[i].getName());
                    ListElementsArrayListeti.add(f.toString());
                    f = new StringBuilder();
                }
            }
        }
        return ListElementsArrayListeti;

    }



}
