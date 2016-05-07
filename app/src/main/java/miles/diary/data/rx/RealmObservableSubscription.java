package miles.diary.data.rx;

import io.realm.Realm;
import miles.diary.util.Logg;
import rx.Observable;
import rx.Subscriber;

/**
 * Created by mbpeele on 3/3/16.
 */
public abstract class RealmObservableSubscription<T> implements Observable.OnSubscribe<T> {

    private final Realm realm;

    public RealmObservableSubscription(Realm realm) {
        this.realm = realm;
    }

    @Override
    public void call(Subscriber<? super T> subscriber) {
        subscriber.onStart();
        realm.beginTransaction();
        try {
            subscriber.onNext(commit());
        } catch (Exception e) {
            subscriber.onError(e);
        }
        realm.commitTransaction();
        subscriber.onCompleted();
    }

    public abstract T commit();
}
