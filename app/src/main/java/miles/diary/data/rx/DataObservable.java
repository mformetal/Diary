package miles.diary.data.rx;

import io.realm.Realm;
import io.realm.RealmObject;
import rx.Observable;

/**
 * Created by mbpeele on 3/3/16.
 */
public class DataObservable {

    public static <T extends RealmObject> Observable<T> delete(final T object,
                                                               final Realm realm) {
        return Observable.create(new OnSubscribeDataTransaction<T>(realm) {
            @Override
            public T execute() {
                object.removeFromRealm();
                return null;
            }
        });
    }

    public static <T extends RealmObject> Observable<T> upload(final T object, final Realm realm) {
        return Observable.create(new OnSubscribeDataTransaction<T>(realm) {
            @Override
            public T execute() {
                return realm.copyToRealm(object);
            }
        });
    }

    public static <T extends RealmObject> Observable<T> update(final DataTransaction<T> dataTransaction,
                                                               final Realm realm) {
        return Observable.create(new OnSubscribeDataTransaction<T>(realm) {
            @Override
            public T execute() {
                return realm.copyToRealmOrUpdate(dataTransaction.call());
            }
        });
    }
}
