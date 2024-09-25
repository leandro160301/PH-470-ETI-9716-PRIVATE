package com.jws.jwsapi;

import static com.jws.jwsapi.core.storage.Storage.createMemoryDirectory;
import static com.jws.jwsapi.core.storage.Storage.deleteCache;
import static com.jws.jwsapi.core.user.UserConstants.ROLE_SUPERVISOR;
import static com.jws.jwsapi.dialog.DialogUtil.dialogText;

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
import com.jws.jwsapi.core.services.FtpInit;
import com.jws.jwsapi.core.services.httpserver.InitServer;
import com.jws.jwsapi.core.storage.StorageService;
import com.jws.jwsapi.core.user.UserManager;
import com.jws.jwsapi.shared.UserRepository;
import com.jws.jwsapi.utils.ToastHelper;
import com.jws.jwsapi.utils.Utils;

import java.io.IOException;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class MainActivity extends AppCompatActivity {
    public static final String VERSION = "PH 470 BZA 1.00";
    public JwsManager jwsObject;
    public MainClass mainClass;
    private InitServer initServer;
    @Inject
    UserManager userManager;
    @Inject
    UserRepository userRepository;
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
        initService();
        initMainClass();
        initPendrive();

    }

    private void initPendrive() {
        storageService.initService(this);
    }

    private void initService() {
        try {
            FtpInit ftpInit = new FtpInit(getApplicationContext(), userRepository.getUsers(), preferencesManager);
            ftpInit.ftpServer();
        } catch (Exception e) {
            e.printStackTrace();
        }
        initServer = new InitServer(this, this, userManager, preferencesManager);
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
        mainClass = new MainClass(this, this, userRepository);
        mainClass.init();
    }

    private void updateViews() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        WallpaperManager wallpaperManager = WallpaperManager.getInstance(getApplicationContext());
        Drawable drawable = ContextCompat.getDrawable(getApplicationContext(), R.drawable.blanco);
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
        if (preferencesManager.getTheme() != R.style.AppTheme_NoActionBar) {
            try {
                setTheme(preferencesManager.getTheme());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void clearCache() {
        Context context = getApplicationContext();
        if (userRepository.getLevelUser() > ROLE_SUPERVISOR) {
            dialogText(this, getString(R.string.dialog_delete_cache), getString(R.string.dialog_button_text_delete_cache), () -> {
                deleteCache(context);
                deleteDatabase(MainClass.DB_NAME);
                deleteDatabase("bza-database");
                jwsObject.jwsReboot("");
            });
        } else {
            ToastHelper.message(getString(R.string.toast_login_error_delete_cache), R.layout.item_customtoasterror, this);
        }
    }

    public void openSettings() {
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
    public void onDestroy() {
        super.onDestroy();
    }
}


