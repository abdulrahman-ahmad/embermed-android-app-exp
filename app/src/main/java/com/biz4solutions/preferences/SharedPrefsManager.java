package com.biz4solutions.preferences;

import android.content.Context;
import android.content.SharedPreferences;

import com.biz4solutions.models.UserDTO;
import com.google.gson.Gson;

import java.io.Serializable;

public class SharedPrefsManager implements Serializable {

    private static SharedPrefsManager instance;

    private SharedPrefsManager() {
    }

    public static SharedPrefsManager getInstance() {
        if (instance == null) {
            instance = new SharedPrefsManager();
        }
        return instance;
    }

    public void clearPreference(Context context, String preferenceName) {
        SharedPreferences settings = context.getSharedPreferences(preferenceName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.clear();
        editor.apply();
    }

    public void storeUserPreference(Context context, String preferenceName, String key, UserDTO user) {
        SharedPreferences settings = context.getSharedPreferences(preferenceName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        String userJson = new Gson().toJson(user);
        editor.putString(key, userJson);
        editor.apply();
    }

    public UserDTO retrieveUserPreference(Context context, String preferenceName, String key) {
        SharedPreferences settings = context.getSharedPreferences(preferenceName, Context.MODE_PRIVATE);
        return new Gson().fromJson(settings.getString(key, ""), UserDTO.class);
    }

    public void storeBooleanPreference(Context context, String preferenceName, String key, boolean value) {
        if (context != null) {
            SharedPreferences settings = context.getSharedPreferences(preferenceName, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = settings.edit();
            editor.putBoolean(key, value);
            editor.apply();
        }
    }

    public boolean retrieveBooleanPreference(Context context, String preferenceName, String key) {
        if (context != null) {
            SharedPreferences settings = context.getSharedPreferences(preferenceName, Context.MODE_PRIVATE);
            return settings.getBoolean(key, false);
        } else {
            return false;
        }
    }

    public void storeStringPreference(Context context, String preferenceName, String key, String value) {
        SharedPreferences settings = context.getSharedPreferences(preferenceName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public String retrieveStringPreference(Context context, String preferenceName, String key) {
        SharedPreferences settings = context.getSharedPreferences(preferenceName, Context.MODE_PRIVATE);
        return settings.getString(key, "");
    }

    public void storeLongPreference(Context context, String preferenceName, String key, long value) {
        SharedPreferences settings = context.getSharedPreferences(preferenceName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putLong(key, value);
        editor.apply();
    }

    public long retrieveLongPreference(Context context, String preferenceName, String key) {
        SharedPreferences settings = context.getSharedPreferences(preferenceName, Context.MODE_PRIVATE);
        return settings.getLong(key, 0L);
    }

}