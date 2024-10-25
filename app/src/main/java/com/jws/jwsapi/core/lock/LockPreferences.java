package com.jws.jwsapi.core.lock;

import com.jws.jwsapi.core.data.local.PreferencesHelper;

import javax.inject.Inject;

public class LockPreferences {

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


}
