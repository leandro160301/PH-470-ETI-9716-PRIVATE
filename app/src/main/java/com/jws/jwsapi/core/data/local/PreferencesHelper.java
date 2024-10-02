package com.jws.jwsapi.core.data.local;

import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class PreferencesHelper {

    private final SharedPreferences sharedPreferences;
    private final Gson gson;
    private final SharedPreferences.Editor editor;

    @Inject
    public PreferencesHelper(SharedPreferences sharedPreferences) {
        this.sharedPreferences = sharedPreferences;
        this.gson = new Gson();
        this.editor = sharedPreferences.edit();
    }

    public void putInt(String key, int value) {
        editor.putInt(key, value);
        editor.apply();
    }

    public int getInt(String key, int defaultValue) {
        return sharedPreferences.getInt(key, defaultValue);
    }

    public void putString(String key, String value) {
        editor.putString(key, value);
        editor.apply();
    }

    public String getString(String key, String defaultValue) {
        return sharedPreferences.getString(key, defaultValue);
    }

    public void putBoolean(String key, boolean value) {
        editor.putBoolean(key, value);
        editor.apply();
    }

    public boolean getBoolean(String key, boolean defaultValue) {
        return sharedPreferences.getBoolean(key, defaultValue);
    }

    public void putFloat(String key, float value) {
        editor.putFloat(key, value);
        editor.apply();
    }

    public float getFloat(String key, float defaultValue) {
        return sharedPreferences.getFloat(key, defaultValue);
    }

    public long getLong(String key, long value) {
        return sharedPreferences.getLong(key, value);
    }

    public void putLong(String key, long value) {
        editor.putLong(key, value);
        editor.apply();
    }

    public void putIntegerList(String key, List<Integer> list) {
        String json = gson.toJson(list);
        editor.putString(key, json);
        editor.apply();
    }

    public List<Integer> getIntegerList(String key) {
        String json = sharedPreferences.getString(key, null);
        if (json == null) return null;
        Type type = new TypeToken<List<Integer>>() {
        }.getType();
        return gson.fromJson(json, type);
    }

    public void putStringList(String key, List<String> list) {
        String json = gson.toJson(list);
        editor.putString(key, json);
        editor.apply();
    }

    public List<String> getStringList(String key) {
        String json = sharedPreferences.getString(key, null);
        if (json == null) return null;
        Type type = new TypeToken<List<String>>() {
        }.getType();
        return gson.fromJson(json, type);
    }
}