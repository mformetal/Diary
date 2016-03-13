package miles.diary.data.rx;

import io.realm.Realm;
import rx.Observable;
import rx.Subscriber;

/**
 * Created by mbpeele on 3/3/16.
 */
public abstract class OnSubscribeTransaction<T> implements Observable.OnSubscribe<T> {

    private final Realm realm;

    public OnSubscribeTransaction(Realm realm) {
        this.realm = realm;
    }

    @Override
    public void call(Subscriber<? super T> subscriber) {
        subscriber.onStart();
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                T result = commit();
                if (result != null) {
                    subscriber.onNext(result);
                }
            }
        }, new Realm.Transaction.OnSuccess() {
            @Override
            public void onSuccess() {
                subscriber.onCompleted();
            }
        }, new Realm.Transaction.OnError() {
            @Override
            public void onError(Throwable error) {
                subscriber.onError(error);
            }
        });
    }

    public abstract T commit();
}
