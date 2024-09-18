package com.jws.jwsapi;

import static com.jws.jwsapi.core.user.UserConstants.ROLE_SUPERVISOR;
import static com.jws.jwsapi.dialog.DialogUtil.dialogText;
import static com.jws.jwsapi.core.storage.Storage.createMemoryDirectory;
import static com.jws.jwsapi.core.storage.Storage.deleteCache;

import android.app.WallpaperManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.WindowManager;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.android.jws.JwsManager;
import com.jws.jwsapi.core.data.local.PreferencesManager;
import com.jws.jwsapi.core.storage.StorageService;
import com.jws.jwsapi.core.services.FtpInit;
import com.jws.jwsapi.core.services.httpserver.InitServer;
import com.jws.jwsapi.core.user.UserManager;
import com.jws.jwsapi.utils.ToastHelper;
import com.jws.jwsapi.utils.Utils;

import java.io.IOException;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class MainActivity extends AppCompatActivity{
    public static String VERSION ="PH 470 BZA 1.00";
    public JwsManager jwsObject;
    public MainClass mainClass;
    private InitServer initServer;
    @Inject
    UserManager userManager;
    @Inject
    StorageService storageService;
    @Inject
    PreferencesManager preferencesManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        storageService.initService(this);
    }

    private void initFtpWebRTC() {
        try {
            FtpInit ftpInit= new FtpInit(getApplicationContext(), userManager.getUsers(), preferencesManager);
            ftpInit.ftpServer();
        } catch (Exception e) {
            e.printStackTrace();
        }
        initServer = new InitServer(this,this, userManager,preferencesManager);
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
        mainClass = new MainClass(this,this, userManager);
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
        if(preferencesManager.consultaTema()!=R.style.AppTheme_NoActionBar){
            try {
                setTheme(preferencesManager.consultaTema());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void clearCache(){
        Context context=getApplicationContext();
        if(userManager.getLevelUser()>ROLE_SUPERVISOR){
            dialogText(this, "¿Esta seguro de volver a los valores de fabrica del equipo?", "RESER", () -> {
                // Elimina la caché de la aplicación
                deleteCache(context);
                // Puedes agregar más código para eliminar otros datos específicos de tu aplicación si es necesario
                deleteDatabase(MainClass.DB_NAME);
                deleteDatabase("bza-database");
                jwsObject.jwsReboot("");
            });
        }else{
            ToastHelper.message("Debe ingresar la clave para acceder a esta configuracion",R.layout.item_customtoasterror,this);
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
}


