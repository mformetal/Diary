package miles.diary.data.api;

import java.util.List;

import io.realm.RealmObject;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import miles.diary.data.model.realm.Search;
import miles.diary.data.model.realm.Sorter;
import miles.diary.data.rx.DataTransaction;
import rx.Observable;
import rx.Single;

/**
 * Created by mbpeele on 3/2/16.
 */
public interface Repository {

    void open();

    void close();

    <T extends RealmObject> Observable<List<T>> getAll(Class<T> tClass);

    <T extends RealmObject> Observable<List<T>> getAllSorted(Class<T> tClass, Sorter sorter);

    <T extends RealmObject> Observable<T> getObject(Class<T> tClass, long key);

    <T extends RealmObject> T get(Class<T> tClass, long key);

    <T extends RealmObject> Observable<T> uploadObject(T object);

    <T extends RealmObject> Observable<T> updateObject(DataTransaction<T> dataTransaction);

    <T extends RealmObject> Observable<T> uploadObject(DataTransaction<T> dataTransaction);

    <T extends RealmObject> Single<Void> deleteObject(T object);

    <T extends RealmObject> Observable<List<T>> search(Class<T> tClass, Search search);

    void deleteAll();

    boolean isDataValid(RealmResults realmResults);

    boolean isDataValid(RealmObject realmObject);

    boolean hasConnection();
}
