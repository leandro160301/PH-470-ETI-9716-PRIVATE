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

    @SuppressWarnings("unused")
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

    public static String format(String weight, int decimals) {
        String format = "0.";
        StringBuilder capacidadBuilder = new StringBuilder(format);
        for (int i = 0; i < decimals; i++) {
            capacidadBuilder.append("0");
        }
        format = capacidadBuilder.toString();
        DecimalFormat df = new DecimalFormat(format);

        double pesoNumero = Double.parseDouble(weight);
        return df.format(pesoNumero);
    }


    public static Bitmap drawableToBitmap(Drawable drawable) {
        Bitmap bitmap;
        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            if (bitmapDrawable.getBitmap() != null) {
                return bitmapDrawable.getBitmap();
            }
        }

        if (drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
            bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888);
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

    @SuppressWarnings("unused")
    public static String pointDecimalFormat(String number, int decimal) {
        try {
            Double.parseDouble(number);
        } catch (NumberFormatException e) {
            return "0000";
        }
        StringBuilder format = new StringBuilder("0");
        if (decimal > 0) {
            format.append(".");
            for (int i = 0; i < decimal; i++) {
                format.append("0");
            }
        }
        DecimalFormat decimalFormat = new DecimalFormat(format.toString());
        return decimalFormat.format(Double.parseDouble(number));
    }

    @SuppressWarnings("unused")
    public static int indexOfArray(String[] array, String target) {
        for (int i = 0; i < array.length; i++) {
            if (array[i].equals(target)) {
                return i;
            }
        }
        return -1;
    }
}