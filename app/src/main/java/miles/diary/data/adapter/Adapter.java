package miles.diary.data.adapter;

import java.util.Collection;

import io.realm.RealmObject;

/**
 * Created by mbpeele on 3/11/16.
 */
interface Adapter<T> {

    Collection<?> getData();

    T getObject(int item);

    boolean isEmpty();

    boolean addData(T object);

    void addAtPosition(T object, int position);

    boolean addAll(Collection<T> objects);

    boolean removeObject(T object);

    void removeObject(int position);

    boolean isDataValid(T realmObject);

    void clear();
}
