package com.jws.jwsapi.general.files;

import static com.jws.jwsapi.general.files.Storage.installApk;

import android.content.Context;
import android.os.Handler;

import java.io.File;

import javax.inject.Singleton;

@Singleton
public class StorageService {
    private final Context context;
    private static final Handler mHandler= new Handler();

    private int usbState =0;

    public StorageService(Context context) {
        this.context = context;

    }

    public void initService() {
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
        if (FilePaths.usbPaths.stream().anyMatch(File::isDirectory)) {
            for(File apk:FilePaths.apks){
                if(apk.exists()){
                    installApk(context);
                }
            }
            if(FilePaths.usbMultimediaPaths.stream().anyMatch(File::isDirectory)&& usbState ==0){
                UsbDialogHandler usbDialogHandler= new UsbDialogHandler(context);
                usbDialogHandler.showDialog();
                usbState =1;
            }
            if(FilePaths.usbMultimediaPaths.stream().noneMatch(File::isDirectory)&& usbState ==1){
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
