package mformetal.diary.util;

/**
 * Created by mbpeele on 5/8/16.
 */
public interface Storage {

    String getString(String key, String defaultValue);

    void setString(String key, String value);

    boolean getBoolean(String key, boolean defaultValue);

    void setBoolean(String key, boolean value);
}
