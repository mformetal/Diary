package miles.diary.util;

import java.util.List;

import rx.functions.Func0;
import rx.functions.Func1;
import rx.functions.Func2;

/**
 * Created by mbpeele on 3/27/16.
 */
public class ListUtils {

    public static <T> List<T> filter(List<T> list, Func1<T, Boolean> func1) {
        for (T object: list) {
            if (func1.call(object)) {
                list.remove(object);
            }
        }
        return list;
    }
}
