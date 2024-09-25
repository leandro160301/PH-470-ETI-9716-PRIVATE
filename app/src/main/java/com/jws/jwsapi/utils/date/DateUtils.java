package com.jws.jwsapi.utils.date;

import android.annotation.SuppressLint;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DateUtils {

    @SuppressLint("DefaultLocale")
    public static String formatDate(int day, int month, int year) {
        return String.format("%02d/%02d/%04d", day, month, year);
    }

    public static String getHour() {
        Calendar calendar = Calendar.getInstance();
        int hour24hrs = calendar.get(Calendar.HOUR_OF_DAY);
        int minutes = calendar.get(Calendar.MINUTE);
        int seconds = calendar.get(Calendar.SECOND);
        String hora24 = String.valueOf(hour24hrs);
        String minutos = String.valueOf(minutes);
        String segundos = String.valueOf(seconds);
        return hora24 + ":" + minutos + ":" + segundos;
    }

    public static String getDate() {
        return new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date());
    }

    public static String getDateDDMMYYYY() {
        return new SimpleDateFormat("yyyy/MM/dd", Locale.getDefault()).format(new Date());
    }

}