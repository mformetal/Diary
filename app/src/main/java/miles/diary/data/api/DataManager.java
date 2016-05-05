package miles.diary.data.api;

import android.util.Pair;

import java.util.List;

import io.realm.Case;
import io.realm.RealmObject;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import miles.diary.data.model.realm.Profile;
import miles.diary.data.rx.DataTransaction;
import rx.Observable;
import rx.Single;

/**
 * Created by mbpeele on 3/2/16.
 */
interface DataManager {

    void init();

    void close();

    Observable<Profile> getProfile();

    <T extends RealmObject> Observable<List<T>> getAll(Class<T> tClass);

    <T extends RealmObject> Observable<T> getObject(Class<T> tClass, long key);

    <T extends RealmObject> T get(Class<T> tClass, long key);

    <T extends RealmObject> Observable<T> uploadObject(T object);

    <T extends RealmObject> Observable<T> updateObject(DataTransaction<T> dataTransaction);

    <T extends RealmObject> Observable<T> uploadObject(DataTransaction<T> dataTransaction);

    <T extends RealmObject> Single<T> deleteObject(T object);

    <T extends RealmObject> RealmQuery<T> exposeSearch(Class<T> tClass);

    <T extends RealmObject> Observable<List<T>> searchFieldnames(Class<T> tClass, String constraint,
                                                                 Case casing, boolean useOr,
                                                                 String... fieldNames);

    <T extends RealmObject> void delete(T object);

    void deleteAll();

    boolean isDataValid(RealmResults realmResults);

    boolean isDataValid(RealmObject realmObject);

    boolean hasConnection();
}
