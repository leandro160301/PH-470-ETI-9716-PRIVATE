package com.jws.jwsapi.utils.date;

import android.annotation.SuppressLint;

public class DateUtils {

    @SuppressLint("DefaultLocale")
    public static String formatDate(int day, int month, int year) {
        return String.format("%02d/%02d/%04d", day, month, year);
    }
}