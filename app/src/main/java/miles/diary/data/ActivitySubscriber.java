package miles.diary.data;

import android.app.Activity;

import java.lang.ref.SoftReference;

import miles.diary.ui.activity.BaseActivity;
import miles.diary.util.Logg;
import rx.Subscriber;

/**
 * Created by mbpeele on 1/16/16.
 */
public class ActivitySubscriber<T> extends Subscriber<T> {

    private final SoftReference<BaseActivity> softReference;

    public ActivitySubscriber(BaseActivity activity) {
        if (activity == null) {
            onError(new NullPointerException("Activity subscriber is null"));
        }

        softReference = new SoftReference<BaseActivity>(activity);
    }

    @Override
    public void onCompleted() {
    }

    @Override
    public void onError(Throwable e) {
        BaseActivity activity = softReference.get();
        if (activity != null) {
            Logg.log(activity.getClass().getName(), e);
        }
        removeSelf();
    }

    @Override
    public void onNext(T t) {

    }

    private void removeSelf() {
        BaseActivity activity = softReference.get();
        if (activity != null) {
            activity.removeSubscription(this);
        }
    }
}
