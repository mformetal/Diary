package miles.diary.data.model.realm;

import io.realm.Sort;

/**
 * Created by mbpeele on 5/8/16.
 */
public class Sorter {

    public final String[] keys;
    public final Sort[] sorts;

    public Sorter(String[] keys, Sort[] sorts) {
        this.keys = keys;
        this.sorts = sorts;
    }

    public boolean hasInformation() {
        boolean keysCheck = keys != null && keys.length > 0;
        boolean sortsCheck = sorts != null && sorts.length > 0;
        return keysCheck && sortsCheck;
    }
}
