package miles.diary.data.rx;

import io.realm.Realm;
import rx.Single;
import rx.SingleSubscriber;

/**
 * Created by mbpeele on 5/6/16.
 */
public abstract class RealmSingleSubscription implements Single.OnSubscribe<Void> {

    private final Realm realm;

    public RealmSingleSubscription(Realm realm) {
        this.realm = realm;
    }

    @Override
    public void call(SingleSubscriber<? super Void> singleSubscriber) {
        realm.beginTransaction();
        try {
            singleSubscriber.onSuccess(commit());
        } catch (Exception e) {
            singleSubscriber.onError(e);
        }
        realm.commitTransaction();
    }

    public abstract Void commit();
}
