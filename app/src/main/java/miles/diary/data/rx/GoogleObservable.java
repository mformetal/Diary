package miles.diary.data.rx;

import android.support.annotation.NonNull;

import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.ResultCallback;

import rx.Observable;
import rx.Subscriber;
import rx.functions.Action0;
import rx.subscriptions.Subscriptions;

/**
 * Created by mbpeele on 3/9/16.
 */
public class GoogleObservable {

    public static <T extends Result> Observable<T> execute(PendingResult<T> result) {
        return Observable.create(new Observable.OnSubscribe<T>() {
            @Override
            public void call(Subscriber<? super T> subscriber) {
                final boolean[] complete = {false};

                subscriber.onStart();
                result.setResultCallback(new ResultCallback<T>() {
                    @Override
                    public void onResult(@NonNull T t) {
                        subscriber.onNext(t);
                        complete[0] = true;
                        subscriber.onCompleted();
                    }
                });

                subscriber.add(Subscriptions.create(new Action0() {
                    @Override
                    public void call() {
                        if (!complete[0]) {
                            result.cancel();
                        }
                    }
                }));
            }
        });
    }
}
