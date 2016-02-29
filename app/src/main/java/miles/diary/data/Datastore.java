package miles.diary.data;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;

/**
 * Created by mbpeele on 1/18/16.
 */
public class DataStore {

    private Gson gson;
    private SharedPreferences preferences;

    private final static String SHARED_PREFS_KEY = "prefs";

    public DataStore(Application application) {
        gson = new Gson();
        preferences = application.getSharedPreferences(SHARED_PREFS_KEY, Context.MODE_PRIVATE);
    }

    private SharedPreferences.Editor getEditor() {
        return preferences.edit();
    }

    private SharedPreferences getPrefs() {
        return preferences;
    }
}
