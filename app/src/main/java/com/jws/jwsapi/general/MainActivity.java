package com.jws.jwsapi.general;

import static com.jws.jwsapi.general.files.Storage.createMemoryDirectory;
import static com.jws.jwsapi.general.files.Storage.deleteCache;
import static com.jws.jwsapi.general.dialog.DialogUtil.dialogText;
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
import com.jws.jwsapi.R;
import com.jws.jwsapi.general.data.local.PreferencesManagerBase;
import com.jws.jwsapi.general.services.FtpInit;
import com.jws.jwsapi.general.services.httpserver.InitServer;
import com.jws.jwsapi.general.files.Storage;
import com.jws.jwsapi.general.user.UsersManager;
import com.jws.jwsapi.general.utils.Utils;
import java.io.IOException;
import javax.inject.Inject;
import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class MainActivity extends AppCompatActivity{
    public static String VERSION ="PH 470 BZA 1.00";
    public JwsManager jwsObject;
    public MainClass mainClass;
    public Storage storage;
    InitServer initServer;
    public PreferencesManagerBase preferencesManagerBase;
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
        mainClass = new MainClass(this,this,usersManager);
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
            dialogText(this, "¿Esta seguro de volver a los valores de fabrica del equipo?", "RESER", () -> {
                // Elimina la caché de la aplicación
                deleteCache(context);
                // Puedes agregar más código para eliminar otros datos específicos de tu aplicación si es necesario
                deleteDatabase(MainClass.DB_NAME);
                deleteDatabase("bza-database");
                jwsObject.jwsReboot("");
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
}


