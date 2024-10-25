package com.jws.jwsapi.core.lock;

import android.annotation.SuppressLint;

import com.jws.jwsapi.utils.date.DateUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

public class LockManager {
    private final LockPreferences preferences;

    @Inject
    public LockManager(LockPreferences preferences) {
        this.preferences = preferences;
    }

    public boolean isLocked() {
        return compareDate(DateUtils.getDate(), preferences.getDay())<=0;
    }

    public int remainingDays() {
        return compareDate(DateUtils.getDate(), preferences.getDay());
    }

    public boolean isLockedEnabled() {
        return !preferences.getDay().isEmpty();
    }

    private static int compareDate(String currentDate, String date) {
        if (date.isEmpty()) return 1000;
        @SuppressLint("SimpleDateFormat") SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");

        try {
            Date formatCurrentDate = format.parse(currentDate);
            Date formatDate = format.parse(date);

            long differenceMiliseconds = formatDate.getTime() - formatCurrentDate.getTime();

            return (int) TimeUnit.DAYS.convert(differenceMiliseconds, TimeUnit.MILLISECONDS);
        } catch (ParseException e) {
            e.printStackTrace();
            return 0;
        }
    }

}
