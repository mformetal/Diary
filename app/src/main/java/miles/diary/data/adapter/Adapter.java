package miles.diary.data.adapter;

import java.util.Collection;
import java.util.List;

import io.realm.RealmObject;

/**
 * Created by mbpeele on 3/11/16.
 */
interface Adapter<T> {

    boolean animateContentsChanging();

    List<T> getData();

    void setData(List<T> collection);

    T getObject(int item);

    boolean isEmpty();

    boolean addObject(T object);

    void addAtPosition(T object, int position);

    boolean addAll(Collection<T> objects);

    boolean removeObject(T object);

    void removeObject(int position);

    boolean isObjectValid(T realmObject);

    void clear();
}
