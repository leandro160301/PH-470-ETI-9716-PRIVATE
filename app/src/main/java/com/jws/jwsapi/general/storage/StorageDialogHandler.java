package com.jws.jwsapi.general.storage;

import static com.jws.jwsapi.general.storage.StoragePaths.MEMORY_PATH;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.jws.jwsapi.R;
import com.jws.jwsapi.databinding.DialogoUsbBinding;
import com.jws.jwsapi.general.utils.AdapterCommonFix;
import com.jws.jwsapi.general.utils.ToastHelper;

import java.io.File;

public class StorageDialogHandler {
    private final Context context;
    File file;

    public StorageDialogHandler(Context context) {
        this.context = context;
    }

    public void showDialog() {
        AlertDialog.Builder dialogoUSB = new AlertDialog.Builder(context, R.style.AlertDialogCustom);

        LayoutInflater inflater = (context instanceof AppCompatActivity)
                ? ((AppCompatActivity) context).getLayoutInflater()
                : LayoutInflater.from(context);

        DialogoUsbBinding binding = DialogoUsbBinding.inflate(inflater);

        setupRecyclerView(binding.listview, ".pdf");

        dialogoUSB.setView(binding.getRoot());
        final AlertDialog dialogusb = dialogoUSB.create();
        dialogusb.show();

        binding.btPdf.setOnClickListener(view -> resetRecyclerView(binding.listview, ".pdf"));
        binding.btCaptura.setOnClickListener(view -> resetRecyclerView(binding.listview, ".png"));
        binding.btExcel.setOnClickListener(view -> resetRecyclerView(binding.listview, ".xls"));
        binding.btCsv.setOnClickListener(view -> resetRecyclerView(binding.listview, ".csv"));
        binding.buttons.setOnClickListener(view -> copyFileToUsb());
        binding.buttonc.setOnClickListener(view -> dialogusb.cancel());
        binding.btborrar.setOnClickListener(view -> deleteFile());
    }

    private void deleteFile() {
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
    }

    private void copyFileToUsb() {
        if (file != null && file.exists()) {
            for(File dir: StoragePaths.DIRECTORY_MEMORY_LIST){
                if (dir.isDirectory()) {
                    Storage.copyFileProgress(file, dir,context);
                }
            }
            if (StoragePaths.DIRECTORY_MEMORY_LIST.stream().noneMatch(File::isDirectory)) {
                ToastHelper.message("Pendrive no disponible", R.layout.item_customtoasterror,context);
            }
        }
    }

    private void setupRecyclerView(RecyclerView recyclerView, String extension) {
        AdapterCommonFix adapter = Storage.setupRecyclerExtension(extension, recyclerView, context);
        adapter.setClickListener((view, position) -> {
            String archivoPath = MEMORY_PATH.concat(adapter.getItem(position));
            file = new File(archivoPath);
        });
    }

    private void resetRecyclerView(RecyclerView recyclerView, String extension) {
        file = null;
        setupRecyclerView(recyclerView, extension);
    }
}
