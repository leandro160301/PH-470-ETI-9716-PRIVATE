package com.jws.jwsapi.core.storage;

import static com.jws.jwsapi.core.storage.StoragePaths.MEMORY_PATH;
import static com.jws.jwsapi.dialog.DialogUtil.dialogLoading;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.jws.jwsapi.MainActivity;
import com.jws.jwsapi.R;
import com.jws.jwsapi.utils.FileUtils;
import com.jws.jwsapi.utils.ToastHelper;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.channels.FileChannel;

public class Storage {
    public static String openAndReadFile(String fileName, MainActivity mainActivity) {
        String filePath = MEMORY_PATH + fileName;
        File file = new File(filePath);
        if (!file.exists()) {
            ToastHelper.message("La etiqueta ya no esta disponible", R.layout.item_customtoasterror, mainActivity);
            return "";
        } else {
            String fileContent = "";
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

            } catch (IOException e) {
                e.printStackTrace();
                ToastHelper.message("Error al intentar leer la etiqueta" + e, R.layout.item_customtoasterror, mainActivity);
            } finally {
                try {
                    if (br != null) br.close();
                    if (isr != null) isr.close();
                    if (fis != null) fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return fileContent;
        }

    }

    public static void copyFileProgress(final File file, final File dir, Context context) {
        AlertDialog dialog = dialogLoading(context, String.valueOf(R.string.dialog_copy_file));
        copyFileTransfer(file, dir, dialog, context);
    }

    @SuppressWarnings("all")
    private static void copyFileTransfer(File file, File dir, AlertDialog dialog, Context context) {
        new Thread(() -> {
            File newFile = new File(dir, file.getName());
            try (
                    FileChannel outputChannel = new FileOutputStream(newFile).getChannel();
                    FileChannel inputChannel = new FileInputStream(file).getChannel()
            ) {
                inputChannel.transferTo(0, inputChannel.size(), outputChannel);
                outputChannel.force(true);
                outputChannel.close();

                Handler mainHandler = new Handler(Looper.getMainLooper());
                mainHandler.post(() -> {
                    dialog.cancel();
                    ToastHelper.message("Archivo enviado", R.layout.item_customtoastok, context);
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    @SuppressWarnings("all")
    public static void createMemoryDirectory() {
        File memoryFile = new File(MEMORY_PATH);
        if (!memoryFile.isDirectory()) {
            memoryFile.mkdir();
        }
    }

    @SuppressWarnings("all")
    public void copy(File src, File dst) throws IOException {
        InputStream in = new FileInputStream(src);
        OutputStream out = new FileOutputStream(dst);

        byte[] buf = new byte[1024];
        int len;
        while ((len = in.read(buf)) > 0) {
            out.write(buf, 0, len);
        }
        in.close();
        out.close();
    }

}
