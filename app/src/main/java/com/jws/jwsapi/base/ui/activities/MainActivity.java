package com.jws.jwsapi.base.ui.activities;

import static com.jws.jwsapi.common.storage.Storage.createMemoryDirectory;
import static com.jws.jwsapi.common.storage.Storage.deleteCache;
import static com.jws.jwsapi.feature.formulador.ui.dialog.DialogUtil.dialogoTexto;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.app.WallpaperManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import com.android.jws.JwsManager;
import com.jws.jwsapi.base.containers.ContainerFragment;
import com.jws.jwsapi.base.containers.ContainerPrincipalFragment;
import com.jws.jwsapi.base.data.preferences.PreferencesManagerBase;
import com.jws.jwsapi.common.ftpserver.FtpInit;
import com.jws.jwsapi.common.httpserver.InitServer;
import com.jws.jwsapi.common.storage.Storage;
import com.jws.jwsapi.common.users.UsersManager;
import com.jws.jwsapi.feature.formulador.MainFormClass;
import com.jws.jwsapi.feature.formulador.data.preferences.PreferencesManager;
import com.jws.jwsapi.R;
import com.jws.jwsapi.feature.formulador.ui.dialog.DialogButtonInterface;
import com.jws.jwsapi.feature.formulador.ui.fragment.FormPrincipal;
import com.jws.jwsapi.utils.Utils;
import com.service.Balanzas.BalanzaService;
import com.service.Comunicacion.OnFragmentChangeListener;

import java.io.IOException;
import javax.inject.Inject;
import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class MainActivity extends AppCompatActivity implements OnFragmentChangeListener {
    public static String VERSION ="PH 470 FRM 1.02";
    public JwsManager jwsObject;
    public MainFormClass mainClass;
    public Storage storage;
    InitServer initServer;
    public PreferencesManagerBase preferencesManagerBase;
    Boolean permitirClic=true;
    @Inject
    UsersManager usersManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        preferencesManagerBase= new PreferencesManagerBase(this);
        setTheme();
        initJwsObject();
        updateViews();
        createMemoryDirectory();
        hideNav();
        initFtpWebRTC();
        initMainClass();
        initPendrive();

    }


    private void initPendrive() {
        storage =new Storage(this);
        storage.init();
    }

    private void initFtpWebRTC() {
        try {
            FtpInit ftpInit= new FtpInit(getApplicationContext(),usersManager.obtenerUsuarios());
            ftpInit.ftpServer();
        } catch (Exception e) {
            e.printStackTrace();
        }
        initServer = new InitServer(this,this,usersManager);
        try {
            initServer.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void initJwsObject() {
        jwsObject = new JwsManager(getApplicationContext());
        setContentView(R.layout.activity_main);
    }

    private void initMainClass() {
        mainClass = new MainFormClass(this,this,usersManager,new PreferencesManager(this.getApplication()));
        mainClass.init();
    }

    private void updateViews() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        WallpaperManager wallpaperManager = WallpaperManager.getInstance(getApplicationContext());
        Drawable drawable = ContextCompat.getDrawable(getApplicationContext(),R.drawable.blanco);
        Bitmap bitmap = Utils.drawableToBitmap(drawable);
        try {
            wallpaperManager.setBitmap(bitmap);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void hideNav() {
        Intent hidenav = new Intent("android.intent.action.HIDE_NAVIGATION_BAR");
        this.getApplicationContext().sendBroadcast(hidenav);
    }

    public void setTheme() {
        if(preferencesManagerBase.consultaTema()!=R.style.AppTheme_NoActionBar){
            try {
                setTheme(preferencesManagerBase.consultaTema());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }


    public void clearCache(){
        Context context=getApplicationContext();
        if(usersManager.getNivelUsuario()>2){
            dialogoTexto(this, "¿Esta seguro de volver a los valores de fabrica del equipo?", "RESER", new DialogButtonInterface() {
                @Override
                public void buttonClick() {
                    SharedPreferences preferences = getSharedPreferences(PreferencesManager.SP_NAME, Context.MODE_PRIVATE);
                    preferences.edit().clear().apply();
                    // Elimina la caché de la aplicación
                    deleteCache(context);
                    // Puedes agregar más código para eliminar otros datos específicos de tu aplicación si es necesario
                    deleteDatabase(MainFormClass.DB_NAME);
                    jwsObject.jwsReboot("");
                }
            });
        }else{
            Utils.Mensaje("Debe ingresar la clave para acceder a esta configuracion",R.layout.item_customtoasterror,this);
        }


    }


    public void openSettings(){
        Intent launchIntent = getApplicationContext().getPackageManager().getLaunchIntentForPackage("com.android.settings");
        if (launchIntent != null) {
            startActivity(launchIntent);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        initServer.handleActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void openFragmentService(Fragment fragment, Bundle arg) {
        if(permitirClic){
            FragmentManager fragmentManager = ((AppCompatActivity) this).getSupportFragmentManager();
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

    @Override
    public void openFragmentPrincipal() {
        Fragment fragment = new FormPrincipal();
        FragmentManager fragmentManager = ((AppCompatActivity) this).getSupportFragmentManager();
        Fragment fragmentoActual = new ContainerPrincipalFragment();
        ContainerPrincipalFragment containerFragment = ContainerPrincipalFragment.newInstance(fragment.getClass());
        containerFragment.setFragmentActual(fragmentoActual);
        fragmentManager.beginTransaction()
                .replace(R.id.container_fragment, containerFragment)
                .commit();
    }

    public void openFragment(Fragment fragment) {
        FragmentManager fragmentManager = ((AppCompatActivity) this).getSupportFragmentManager();
        Fragment fragmentoActual = new ContainerFragment();

        ContainerFragment containerFragment = ContainerFragment.newInstance(fragment.getClass());
        containerFragment.setFragmentActual(fragmentoActual);
        fragmentManager.beginTransaction()
                .replace(R.id.container_fragment, containerFragment)
                .commit();
    }
}


