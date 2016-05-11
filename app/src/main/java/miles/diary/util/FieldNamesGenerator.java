package miles.diary.util;

import java.lang.reflect.Field;

import io.realm.RealmModel;

/**
 * Created by mbpeele on 5/10/16.
 */
public class FieldNamesGenerator {

    public static void test(Class<?> clazz) {
        if (clazz.isAssignableFrom(RealmModel.class)) {
            Field[] fields = clazz.getDeclaredFields();
            for (Field field: fields) {
                String name = field.getName();
            }
        } else {
            throw new IllegalArgumentException("Attempt to use FieldNamesGenerator on a non-RealmModel class");
        }
    }
}
