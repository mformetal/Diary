package miles.diary.data.api.db;

import android.app.Application;

import com.google.common.collect.ImmutableList;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Objects;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.RealmResults;
import miles.diary.data.error.NoInternetException;
import miles.diary.data.error.NoRealmObjectKeyException;
import miles.diary.data.model.realm.IRealmInterface;
import miles.diary.data.rx.DataObservable;
import miles.diary.data.rx.DataTransaction;
import rx.Observable;
import rx.functions.Func1;

/**
 * Created by mbpeele on 3/2/16.
 */
public class DataManager implements DataManagerInterface {

    private Application application;
    private Realm realm;

    public DataManager(Application application) {
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
    public <T extends RealmObject> Observable<List<T>> loadObjects(Class<T> tClass) {
        return realm.where(tClass)
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
        for (Field field: tClass.getDeclaredFields()) {
            String name = field.getName();
            if (Objects.equals(name, IRealmInterface.CLASS_KEY)) {
                try {
                    String classKey = (String) field.get(null);

                    return realm.where(tClass)
                            .equalTo(classKey, key)
                            .findFirst()
                            .asObservable();
                } catch (IllegalAccessException e) {
                    return Observable.error(e);
                }
            }
        }

        return Observable.error(new NoRealmObjectKeyException());
    }

    @Override
    public <T extends RealmObject> T get(Class<T> tClass, long key) {
        for (Field field: tClass.getDeclaredFields()) {
            String name = field.getName();
            if (Objects.equals(name, IRealmInterface.CLASS_KEY)) {
                try {
                    String classKey = (String) field.get(null);

                    return realm.where(tClass)
                            .equalTo(classKey, key)
                            .findFirst();
                } catch (IllegalAccessException e) {
                    return null;
                }
            }
        }

        throw new NoRealmObjectKeyException();
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
    public <T extends RealmObject> Observable<T> deleteObject(T object) {
        if (hasConnection()) {
            return DataObservable.delete(object, realm);
        } else {
            return Observable.error(new NoInternetException());
        }
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
