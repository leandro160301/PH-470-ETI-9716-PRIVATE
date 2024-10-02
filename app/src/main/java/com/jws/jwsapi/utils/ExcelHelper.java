package com.jws.jwsapi.utils;

import static com.jws.jwsapi.dialog.DialogUtil.dialogLoading;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;

import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.formula.functions.T;

import java.io.File;
import java.io.FileOutputStream;
import java.lang.reflect.Field;
import java.util.List;

@SuppressWarnings("unused")
public class ExcelHelper {

    @SuppressWarnings("unused")
    public static void excelDialog(List<T> list, Context context) {
        final AlertDialog dialog = dialogLoading(context, "Creando excel...");
        exportToExcel(list);
        new Thread(() -> {
            try {
                Thread.sleep(7000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            Handler mainHandler = new Handler(Looper.getMainLooper());
            mainHandler.post(() -> {
                if (dialog.isShowing()) {
                    dialog.cancel();
                }
            });
        }).start();
    }

    @SuppressWarnings("all")
    private static <T> void exportToExcel(List<T> listModel) {
        if (listModel == null || listModel.isEmpty()) {
            return;
        }

        File filePath = new File(Environment.getExternalStorageDirectory() + "/Memoria/Informe.xls");
        int j = 1;
        while (filePath.exists()) {
            filePath = new File(Environment.getExternalStorageDirectory() + "/Memoria/Informe (" + j + ").xls");
            j++;
        }

        HSSFWorkbook hssfWorkbook = new HSSFWorkbook();
        HSSFSheet hssfSheet = hssfWorkbook.createSheet("INFORME");

        HSSFRow headerRow = hssfSheet.createRow(0);
        Field[] fields = listModel.get(0).getClass().getDeclaredFields();

        for (int i = 0; i < fields.length; i++) {
            headerRow.createCell(i).setCellValue(fields[i].getName());
        }

        for (int i = 0; i < listModel.size(); i++) {
            HSSFRow row = hssfSheet.createRow(i + 1);
            T item = listModel.get(i);

            for (int k = 0; k < fields.length; k++) {
                Field field = fields[k];
                field.setAccessible(true);
                try {
                    Object value = field.get(item);
                    if (value != null) {
                        row.createCell(k).setCellValue(value.toString());
                    } else {
                        row.createCell(k).setCellValue("");
                    }
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }

        try {
            if (!filePath.exists()) {
                filePath.createNewFile();
            }
            FileOutputStream fileOutputStream = new FileOutputStream(filePath);
            hssfWorkbook.write(fileOutputStream);

            if (fileOutputStream != null) {
                fileOutputStream.flush();
                fileOutputStream.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

