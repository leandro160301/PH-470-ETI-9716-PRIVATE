package com.jws.jwsapi.core.lock;

import static com.jws.jwsapi.core.lock.LockConstants.CODE_1;
import static com.jws.jwsapi.core.lock.LockConstants.CODE_2;
import static com.jws.jwsapi.core.lock.LockConstants.CODE_3;
import static com.jws.jwsapi.core.lock.LockConstants.CODE_4;

import android.annotation.SuppressLint;

import com.jws.jwsapi.utils.date.DateUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

public class LockManager {
    private final LockPreferences preferences;

    @Inject
    public LockManager(LockPreferences preferences) {
        this.preferences = preferences;
    }

    private static Integer compareDate(String currentDate, String lockDate) {
        if (lockDate.isEmpty()) return 1000;
        @SuppressLint("SimpleDateFormat") SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");

        try {
            Date formatCurrentDate = format.parse(currentDate);
            Date formatDate = format.parse(lockDate);

            long differenceMiliseconds = formatDate.getTime() - formatCurrentDate.getTime();

            return (int) TimeUnit.DAYS.convert(differenceMiliseconds, TimeUnit.MILLISECONDS);
        } catch (ParseException e) {
            e.printStackTrace();
            return 0;
        }
    }

    public static String calculateLockDay(String days) {
        Calendar calendar = Calendar.getInstance();
        int daysNumber = Integer.parseInt(days);
        calendar.add(Calendar.DAY_OF_YEAR, daysNumber);
        Date newDate = calendar.getTime();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        return dateFormat.format(newDate);
    }

    private static String getStandarCode(int code, int number) {
        return String.valueOf(((code + number) * 6) / 4);
    }

    public static String getCode1(int code) {
        return getStandarCode(code, CODE_1);
    }

    public static String getCode2(int code) {
        return getStandarCode(code, CODE_2);
    }

    public static String getCode3(int code) {
        return getStandarCode(code, CODE_3);
    }

    public static String getCode4(int code) {
        return getStandarCode(code, CODE_4);
    }

    public boolean isLocked() {
        return compareDate(preferences.getLastDate(), preferences.getDay()) <= 0;
    }

    public Integer remainingDays() {
        return compareDate(preferences.getLastDate(), preferences.getDay());
    }

    public void updateDate() {
        if (preferences.getDay().isEmpty()) {
            preferences.setLastDate(DateUtils.getDate());
            return;
        }
        int verify = compareDate(DateUtils.getDate(), preferences.getLastDate());
        if (verify > 0) {
            preferences.setDay(calculateLockDay("0"));
        } else {
            preferences.setLastDate(DateUtils.getDate());
        }
    }

    public boolean isLockedEnabled() {
        return !preferences.getDay().isEmpty();
    }

}
