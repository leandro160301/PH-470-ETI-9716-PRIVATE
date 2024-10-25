package com.jws.jwsapi.core.lock;

import com.jws.jwsapi.core.data.local.PreferencesHelper;
import com.jws.jwsapi.utils.date.DateUtils;

import javax.inject.Inject;

public class LockPreferences {

    private static final String LAST_CHECK_TIMESTAMP = "last_check_timestamp";
    private final PreferencesHelper preferencesHelper;

    @Inject
    public LockPreferences(PreferencesHelper preferencesHelper) {
        this.preferencesHelper = preferencesHelper;
    }

    public String getDay() {
        return preferencesHelper.getString("LOCKER_DAY", "");
    }

    public void setDay(String day) {
        preferencesHelper.putString("LOCKER_DAY", day);
    }

    public String getLastDate() {
        return preferencesHelper.getString(LAST_CHECK_TIMESTAMP, DateUtils.getDate());
    }

    public void setLastDate(String day) {
        preferencesHelper.putString(LAST_CHECK_TIMESTAMP, day);
    }


}
