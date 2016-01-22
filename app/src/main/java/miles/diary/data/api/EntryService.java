package miles.diary.data.api;

import io.realm.Realm;
import io.realm.RealmResults;
import miles.diary.data.SimpleObserver;
import miles.diary.data.SimpleSubscriber;
import miles.diary.data.model.Entry;
import miles.diary.util.Logg;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.functions.Action1;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by mbpeele on 1/18/16.
 */
public class EntryService {

    private Observable<Entry> entries;
    private Realm entriesRealm;
    private CompositeSubscription compositeSubscription;

    public EntryService(Realm realm) {
        compositeSubscription = new CompositeSubscription();
//        queryEntries();
        entriesRealm = realm;
    }

    public Observable<Entry> getEntries() {
        if (entries == null) {
//            entries = queryEntries();
        }

        return entries;
    }

    private void addSubscription(Subscription subscription) {
        compositeSubscription.add(subscription);
    }

    private void removeSubscription(Subscription subscription) {
        compositeSubscription.remove(subscription);
    }

    private void unsubscribe() {
        compositeSubscription.unsubscribe();
    }
}
