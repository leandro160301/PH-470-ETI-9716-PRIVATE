package com.jws.jwsapi.utils;

import android.content.Context;

public class CacheUtils {
    public static void deleteCache(Context context) {
        try {
            context.getCacheDir().deleteOnExit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
