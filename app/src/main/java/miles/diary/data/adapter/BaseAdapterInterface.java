package miles.diary.data.adapter;

import java.util.Collection;

import io.realm.RealmObject;

/**
 * Created by mbpeele on 3/11/16.
 */
public interface BaseAdapterInterface {

    Collection<?> getData();

    RealmObject getObject(int item);

    boolean isEmpty();

    <T extends RealmObject> boolean addData(T object);

    boolean addAll(Collection<? extends RealmObject> objects);

    <T extends RealmObject> boolean removeObject(T object);

    void removeObject(int position);

    boolean isDataValid(RealmObject realmObject);

    void clear();
}
