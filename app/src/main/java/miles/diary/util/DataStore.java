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

    public <T> void set(String type, T payload, Class<T> clazz) {
        getEditor().putString(type, gson.toJson(payload, clazz));
        getEditor().commit();
    }

    public <T> void set(T payload, Class<T> clazz) {
        getEditor().putString(clazz.getSimpleName(), gson.toJson(payload, clazz));
        getEditor().commit();
    }

    public <T> void set(String type, List<T> payload, Class<T[]> clazz) {
        getEditor().putString(type, gson.toJson(payload.toArray(), clazz));
        getEditor().commit();
    }

    public <T> void set(String type, HashSet<T> payload, Class<T[]> clazz) {
        getEditor().putString(type, gson.toJson(payload.toArray(), clazz));
        getEditor().commit();
    }

    public <T> T get(String type, Class<T> clazz) {
        String json = preferences.getString(type, null);
        if (json == null) {
            return null;
        }
        return clazz.cast(gson.fromJson(json, clazz));
    }

    public <T> T get(Class<T> clazz) {
        String json = preferences.getString(clazz.getSimpleName(), null);
        if (json == null) {
            return null;
        }
        return clazz.cast(gson.fromJson(json, clazz));
    }

    public <T> List<T> getList(String type, Class<T[]> clazz) {
        String json = preferences.getString(type, null);
        if (json == null) {
            return null;
        }
        T[] objects = gson.fromJson(json, clazz);
        return Arrays.asList(objects);
    }

    public <T> T[] getPrimitiveList(String type, Class<T[]> clazz) {
        String json = preferences.getString(type, null);
        if (json == null) {
            return null;
        }
        return gson.fromJson(json, clazz);
    }

    public <T> List<T> getList(String type, Class<T[]> clazz, List<T> defaultValue) {
        String json = preferences.getString(type, null);
        if (json == null) {
            return defaultValue;
        }
        T[] objects = gson.fromJson(json, clazz);
        return Arrays.asList(objects);

    }

    public <T> HashSet<T> getHashSet(String type, Class<T[]> clazz, HashSet<T> defaultValue) {
        String json = preferences.getString(type, null);
        if (json == null) {
            return defaultValue;
        }
        T[] objects = gson.fromJson(json, clazz);

        return new HashSet<>(Arrays.asList(objects));

    }

    public <T> void updateSet(String type, T value, Class<T[]> clazz, boolean add) {
        HashSet<T> hashSet = getHashSet(type, clazz, new HashSet<T>());
        if (add) {
            if (!hashSet.add(value)) { // equals can not be able to force the replace
                hashSet.remove(value);
                hashSet.add(value);
            }
        } else {
            hashSet.remove(value);
        }
        set(type, hashSet, clazz);
    }


    public <T> T get(String type, Class<T> clazz, T defaultValue) {
        String json = preferences.getString(type, null);
        if (json == null) {
            return defaultValue;
        }
        return clazz.cast(gson.fromJson(json, clazz));
    }

    public void clearAll() {
        getEditor().clear();
        getEditor().commit();
    }

    public void clear(String... values) {
        if (values == null || values.length == 0) {
            return;
        }
        for (String value : values) {
            getEditor().remove(value);
        }
        getEditor().commit();
    }

    public void setBoolean(String type, Boolean payload) {
        getEditor().putBoolean(type, payload);
        getEditor().commit();
    }

    public void setLong(String type, long payload) {
        getEditor().putLong(type, payload);
        getEditor().commit();
    }

    public void setInt(String type, int payload) {
        getEditor().putInt(type, payload);
        getEditor().commit();
    }

    public void setFloat(String type, float payload) {
        getEditor().putFloat(type, payload);
        getEditor().commit();
    }

    public void setString(String type, String payload) {
        getEditor().putString(type, payload);
        getEditor().commit();
    }

    public boolean getBoolean(String type) {
        return preferences.getBoolean(type, false);
    }

    public long getLong(String type) {
        return preferences.getLong(type, 0);
    }

    public int getInt(String type) {
        return preferences.getInt(type, 0);
    }

    public float getFloat(String type) {
        return preferences.getFloat(type, 0.0f);
    }

    public String getString(String type) {
        return preferences.getString(type, null);
    }

    public String getString(String type, String defaultValue) {
        String result = getString(type);
        if (result == null) {
            return defaultValue;
        }
        return result;
    }
}
