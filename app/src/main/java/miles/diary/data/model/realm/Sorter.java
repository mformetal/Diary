package miles.diary.data.model.realm;

import java.util.LinkedList;
import java.util.List;

import io.realm.Sort;

/**
 * Created by mbpeele on 5/8/16.
 */
public class Sorter {

    public final String[] fieldNames;
    public final Sort[] sortOrders;

    public Sorter(String[] fieldNames, Sort[] sortOrders) {
        this.fieldNames = fieldNames;
        this.sortOrders = sortOrders;
    }

    public boolean hasInformation() {
        boolean keysCheck = fieldNames != null && fieldNames.length > 0;
        boolean sortsCheck = sortOrders != null && sortOrders.length > 0;
        return keysCheck && sortsCheck;
    }
}
