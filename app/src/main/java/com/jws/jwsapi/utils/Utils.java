package com.jws.jwsapi.utils;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import java.text.DecimalFormat;

public class Utils {

    @SuppressWarnings("all")
    public static boolean isNumeric(String strNum) {
        if (strNum == null) {
            return false;
        }
        try {
            double d = Double.parseDouble(strNum);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }


    public static boolean isLong(String strNum) {
        if (strNum == null) {
            return false;
        }
        try {
            long l = Long.parseLong(strNum);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }

    public static String format(String peso, int decimales) {
        String formato = "0.";
        StringBuilder capacidadBuilder = new StringBuilder(formato);
        for (int i = 0; i < decimales; i++) {
            capacidadBuilder.append("0");
        }
        formato = capacidadBuilder.toString();
        DecimalFormat df = new DecimalFormat(formato);

        double pesoNumero = Double.parseDouble(peso);
        return df.format(pesoNumero);
    }


    public static Bitmap drawableToBitmap(Drawable drawable) {
        Bitmap bitmap = null;
        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            if (bitmapDrawable.getBitmap() != null) {
                return bitmapDrawable.getBitmap();
            }
        }

        if (drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
            bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888); // Single color bitmap will be created of 1x1 pixel
        } else {
            bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        }

        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }

    public static int randomNumber() {
        int min = 1000;
        int max = 9999;
        return (int) Math.floor(Math.random() * (max - min + 1) + min);
    }

    public static String pointDecimalFormat(String numero, int decimales) {
        try {
            Double.parseDouble(numero);
        } catch (NumberFormatException e) {
            return "0000";
        }
        StringBuilder format = new StringBuilder("0");
        if (decimales > 0) {
            format.append(".");
            for (int i = 0; i < decimales; i++) {
                format.append("0");
            }
        }
        DecimalFormat decimalFormat = new DecimalFormat(format.toString());
        return decimalFormat.format(Double.parseDouble(numero));
    }

    public static int indexOfArray(String[] array, String target) {
        for (int i = 0; i < array.length; i++) {
            if (array[i].equals(target)) {
                return i;
            }
        }
        return -1;
    }
}