package com.jws.jwsapi.core.services.httpserver;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.projection.MediaProjectionManager;
import android.os.IBinder;
import android.provider.Settings;

import androidx.core.content.ContextCompat;

import com.jws.jwsapi.MainActivity;
import com.jws.jwsapi.core.data.local.PreferencesManager;
import com.jws.jwsapi.core.user.UserManager;


public class InitServer {

    private final Context context;
    private AppService appService;
    private final PermissionHelper permissionHelper;
    private ServiceConnection serviceConnection;
    MainActivity mainActivity;
    UserManager userManager;
    PreferencesManager preferencesManagerBase;

    public InitServer(Context context, MainActivity mainActivity, UserManager userManager, PreferencesManager preferencesManager) {
        this.context = context;
        this.preferencesManagerBase = preferencesManager;
        this.mainActivity=mainActivity;
        this.permissionHelper = new PermissionHelper((Activity) context, new OnPermissionGrantedListener());
        this.userManager = userManager;
    }

    public void start() {
        if (AppService.isServiceRunning()) {
            bindService();
            return;
        }
        permissionHelper.requestInternetPermission();
    }

    private boolean isAccessibilityServiceEnabled() {
        ComponentName compName = new ComponentName(context, MouseAccessibilityService.class);
        String flatName = compName.flattenToString();
        String enabledList = Settings.Secure.getString(context.getContentResolver(),
                Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);
        return enabledList != null && enabledList.contains(flatName);
    }

    private void bindService() {
        Intent serviceIntent = new Intent(context, AppService.class);
        serviceConnection = new AppServiceConnection();
        context.bindService(serviceIntent, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    public void startService() {
        Intent serviceIntent = new Intent(context, AppService.class);
        ContextCompat.startForegroundService(context, serviceIntent);
        serviceConnection = new AppServiceConnection();
        context.bindService(serviceIntent, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    private void enableAccessibilityService() {
        if (appService != null)
            appService.accessibilityServiceSet(context, true);
    }

    private void askMediaProjectionPermission() {
        MediaProjectionManager mediaProjectionManager = (MediaProjectionManager)
                context.getSystemService(Context.MEDIA_PROJECTION_SERVICE);
        ((Activity) context).startActivityForResult(mediaProjectionManager.createScreenCaptureIntent(),
                101);
    }

    private class AppServiceConnection implements ServiceConnection {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            AppService.AppServiceBinder binder = (AppService.AppServiceBinder) service;
            appService = binder.getService();

            if (!appService.isServerRunning())
                askMediaProjectionPermission();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            appService = null;
        }
    }

    private class OnPermissionGrantedListener implements
            PermissionHelper.OnPermissionGrantedListener {

        @Override
        public void onAccessNetworkStatePermissionGranted(boolean isGranted) {
            if (!isGranted)
                return;
        }

        @Override
        public void onInternetPermissionGranted(boolean isGranted) {
            permissionHelper.requestReadExternalStoragePermission();
        }

        @Override
        public void onReadExternalStoragePermissionGranted(boolean isGranted) {
            permissionHelper.requestWakeLockPermission();
        }

        @Override
        public void onWakeLockPermissionGranted(boolean isGranted) {
            permissionHelper.requestForegroundServicePermission();
        }

        @Override
        public void onForegroundServicePermissionGranted(boolean isGranted) {
            if (isGranted) {
                startService();
            }
        }

        @Override
        public void onRecordAudioPermissionGranted(boolean isGranted) {}

        @Override
        public void onCameraPermissionGranted(boolean isGranted) {}
    }

    public void handleActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 101: // PERM_MEDIA_PROJECTION_SERVICE
                if (resultCode == Activity.RESULT_OK) {
                    Runnable myRunnable = () -> {
                        if (appService != null) {
                            if (!appService.serverStart(data, 8001,
                                    isAccessibilityServiceEnabled(), context, mainActivity, userManager,preferencesManagerBase)) {
                                return;
                            }
                        }
                    };
                    Thread myThread = new Thread(myRunnable);
                    myThread.start();
                }
                break;
            case 102: // PERM_ACTION_ACCESSIBILITY_SERVICE
                if (isAccessibilityServiceEnabled())
                    enableAccessibilityService();
                break;
        }
    }
}