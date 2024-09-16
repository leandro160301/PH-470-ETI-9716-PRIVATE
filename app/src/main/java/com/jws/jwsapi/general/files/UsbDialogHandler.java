package com.jws.jwsapi.general.files;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.jws.jwsapi.R;
import com.jws.jwsapi.general.utils.AdapterCommonFix;
import com.jws.jwsapi.general.utils.ToastHelper;

import java.io.File;
import java.util.Arrays;
import java.util.List;

public class UsbDialogHandler {
    private final Context context;
    String FileDialog="";
    File file;

    private final List<File> usbMultimediaPaths = Arrays.asList(
            new File("/storage/udisk0"),
            new File("/storage/udisk1"),
            new File("/storage/udisk2")
    );

    public UsbDialogHandler(Context context) {
        this.context = context;
    }

    public void dialogoUSB(){
        AlertDialog.Builder DialogoUSB = new AlertDialog.Builder(context, R.style.AlertDialogCustom);
        LayoutInflater inflater;
        if (context instanceof AppCompatActivity) {
            inflater = ((AppCompatActivity) context).getLayoutInflater();
        } else {
            inflater = LayoutInflater.from(context);
        }
        View mView = inflater.inflate(R.layout.dialogo_usb, null);
        Button Guardar =  mView.findViewById(R.id.buttons);
        Button Cancelar =  mView.findViewById(R.id.buttonc);
        Button Borrar = mView.findViewById(R.id.btborrar);
        Button bt_pdf = mView.findViewById(R.id.bt_pdf);
        Button bt_excel = mView.findViewById(R.id.bt_excel);
        Button bt_csv = mView.findViewById(R.id.bt_csv);
        Button bt_captura = mView.findViewById(R.id.bt_captura);
        RecyclerView recyclerView=mView.findViewById(R.id.listview);
        AdapterCommonFix adapter = Storage.setupRecyclerExtension(".pdf",recyclerView,context);
        adapter.setClickListener((view, position) -> {
            String archivo = "/storage/emulated/0/Memoria/";
            FileDialog=adapter.getItem(position);
            String archivo2=archivo.concat(adapter.getItem(position));
            file = new File(archivo2);
        });
        DialogoUSB.setView(mView);
        final AlertDialog dialogusb = DialogoUSB.create();
        dialogusb.show();
        bt_pdf.setOnClickListener(view -> {
            file=null;
            AdapterCommonFix adapter1 = Storage.setupRecyclerExtension(".pdf",recyclerView,context);
            adapter1.setClickListener((view1, position) -> {
                String archivo = "/storage/emulated/0/Memoria/";
                FileDialog= adapter1.getItem(position);
                String archivo2=archivo.concat(adapter1.getItem(position));
                file = new File(archivo2);
            });
        });
        bt_captura.setOnClickListener(view -> {
            file=null;
            AdapterCommonFix adapter1 = Storage.setupRecyclerExtension(".png",recyclerView,context);
            adapter1.setClickListener((view1, position) -> {
                String archivo = "/storage/emulated/0/Memoria/";
                FileDialog= adapter1.getItem(position);
                String archivo2=archivo.concat(adapter1.getItem(position));
                file = new File(archivo2);
            });
        });
        bt_excel.setOnClickListener(view -> {
            file=null;
            AdapterCommonFix adapter12 = Storage.setupRecyclerExtension(".xls",recyclerView,context);
            adapter12.setClickListener((view13, position) -> {
                String archivo = "/storage/emulated/0/Memoria/";
                FileDialog= adapter12.getItem(position);
                String archivo2=archivo.concat(adapter12.getItem(position));
                file = new File(archivo2);
            });
        });
        bt_csv.setOnClickListener(view -> {
            file=null;
            AdapterCommonFix adapter13 = Storage.setupRecyclerExtension(".csv",recyclerView,context);
            adapter13.setClickListener((view12, position) -> {
                String archivo = "/storage/emulated/0/Memoria/";
                FileDialog= adapter13.getItem(position);
                String archivo2=archivo.concat(adapter13.getItem(position));
                file = new File(archivo2);
            });
        });
        Guardar.setOnClickListener(view -> {
            if (file != null && file.exists()) {
                for(File dir:usbMultimediaPaths){
                    if (dir.isDirectory()) {
                        Storage.copyFileProgress(file, dir,context);
                    }
                }
                if (usbMultimediaPaths.stream().noneMatch(File::isDirectory)) {
                    ToastHelper.message("Pendrive no disponible", R.layout.item_customtoasterror,context);
                }
            }
        });
        Cancelar.setOnClickListener(view -> dialogusb.cancel());
        Borrar.setOnClickListener(view -> {
            if (file != null && file.exists()) {
                boolean eliminacion=file.delete();
                if(eliminacion){
                    ToastHelper.message("Archivo borrado", R.layout.item_customtoastok,context);
                }else{
                    ToastHelper.message("El archivo no se pudo borrar", R.layout.item_customtoasterror,context);
                }
            }else{
                ToastHelper.message("El archivo no existe", R.layout.item_customtoasterror,context);
            }
        });
    }
}
