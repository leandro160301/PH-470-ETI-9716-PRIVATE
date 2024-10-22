package com.jws.jwsapi.productionline;

import android.content.Context;
import android.os.Environment;

import com.jws.jwsapi.R;
import com.jws.jwsapi.utils.ToastHelper;
import com.opencsv.CSVReader;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import au.com.bytecode.opencsv.CSVWriter;

public class ProductionLineCaliberRepository {

    public static List<String> getCalibers(Context context) {
        List<String> elements = new ArrayList<>();
        try {
            String filePath = Environment.getExternalStorageDirectory() + "/Memoria/Calibres.csv";
            File filess = new File(filePath);
            boolean error = false;
            if (filess.exists()) {
                try (CSVReader reader = new CSVReader(new FileReader(filePath))) {
                    String[] nextLine;
                    while ((nextLine = reader.readNext()) != null) {
                        if (nextLine.length > 0) {
                            String pos0 = nextLine[0].replace("\"", "");
                            elements.add(pos0);
                        } else {
                            error = true;
                        }
                    }
                    if (error) {
                        ToastHelper.message("Error en el archivo, verifique que todos los datos son correctos", R.layout.item_customtoasterror, context);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                ToastHelper.message("Error no se encuentran calibres", R.layout.item_customtoasterror, context);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return elements;
    }

    public static void setCalibers(List<String> elements) {
        Runnable myRunnable = () -> {
            File filePath = new File(Environment.getExternalStorageDirectory() + "/Memoria/Calibres.csv");
            if (filePath.exists()) {
                filePath.delete();
            }
            try {
                Boolean bool = filePath.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            try {
                CSVWriter writer = new CSVWriter(new FileWriter(filePath.getAbsolutePath(), true), ',');
                for (int i = 0; i < elements.size(); i++) {
                    writer.writeNext(new String[]{elements.get(i)});
                }
                writer.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        };

        Thread myThread = new Thread(myRunnable);
        myThread.start();

    }

}
