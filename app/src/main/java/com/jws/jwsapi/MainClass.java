package com.jws.jwsapi;

import static com.jws.jwsapi.core.user.UserConstants.ROLE_ADMINISTRATOR;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.jws.jwsapi.core.container.ContainerContainerFragment;
import com.jws.jwsapi.core.container.ContainerFragment;
import com.jws.jwsapi.core.user.UserManager;
import com.jws.jwsapi.home.HomeFragment;
import com.service.Balanzas.BalanzaService;
import com.service.Comunicacion.OnFragmentChangeListener;

public class MainClass implements OnFragmentChangeListener {

    private final Context context;
    private final MainActivity mainActivity;
    public static String DB_NAME = "Frm_DB";
    public BalanzaService service;
    public BalanzaService.Balanzas bza;
    public int nBza =1;
    UserManager userManager;
    Boolean clickEnable = true;

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
        Fragment fragmentoActual = new ContainerContainerFragment();
        ContainerContainerFragment containerFragment = ContainerContainerFragment.newInstance(fragment.getClass());
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
        if(clickEnable){
            FragmentManager fragmentManager = ((AppCompatActivity) context).getSupportFragmentManager();
            Fragment fragmentoActual = new ContainerFragment();
            boolean programador= userManager.getLevelUser() > ROLE_ADMINISTRATOR;
            ContainerFragment containerFragment = ContainerFragment.newInstanceService(fragment.getClass(),arg,programador);
            containerFragment.setFragmentActual(fragmentoActual);
            fragmentManager.beginTransaction()
                    .replace(R.id.container_fragment, containerFragment)
                    .commit();
            clickEnable = false;
            Handler handler= new Handler();
            handler.postDelayed(() -> clickEnable = true, 1000); //arreglar problema de que mas de una optima llame a service al mismo tiempo
        }

    }


}
