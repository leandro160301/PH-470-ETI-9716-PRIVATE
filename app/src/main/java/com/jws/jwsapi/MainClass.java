package com.jws.jwsapi;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.jws.jwsapi.general.container.HomeContainerFragment;
import com.jws.jwsapi.general.user.UserManager;
import com.jws.jwsapi.general.home.HomeFragment;
import com.service.Balanzas.BalanzaService;
import com.service.Comunicacion.OnFragmentChangeListener;
import com.jws.jwsapi.general.container.ContainerFragment;

public class MainClass implements OnFragmentChangeListener {

    private final Context context;
    private final MainActivity mainActivity;
    public static String DB_NAME = "Frm_DB";
    public static int DB_VERSION = 4;
    public BalanzaService service;
    public BalanzaService.Balanzas bza;
    public int nBza =1;
    UserManager userManager;
    Boolean permitirClic=true;

    public MainClass(Context context, MainActivity activity, UserManager userManager) {
        this.context = context;
        this.mainActivity = activity;
        this.userManager = userManager;
    }

    public void init() {
        service = new BalanzaService(mainActivity,this);
        service.init();
        Runnable myRunnable = () -> {
            try {
                Thread.sleep(2000);
                bza =BalanzaService.Balanzas;
                mainActivity.runOnUiThread(this::openFragmentPrincipal);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        };
        Thread myThread = new Thread(myRunnable);
        myThread.start();
    }

    @Override
    public void openFragmentPrincipal() {
        Fragment fragment = new HomeFragment();
        FragmentManager fragmentManager = ((AppCompatActivity) context).getSupportFragmentManager();
        Fragment fragmentoActual = new HomeContainerFragment();
        HomeContainerFragment containerFragment = HomeContainerFragment.newInstance(fragment.getClass());
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

    @Override
    public void openFragmentService(Fragment fragment, Bundle arg) {
        if(permitirClic){
            FragmentManager fragmentManager = ((AppCompatActivity) context).getSupportFragmentManager();
            Fragment fragmentoActual = new ContainerFragment();
            boolean programador= userManager.getNivelUsuario() > 3;
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


}
