package miles.diary.data.api.db;

import java.util.List;

import io.realm.RealmObject;
import io.realm.RealmResults;
import miles.diary.data.rx.DataTransaction;
import rx.Observable;

/**
 * Created by mbpeele on 3/2/16.
 */
public interface DataManagerInterface {

    void init();

    void close();

    <T extends RealmObject> Observable<List<T>> loadObjects(Class<T> tClass);

    <T extends RealmObject> Observable<T> getObject(Class<T> tClass, long key);

    <T extends RealmObject> Observable<T> uploadObject(T object);

    <T extends RealmObject> Observable<T> deleteObject(T object);

    <T extends RealmObject> Observable<T> updateObject(DataTransaction<T> dataTransaction);

    boolean isDataValid(RealmResults realmResults);

    boolean isDataValid(RealmObject realmObject);

    boolean hasConnection();
}
