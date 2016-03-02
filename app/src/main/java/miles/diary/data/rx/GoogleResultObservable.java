package miles.diary.data.rx;

import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.ResultCallback;

import rx.Observable;
import rx.Subscriber;
import rx.functions.Action0;
import rx.subscriptions.Subscriptions;

/**
 * Created by mbpeele on 3/2/16.
 */
public class GoogleResultObservable<T extends Result> implements Observable.OnSubscribe<T> {

    private final PendingResult<T> result;
    private boolean complete = false;

    public GoogleResultObservable(PendingResult<T> result) {
        this.result = result;
    }

    @Override
    public void call(final Subscriber<? super T> subscriber) {
        result.setResultCallback(new ResultCallback<T>() {
            @Override
            public void onResult(T t) {
                subscriber.onNext(t);
                complete = true;
                subscriber.onCompleted();
            }
        });
        subscriber.add(Subscriptions.create(new Action0() {
            @Override
            public void call() {
                if (!complete) {
                    result.cancel();
                }
            }
        }));
    }
}
