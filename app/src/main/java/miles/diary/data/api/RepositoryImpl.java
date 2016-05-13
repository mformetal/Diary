package miles.diary.data.api;

import com.google.common.collect.ImmutableList;

import java.lang.reflect.Field;
import java.util.List;

import javax.inject.Singleton;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import miles.diary.data.error.NoRealmObjectKeyException;
import miles.diary.data.error.RealmClosedException;
import miles.diary.data.model.realm.RealmModel;
import miles.diary.data.model.realm.Search;
import miles.diary.data.model.realm.Sorter;
import miles.diary.data.rx.RealmObservable;
import miles.diary.data.rx.RealmTransaction;
import rx.Observable;
import rx.Single;
import rx.functions.Func1;

/**
 * Created by mbpeele on 3/2/16.
 */
@Singleton
public class RepositoryImpl implements Repository {

    private Realm realm;

    @Override
    public void open() {
        realm = Realm.getDefaultInstance();
    }

    @Override
    public void close() {
        if (!realm.isClosed()) {
            realm.close();
        }
    }

    @Override
    public <T extends RealmObject> Observable<List<T>> getAll(Class<T> tClass) {
        return exposeSearch(tClass)
                .findAllAsync()
                .asObservable()
                .compose(this.<T>applyTransformer());
    }

    @Override
    public <T extends RealmObject> Observable<List<T>> getAllSorted(Class<T> tClass, Sorter sorter) {
        return exposeSearch(tClass)
                .findAllSortedAsync(sorter.fieldNames, sorter.sortOrders)
                .asObservable()
                .compose(this.<T>applyTransformer());
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
            return RealmObservable.upload(object, realm);
        } else {
            return Observable.error(new RealmClosedException());
        }
    }

    @Override
    public <T extends RealmObject> Observable<T> uploadObject(RealmTransaction<T> realmTransaction) {
        if (hasConnection()) {
            return RealmObservable.upload(realmTransaction, realm);
        } else {
            return Observable.error(new RealmClosedException());
        }
    }

    @Override
    public <T extends RealmObject> Single<Void> deleteObject(T object) {
        if (hasConnection()) {
            return RealmObservable.delete(object, realm);
        } else {
            return Single.error(new RealmClosedException());
        }
    }

    @Override
    public <T extends RealmObject> Observable<List<T>> search(Class<T> tClass, Search search) {
        RealmQuery<T> query = exposeSearch(tClass);

        query.beginGroup();
        for (String fieldName : search.fieldNames) {
            query.contains(fieldName, search.constraint, search.casing);

            if (search.useOr) {
                query.or();
            }
        }
        query.endGroup();

        Sorter sorter = search.sorter;
        if (sorter.hasInformation()) {
            return query.findAllSortedAsync(sorter.fieldNames, sorter.sortOrders)
                    .asObservable()
                    .compose(this.<T>applyTransformer());
        } else {
            return query.findAllAsync()
                    .asObservable()
                    .compose(this.<T>applyTransformer());
        }
    }

    @Override
    public void deleteAll() {
        realm.deleteAll();
    }

    @Override
    public <T extends RealmObject> Observable<T> updateObject(RealmTransaction<T> transaction) {
        if (hasConnection()) {
            return RealmObservable.update(transaction, realm);
        } else {
            return Observable.error(new RealmClosedException());
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

    private  <T extends RealmObject> RealmQuery<T> exposeSearch(Class<T> tClass) {
        return realm.where(tClass);
    }

    private <C extends RealmObject> Observable.Transformer<RealmResults<C>, List<C>> applyTransformer() {
        return new Observable.Transformer<RealmResults<C>, List<C>>() {
            @Override
            public Observable<List<C>> call(Observable<RealmResults<C>> realmResultsObservable) {
                return realmResultsObservable.filter(new Func1<RealmResults<C>, Boolean>() {
                    @Override
                    public Boolean call(RealmResults<C> cs) {
                        return isDataValid(cs);
                    }
                }).map(new Func1<RealmResults<C>, List<C>>() {
                    @Override
                    public List<C> call(RealmResults<C> cs) {
                        return ImmutableList.copyOf(cs);
                    }
                }).first();
            }
        };
    }
}
