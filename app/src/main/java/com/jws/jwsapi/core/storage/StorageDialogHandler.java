package com.jws.jwsapi.core.storage;

import static com.jws.jwsapi.core.storage.StoragePaths.MEMORY_PATH;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.jws.jwsapi.R;
import com.jws.jwsapi.databinding.DialogoUsbBinding;
import com.jws.jwsapi.utils.ToastHelper;

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
                ToastHelper.message(context.getString(R.string.toast_storage_file_deleted), R.layout.item_customtoastok,context);
            }else{
                ToastHelper.message(context.getString(R.string.toast_storage_file_not_deleted), R.layout.item_customtoasterror,context);
            }
        }else{
            ToastHelper.message(context.getString(R.string.toast_storage_file_not_exist), R.layout.item_customtoasterror,context);
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
                ToastHelper.message(context.getString(R.string.toast_storage_pendrive_not_avaible), R.layout.item_customtoasterror,context);
            }
        }
    }

    private void setupRecyclerView(RecyclerView recyclerView, String extension) {
        StorageAdapter adapter = Storage.setupRecyclerExtension(extension, recyclerView, context);
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
