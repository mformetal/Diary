package miles.diary.util;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

/**
 * Created by mbpeele on 1/18/16.
 */
public class StorageImpl implements Storage {

    private SharedPreferences preferences;

    private final static String SHARED_PREFS_KEY = "prefs";

    public StorageImpl(Context context) {
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
