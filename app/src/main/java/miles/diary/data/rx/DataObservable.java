package miles.diary.data.rx;

import io.realm.Realm;
import io.realm.RealmObject;
import miles.diary.util.Logg;
import rx.Observable;
import rx.Single;
import rx.SingleSubscriber;

/**
 * Created by mbpeele on 3/3/16.
 */
public class DataObservable {

    public static <T extends RealmObject> Single<T> delete(final T object, final Realm realm) {
        return Single.create(new Single.OnSubscribe<T>() {
            @Override
            public void call(SingleSubscriber<? super T> singleSubscriber) {
                object.deleteFromRealm();
            }
        });
    }

    public static <T extends RealmObject> Observable<T> upload(final T object, final Realm realm) {
        return Observable.create(new OnSubscribeRealm<T>(realm) {
            @Override
            public T commit() {
                return realm.copyToRealm(object);
            }
        });
    }

    public static <T extends RealmObject> Observable<T> upload(final DataTransaction<T> dataTransaction,
                                                               final Realm realm) {
        return Observable.create(new OnSubscribeRealm<T>(realm) {
            @Override
            public T commit() {
                return realm.copyToRealm(dataTransaction.call());
            }
        });
    }

    public static <T extends RealmObject> Observable<T> update(final DataTransaction<T> dataTransaction,
                                                               final Realm realm) {
        return Observable.create(new OnSubscribeRealm<T>(realm) {
            @Override
            public T commit() {
                return realm.copyToRealmOrUpdate(dataTransaction.call());
            }
        });
    }
}
