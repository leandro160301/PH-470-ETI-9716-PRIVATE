package com.jws.jwsapi.utils;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;

import com.jws.jwsapi.MainActivity;

public class PackageUtils {
    public static boolean isPackageExisted(String targetPackage, Context context) {
        PackageManager pm = context.getPackageManager();
        try {
            pm.getPackageInfo(targetPackage, PackageManager.GET_META_DATA);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    public static void installApk(Context context, MainActivity mainActivity) {
        if (isPackageExisted("com.android.documentsui", context)) {
            Intent launchIntent = context.getPackageManager().getLaunchIntentForPackage("com.android.documentsui");
            if (launchIntent != null) {
                launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(launchIntent);
                mainActivity.finish();
                System.exit(0);
                mainActivity.onDestroy();
            }
        }
    }
}
