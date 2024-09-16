package com.jws.jwsapi.general.storage;

import static com.jws.jwsapi.general.storage.Storage.installApk;

import android.content.Context;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;

import javax.inject.Singleton;

@Singleton
public class StorageService {
    private final Context context;
    private AppCompatActivity appCompatActivity;
    private static final Handler mHandler= new Handler();

    private int usbState =0;

    public StorageService(Context context) {
        this.context = context;
    }

    public void initService(AppCompatActivity appCompatActivity) {
        this.appCompatActivity = appCompatActivity;
        startUsbRead.run();
    }

    private final Runnable startUsbRead = new Runnable() {
        @Override
        public void run() {
            verificaMemoriaUSB();
            mHandler.postDelayed(this,1000);
        }
    };

    public void verificaMemoriaUSB(){
        if (StoragePaths.DIRECTORY_MEMORY_LIST.stream().anyMatch(File::isDirectory)) {
            for(File apk: StoragePaths.FILE_APK_LIST){
                if(apk.exists()){
                    installApk(context);
                }
            }
            if(StoragePaths.DIRECTORY_MEMORY_LIST.stream().anyMatch(File::isDirectory)&& usbState ==0){
                StorageDialogHandler storageDialogHandler = new StorageDialogHandler(appCompatActivity);
                storageDialogHandler.showDialog();
                usbState =1;
            }
            if(StoragePaths.DIRECTORY_MEMORY_LIST.stream().noneMatch(File::isDirectory)&& usbState ==1){
                usbState =0;
            }
        }else {
            usbState =0;
        }
    }

    public Boolean getUsbState(){
        return usbState == 1;
    }
}
