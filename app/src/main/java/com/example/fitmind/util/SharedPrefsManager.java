package com.example.fitmind.util;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPrefsManager {

    private static final String PREF_NAME = "fitmind_prefs";
    private static final String KEY_FIRST_LAUNCH = "first_launch";
    private static final String KEY_USER_ID = "user_id";

    private final SharedPreferences prefs;

    public SharedPrefsManager(Context context) {
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public boolean isFirstLaunch() {
        return prefs.getBoolean(KEY_FIRST_LAUNCH, true);
    }

    public void setFirstLaunchDone() {
        prefs.edit().putBoolean(KEY_FIRST_LAUNCH, false).apply();
    }

    public void saveUserId(int userId) {
        prefs.edit().putInt(KEY_USER_ID, userId).apply();
    }

    public int getUserId() {
        return prefs.getInt(KEY_USER_ID, -1);
    }

    public void clear() {
        prefs.edit().clear().apply();
    }
}
