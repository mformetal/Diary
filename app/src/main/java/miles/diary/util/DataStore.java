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
public class DataStore {

    private SharedPreferences preferences;

    private final static String SHARED_PREFS_KEY = "prefs";

    public DataStore(Context context) {
        preferences = context.getSharedPreferences(SHARED_PREFS_KEY, Context.MODE_PRIVATE);
    }

    private SharedPreferences.Editor getEditor() {
        return preferences.edit();
    }

    private SharedPreferences getPrefs() {
        return preferences;
    }

    public void setFirstTimeUser(boolean firstTimeUser) {
        getEditor().putBoolean("firstTimeUser", firstTimeUser).apply();
    }

    public boolean isFirstTimeUser() {
        return getPrefs().getBoolean("firstTimeUser", true);
    }
}
