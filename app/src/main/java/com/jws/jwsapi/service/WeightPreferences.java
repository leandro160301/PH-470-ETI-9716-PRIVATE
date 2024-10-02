package com.jws.jwsapi.service;

import com.jws.jwsapi.core.data.local.PreferencesHelper;

import javax.inject.Inject;

public class WeightPreferences {

    private static final String PREF_STABLE_COUNT_THRESHOLD = "stable_count_threshold";
    private static final String PREF_ZERO_BAND = "zero_band";
    private static final int DEFAULT_STABLE_COUNT_THRESHOLD = 15;
    private static final double DEFAULT_ZERO_BAND = 49.0;

    private final PreferencesHelper preferencesHelper;

    @Inject
    public WeightPreferences(PreferencesHelper preferencesHelper) {
        this.preferencesHelper = preferencesHelper;
    }

    public int getStableCountThreshold() {
        return preferencesHelper.getInt(PREF_STABLE_COUNT_THRESHOLD, DEFAULT_STABLE_COUNT_THRESHOLD);
    }

    public void setStableCountThreshold(int threshold) {
        preferencesHelper.putInt(PREF_STABLE_COUNT_THRESHOLD, threshold);
    }

    public double getZeroBand() {
        return Double.longBitsToDouble(preferencesHelper.getLong(PREF_ZERO_BAND, Double.doubleToRawLongBits(DEFAULT_ZERO_BAND)));
    }

    public void setZeroBand(double zeroBand) {
        preferencesHelper.putLong(PREF_ZERO_BAND, Double.doubleToRawLongBits(zeroBand));
    }
}