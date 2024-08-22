package com.jws.jwsapi.feature.formulador;

import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import com.jws.jwsapi.base.ui.activities.MainActivity;
import com.jws.jwsapi.common.users.UsersManager;
import com.jws.jwsapi.feature.formulador.data.preferences.PreferencesManager;
import com.jws.jwsapi.feature.formulador.ui.fragment.FormPrincipal;
import com.service.Balanzas.BalanzaService;
import com.service.Comunicacion.OnFragmentChangeListener;
import com.jws.jwsapi.base.containers.ContainerFragment;
import com.jws.jwsapi.base.containers.ContainerPrincipalFragment;
import com.jws.jwsapi.feature.formulador.models.FormModelIngredientes;
import com.jws.jwsapi.feature.formulador.models.FormModelReceta;
import com.jws.jwsapi.R;
import com.jws.jwsapi.utils.Utils;
import com.opencsv.CSVReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import au.com.bytecode.opencsv.CSVWriter;

public class MainFormClass implements OnFragmentChangeListener {

    private final Context context;
    private final MainActivity mainActivity;
    public static String DB_NAME = "Frm_DB";
    public static int db_version = 4;
    public BalanzaService Service;
    public BalanzaService.Balanzas BZA;
    public int N_BZA=1;
    UsersManager usersManager;
    Boolean permitirClic=true;
    PreferencesManager preferencesManager;

    public MainFormClass(Context context, MainActivity activity,UsersManager usersManager,PreferencesManager preferencesManager) { //constructor
        this.context = context;
        this.mainActivity = activity;
        this.usersManager = usersManager;
        this.preferencesManager = preferencesManager;
    }

    public void init() {
        Service= new BalanzaService(mainActivity,this);
        Service.init();
        Runnable myRunnable = () -> {
            try {
                Thread.sleep(2000);
                mainActivity.runOnUiThread(() -> {
                    BZA=BalanzaService.Balanzas;
                    BZA.Itw410FrmSetTiempoEstabilizacion(N_BZA,preferencesManager.getEstabilizacion());
                    openFragmentPrincipal();
                });

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        };

        Thread myThread = new Thread(myRunnable);
        myThread.start();

    }

    public void openFragmentPrincipal() {
        Fragment fragment = new FormPrincipal();
        FragmentManager fragmentManager = ((AppCompatActivity) context).getSupportFragmentManager();
        Fragment fragmentoActual = new ContainerPrincipalFragment();
        ContainerPrincipalFragment containerFragment = ContainerPrincipalFragment.newInstance(fragment.getClass());
        containerFragment.setFragmentActual(fragmentoActual);
        fragmentManager.beginTransaction()
                .replace(R.id.container_fragment, containerFragment)
                .commit();
    }

    public void openFragment(Fragment fragment) {
        FragmentManager fragmentManager = ((AppCompatActivity) context).getSupportFragmentManager();
        Fragment fragmentoActual = new ContainerFragment();

        ContainerFragment containerFragment = ContainerFragment.newInstance(fragment.getClass());
        containerFragment.setFragmentActual(fragmentoActual);
        fragmentManager.beginTransaction()
                .replace(R.id.container_fragment, containerFragment)
                .commit();
    }

    public void openFragmentService(Fragment fragment, Bundle arg) {
        if(permitirClic){
            FragmentManager fragmentManager = ((AppCompatActivity) context).getSupportFragmentManager();
            Fragment fragmentoActual = new ContainerFragment();
            boolean programador= usersManager.getNivelUsuario() > 3;
            ContainerFragment containerFragment = ContainerFragment.newInstanceService(fragment.getClass(),arg,programador);
            containerFragment.setFragmentActual(fragmentoActual);
            fragmentManager.beginTransaction()
                    .replace(R.id.container_fragment, containerFragment)
                    .commit();
            permitirClic = false;
            Handler handler= new Handler();
            handler.postDelayed(() -> permitirClic = true, 1000); //arreglar problema de que mas de una optima llame a service al mismo tiempo
        }

    }

    public String devuelveTurnoActual(){
        String hora=Utils.getHora();
        String turno="0";
        if(hora.length()>4){
            String []arr=hora.split(":");
            if(arr.length>2){
                String horanum=arr[0].replace(":","");
                if(Utils.isNumeric(horanum)){
                    float horaActual = Float.parseFloat(horanum);
                    float turno1 = preferencesManager.getTurno1();
                    float turno2 = preferencesManager.getTurno2();
                    float turno3 = preferencesManager.getTurno3();
                    float turno4 = preferencesManager.getTurno4();

                    if (turno4 > 0) { // Verifica si el turno 4 esta habilitado
                        if ((horaActual >= turno1) && (horaActual < turno2)) {
                            turno = "TURNO 1";
                        } else if ((horaActual >= turno2) && (horaActual < turno3)) {
                            turno = "TURNO 2";
                        } else if ((horaActual >= turno3) && (horaActual < turno4)) {
                            turno = "TURNO 3";
                        } else {
                            turno = "TURNO 4";
                        }
                    } else { // Si el turno 4 esta deshabilitado
                        if ((horaActual >= turno1) && (horaActual < turno2)) {
                            turno = "TURNO 1";
                        } else if ((horaActual >= turno2) && (horaActual < turno3)) {
                            turno = "TURNO 2";
                        } else {
                            turno = "TURNO 3";
                        }
                    }
                }
            }

        }
        return turno;
    }

    public List<FormModelIngredientes> getIngredientes() {
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
                        Utils.Mensaje("Error en el archivo, verifique que todos los datos son correctos", R.layout.item_customtoasterror,mainActivity);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                Utils.Mensaje("Error no se encuentran los ingredientes", R.layout.item_customtoasterror,mainActivity);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return cod;
    }

    public List<FormModelReceta> getReceta(String receta) {
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
                        Utils.Mensaje("Error en el archivo, verifique que todos los datos son correctos", R.layout.item_customtoasterror,mainActivity);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                Utils.Mensaje("Error no se encuentra el archivo", R.layout.item_customtoasterror,mainActivity);
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
                    writer.writeNext(new String[]{lista.get(i).getCodigo_ing(), lista.get(i).getDescrip_ing(), lista.get(i).getKilos_ing()});
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
