package mformetal.diary.util;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by mbpeele on 1/18/16.
 */
public class SharedPreferencesStorage implements Storage {

    private SharedPreferences preferences;

    private final static String SHARED_PREFS_KEY = "prefs";

    public SharedPreferencesStorage(Context context) {
        preferences = context.getSharedPreferences(SHARED_PREFS_KEY, Context.MODE_PRIVATE);
    }

    private SharedPreferences.Editor getEditor() {
        return preferences.edit();
    }

    private SharedPreferences getPrefs() {
        return preferences;
    }

    @Override
    public String getString(String key, String defaultValue) {
        return getPrefs().getString(key, defaultValue);
    }

    @Override
    public void setString(String key, String value) {
        getEditor().putString(key, value).commit();
    }

    @Override
    public boolean getBoolean(String key, boolean defaultValue) {
        return getPrefs().getBoolean(key, defaultValue);
    }

    @Override
    public void setBoolean(String key, boolean value) {
        getEditor().putBoolean(key, value).commit();
    }
}
