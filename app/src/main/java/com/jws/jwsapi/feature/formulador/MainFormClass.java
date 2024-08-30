package com.jws.jwsapi.feature.formulador;

import android.content.Context;
import android.os.Bundle;
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
import com.jws.jwsapi.R;
import com.jws.jwsapi.utils.Utils;

public class MainFormClass implements OnFragmentChangeListener {

    private final Context context;
    private final MainActivity mainActivity;
    public static String DB_NAME = "Frm_DB";
    public static int DB_VERSION = 4;
    public BalanzaService service;
    public BalanzaService.Balanzas bza;
    public int nBza =1;
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
        service = new BalanzaService(mainActivity,this);
        service.init();
        Runnable myRunnable = () -> {
            try {
                Thread.sleep(2000);// para que service inicialice modbus
                mainActivity.runOnUiThread(() -> {
                    bza =BalanzaService.Balanzas;
                    bza.Itw410FrmSetTiempoEstabilizacion(nBza,preferencesManager.getEstabilizacion());
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




}
