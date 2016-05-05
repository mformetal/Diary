package miles.diary.data.api;

import android.app.Application;

import com.google.common.collect.ImmutableList;

import java.lang.reflect.Field;
import java.util.List;

import io.realm.Case;
import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import miles.diary.data.error.NoInternetException;
import miles.diary.data.error.NoRealmObjectKeyException;
import miles.diary.data.model.realm.RealmModel;
import miles.diary.data.model.realm.Profile;
import miles.diary.data.rx.DataObservable;
import miles.diary.data.rx.DataTransaction;
import rx.Observable;
import rx.Single;
import rx.functions.Func1;

/**
 * Created by mbpeele on 3/2/16.
 */
public class DataManagerImpl implements DataManager {

    private Application application;
    private Realm realm;

    public DataManagerImpl(Application application) {
        this.application = application;
    }

    @Override
    public void init() {
        realm = Realm.getDefaultInstance();
    }

    @Override
    public void close() {
        realm.close();
    }

    @Override
    public Observable<Profile> getProfile() {
        return exposeSearch(Profile.class)
                .findFirstAsync()
                .asObservable();
    }

    @Override
    public <T extends RealmObject> Observable<List<T>> getAll(Class<T> tClass) {
        return exposeSearch(tClass)
                .findAllAsync()
                .asObservable()
                .filter(new Func1<RealmResults<T>, Boolean>() {
                    @Override
                    public Boolean call(RealmResults<T> ts) {
                        return isDataValid(ts);
                    }
                })
                .map(new Func1<RealmResults<T>, List<T>>() {
                    @Override
                    public List<T> call(RealmResults<T> ts) {
                        return ImmutableList.copyOf(ts);
                    }
                })
                .first();
    }

    @Override
    public <T extends RealmObject> Observable<T> getObject(Class<T> tClass, long key) {
        try {
            Field fieldName = tClass.getDeclaredField(RealmModel.KEY);

            try {
                String classKey = (String) fieldName.get(null);

                return exposeSearch(tClass)
                        .equalTo(classKey, key)
                        .findFirst()
                        .asObservable();
            } catch (IllegalAccessException e) {
                return Observable.error(e);
            }
        } catch (NoSuchFieldException e) {
            return Observable.error(new NoRealmObjectKeyException());
        }
    }

    @Override
    public <T extends RealmObject> T get(Class<T> tClass, long key) {
        try {
            Field fieldName = tClass.getDeclaredField(RealmModel.KEY);

            try {
                String classKey = (String) fieldName.get(null);

                return exposeSearch(tClass)
                        .equalTo(classKey, key)
                        .findFirst();
            } catch (IllegalAccessException e) {
                return null;
            }
        } catch (NoSuchFieldException e) {
            throw new NoRealmObjectKeyException();
        }
    }

    @Override
    public <T extends RealmObject> Observable<T> uploadObject(T object) {
        if (hasConnection()) {
            return DataObservable.upload(object, realm);
        } else {
            return Observable.error(new NoInternetException());
        }
    }

    @Override
    public <T extends RealmObject> Observable<T> uploadObject(DataTransaction<T> dataTransaction) {
        if (hasConnection()) {
            return DataObservable.upload(dataTransaction, realm);
        } else {
            return Observable.error(new NoInternetException());
        }
    }

    @Override
    public <T extends RealmObject> Single<T> deleteObject(T object) {
        if (hasConnection()) {
            return DataObservable.delete(object, realm);
        } else {
            return Single.error(new NoInternetException());
        }
    }

    @Override
    public <T extends RealmObject> RealmQuery<T> exposeSearch(Class<T> tClass) {
        return realm.where(tClass);
    }

    @Override
    public <T extends RealmObject> Observable<List<T>> searchFieldnames(Class<T> tClass, String constraint,
                                                                        Case casing, boolean useOr,
                                                                        String... fieldNames) {
        if (fieldNames.length == 0) {
            throw new IllegalArgumentException("Must give some fieldNames as varargs searchFieldNames param");
        }

        RealmQuery<T> query = exposeSearch(tClass);

        query.beginGroup();
        for (String fieldName: fieldNames) {
            query.contains(fieldName, constraint, casing);

            if (useOr) {
                query.or();
            }
        }
        query.endGroup();

        return query.findAllAsync()
                .asObservable()
                .filter(new Func1<RealmResults<T>, Boolean>() {
                    @Override
                    public Boolean call(RealmResults<T> ts) {
                        return isDataValid(ts);
                    }
                })
                .map(new Func1<RealmResults<T>, List<T>>() {
                    @Override
                    public List<T> call(RealmResults<T> ts) {
                        return ImmutableList.copyOf(ts);
                    }
                })
                .first();
    }

    @Override
    public <T extends RealmObject> void delete(T object) {
        object.removeFromRealm();
    }

    @Override
    public void deleteAll() {
        realm.deleteAll();
    }

    @Override
    public <T extends RealmObject> Observable<T> updateObject(DataTransaction<T> transaction) {
        if (hasConnection()) {
            return DataObservable.update(transaction, realm);
        } else {
            return Observable.error(new NoInternetException());
        }
    }

    @Override
    public boolean isDataValid(RealmResults realmResults) {
        return realmResults.isValid() && realmResults.isLoaded();
    }

    @Override
    public boolean isDataValid(RealmObject realmObject) {
        return realmObject.isValid() && realmObject.isLoaded();
    }

    @Override
    public boolean hasConnection() {
        return !realm.isClosed();
    }
}
