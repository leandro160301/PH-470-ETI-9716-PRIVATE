package com.jws.jwsapi.utils.file;

import static com.jws.jwsapi.dialog.DialogUtil.dialogLoading;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.jws.jwsapi.R;
import com.jws.jwsapi.utils.ToastHelper;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

public class FileUIUtils {
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
}
