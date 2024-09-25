package com.jws.jwsapi.core.storage;

import static com.jws.jwsapi.core.storage.StoragePaths.MEMORY_PATH;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.jws.jwsapi.R;
import com.jws.jwsapi.databinding.DialogoUsbBinding;
import com.jws.jwsapi.utils.FileUtils;
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
            boolean eliminacion = file.delete();
            if (eliminacion) {
                showMessage(R.string.toast_storage_file_deleted, R.layout.item_customtoastok);
            } else {
                showMessage(R.string.toast_storage_file_not_deleted, R.layout.item_customtoasterror);
            }
        } else {
            showMessage(R.string.toast_storage_file_not_exist, R.layout.item_customtoasterror);
        }
    }

    private void copyFileToUsb() {
        if (file == null || !file.exists()) return;

        for (File dir : StoragePaths.DIRECTORY_MEMORY_PATHS) {
            if (dir.isDirectory()) {
                Storage.copyFileProgress(file, dir, context);
            }
        }
        if (StoragePaths.DIRECTORY_MEMORY_PATHS.stream().noneMatch(File::isDirectory)) {
            showMessage(R.string.toast_storage_pendrive_not_avaible, R.layout.item_customtoasterror);
        }
    }

    private void showMessage(int text, int layout) {
        ToastHelper.message(context.getString(text), layout, context);
    }

    private void setupRecyclerView(RecyclerView recyclerView, String extension) {
        StorageAdapter adapter = setupRecyclerExtension(extension, recyclerView, context);
        adapter.setClickListener((view, position) -> {
            String archivoPath = MEMORY_PATH.concat(adapter.getItem(position));
            file = new File(archivoPath);
        });
    }

    private static StorageAdapter setupRecyclerExtension(String extension, RecyclerView recyclerView, Context context) {
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        StorageAdapter adapter = new StorageAdapter(context, FileUtils.getFilesExtension(extension));
        recyclerView.setAdapter(adapter);
        return adapter;
    }

    private void resetRecyclerView(RecyclerView recyclerView, String extension) {
        file = null;
        setupRecyclerView(recyclerView, extension);
    }
}
