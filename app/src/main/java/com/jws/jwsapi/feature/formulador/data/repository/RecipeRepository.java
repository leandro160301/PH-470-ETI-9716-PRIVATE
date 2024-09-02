package com.jws.jwsapi.feature.formulador.data.repository;

import android.os.Environment;

import com.jws.jwsapi.feature.formulador.data.sql.DatabaseHelper;
import com.jws.jwsapi.feature.formulador.models.FormModelIngredientes;
import com.jws.jwsapi.feature.formulador.models.FormModelReceta;
import com.jws.jwsapi.utils.ToastHelper;
import com.jws.jwsapi.utils.Utils;
import com.opencsv.CSVReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import au.com.bytecode.opencsv.CSVWriter;

public class RecipeRepository {
    private final DatabaseHelper formSqlHelper;

    @Inject
    public RecipeRepository(DatabaseHelper formSqlHelper) {
        this.formSqlHelper = formSqlHelper;
    }
    public String getNuevoLoteFecha() {
        String nuevolote = Utils.getFechaDDMMYYYY().replace("/", "");
        int ultimo = formSqlHelper.getLastLote(nuevolote);
        return nuevolote + "-" + (ultimo + 1);
    }

    public String verificarNuevoLoteFecha() {
        String nuevolote = Utils.getFechaDDMMYYYY().replace("/", "");
        int ultimo = formSqlHelper.getLastLote(nuevolote);
        return (ultimo == 0) ? nuevolote + "-1" : null;
    }

    public List<FormModelIngredientes> getIngredientes(ToastHelper toastHelper) {
        List<FormModelIngredientes> cod = new ArrayList<>();
        try {
            String filePath = Environment.getExternalStorageDirectory() + "/Memoria/Ingredientes.csv";
            File filess = new File(filePath);
            boolean error = false;
            if (filess.exists()) {
                try (CSVReader reader = new CSVReader(new FileReader(filePath))) {
                    String[] nextLine;
                    while ((nextLine = reader.readNext()) != null) {
                        if (nextLine.length > 2) {
                            String pos0 = nextLine[0].replace("\"", "");
                            String pos1 = nextLine[1].replace("\"", "");
                            String pos2 = nextLine[2].replace("\"", "");
                            int pos2Int=0;
                            if (Utils.isNumeric(pos2))pos2Int=Integer.parseInt(pos2);
                            cod.add(new FormModelIngredientes(pos0, pos1,pos2Int));
                        } else {
                            error = true;
                        }
                    }
                    if (error) {
                        toastHelper.mensajeError("Error en el archivo, verifique que todos los datos son correctos");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                toastHelper.mensajeError("Error no se encuentran los ingredientes");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return cod;
    }

    public List<FormModelReceta> getReceta(String receta,ToastHelper toastHelper) {
        List<FormModelReceta> listreceta = new ArrayList<>();
        String[] arr = receta.split("_");
        float total = 0f;
        try {
            String filePath = Environment.getExternalStorageDirectory() + "/Memoria/" + receta + ".csv";
            File filess = new File(filePath);
            boolean error = false;
            if (filess.exists()) {
                try (CSVReader reader = new CSVReader(new FileReader(filePath))) {
                    String[] nextLine;
                    while ((nextLine = reader.readNext()) != null) {
                        if (nextLine.length > 2) {
                            String codigo =nextLine[0].replace("\"", "");
                            String ingrediente =nextLine[1].replace("\"", "");
                            String kilos = nextLine[2].replace("\"", "");
                            if (codigo != null && ingrediente != null && kilos != null && arr.length == 3 && Utils.isNumeric(kilos)) {

                                listreceta.add(new FormModelReceta(arr[1].replace("_", ""), arr[2].replace("_", ""),
                                        "0", codigo, ingrediente, kilos, "NO", ""));
                                if (Utils.isNumeric(kilos))total = total + Float.parseFloat(kilos);

                            } else {
                                error = true;
                            }
                        } else {
                            error = true;
                        }
                    }
                    for (int i = 0; i < listreceta.size(); i++) {
                        listreceta.get(i).setKilos_totales(String.valueOf(total));
                    }
                    if (error) {
                        toastHelper.mensajeError("Error en el archivo, verifique que todos los datos son correctos");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                toastHelper.mensajeError("Error en el archivo");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return listreceta;

    }

    public void setReceta(String receta, List<FormModelReceta> lista) throws IOException {
        Runnable myRunnable = () -> {
            File filePath = new File(Environment.getExternalStorageDirectory() + "/Memoria/" + receta + ".csv");
            if (!filePath.exists()) {
                try {
                    filePath.createNewFile();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            filePath.delete();
            try {
                filePath.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            char separador = ',';
            try {
                CSVWriter writer = new CSVWriter(new FileWriter(filePath.getAbsolutePath(), true), separador);
                for (int i = 0; i < lista.size(); i++) {
                    writer.writeNext(new String[]{lista.get(i).getCodigoIng(), lista.get(i).getDescIng(), lista.get(i).getKilosIng()});
                }
                writer.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        };
        Thread myThread = new Thread(myRunnable);
        myThread.start();

    }

    public void setIngredientes(List<FormModelIngredientes> lista) throws IOException {
        Runnable myRunnable = () -> {
            File filePath = new File(Environment.getExternalStorageDirectory() + "/Memoria/Ingredientes.csv");
            if (filePath.exists()) {
                filePath.delete();
            }
            try {
                Boolean bool = filePath.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            char separador = ',';
            try {
                CSVWriter writer = new CSVWriter(new FileWriter(filePath.getAbsolutePath(), true), separador);
                for (int i = 0; i < lista.size(); i++) {
                    writer.writeNext(new String[]{lista.get(i).getCodigo(), lista.get(i).getNombre(),String.valueOf(lista.get(i).getSalida())});
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
