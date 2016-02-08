package miles.diary.data;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;

/**
 * Created by mbpeele on 1/18/16.
 */
public class DataStore {

    private SharedPreferences preferences;

    private final static String SHARED_PREFS_KEY = "prefs";

    public DataStore(Application application) {
        preferences = application.getSharedPreferences(SHARED_PREFS_KEY, Context.MODE_PRIVATE);
    }

    private SharedPreferences.Editor getEditor() {
        return preferences.edit();
    }

    private SharedPreferences getPrefs() {
        return preferences;
    }
}
