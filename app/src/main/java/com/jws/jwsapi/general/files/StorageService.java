package com.jws.jwsapi.general.files;

import static com.jws.jwsapi.general.files.Storage.installApk;

import android.content.Context;
import android.os.Handler;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import javax.inject.Singleton;

@Singleton
public class StorageService {
    private final Context context;
    private static final Handler mHandler= new Handler();
    private final List<File> usbPaths = Arrays.asList(
            new File("/storage/udisk0"),
            new File("/storage/udisk1"),
            new File("/storage/udisk2")
    );
    private final List<File> apks = Arrays.asList(
            new File("/storage/udisk0/instalacion/jwsapi.apk"),
            new File("/storage/udisk1/instalacion/jwsapi.apk"),
            new File("/storage/udisk2/instalacion/jwsapi.apk")
    );
    private final List<File> usbMultimediaPaths = Arrays.asList(
            new File("/storage/udisk0"),
            new File("/storage/udisk1"),
            new File("/storage/udisk2")
    );
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
        if (usbPaths.stream().anyMatch(File::isDirectory)) {
            for(File apk:apks){
                if(apk.exists()){
                    installApk(context);
                }
            }
            if(usbMultimediaPaths.stream().anyMatch(File::isDirectory)&& usbState ==0){
                UsbDialogHandler usbDialogHandler= new UsbDialogHandler(context);
                usbDialogHandler.dialogoUSB();
                usbState =1;
            }
            if(usbMultimediaPaths.stream().noneMatch(File::isDirectory)&& usbState ==1){
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
