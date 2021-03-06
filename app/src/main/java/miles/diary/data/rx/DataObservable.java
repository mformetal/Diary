package miles.diary.data.rx;

import io.realm.Realm;
import io.realm.RealmObject;
import rx.Observable;
import rx.Single;
import rx.SingleSubscriber;

/**
 * Created by mbpeele on 3/3/16.
 */
public class DataObservable {

    public static <T extends RealmObject> Single<Void> delete(final T object, final Realm realm) {
        return Single.create(new RealmSingleSubscription(realm) {
            @Override
            public Void commit() {
                object.deleteFromRealm();
                return null;
            }
        });
    }

    public static <T extends RealmObject> Observable<T> upload(final T object, final Realm realm) {
        return Observable.create(new RealmObservableSubscription<T>(realm) {
            @Override
            public T commit() {
                return realm.copyToRealm(object);
            }
        });
    }

    public static <T extends RealmObject> Observable<T> upload(final DataTransaction<T> dataTransaction,
                                                               final Realm realm) {
        return Observable.create(new RealmObservableSubscription<T>(realm) {
            @Override
            public T commit() {
                return realm.copyToRealm(dataTransaction.call());
            }
        });
    }

    public static <T extends RealmObject> Observable<T> update(final DataTransaction<T> dataTransaction,
                                                               final Realm realm) {
        return Observable.create(new RealmObservableSubscription<T>(realm) {
            @Override
            public T commit() {
                return realm.copyToRealmOrUpdate(dataTransaction.call());
            }
        });
    }
}
