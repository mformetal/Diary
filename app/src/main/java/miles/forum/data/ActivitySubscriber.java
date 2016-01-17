package miles.forum.data;

import java.lang.ref.SoftReference;

import miles.forum.ui.activity.BaseActivity;
import rx.Subscriber;

/**
 * Created by mbpeele on 1/16/16.
 */
public class ActivitySubscriber<T> extends Subscriber<T> {

    private final SoftReference<BaseActivity> softReference;

    public ActivitySubscriber(BaseActivity activity) {
        softReference = new SoftReference<>(activity);
        activity.addSubscription(this);
    }

    @Override
    public void onCompleted() {
        BaseActivity activity = softReference.get();
        if (activity != null) {
            activity.removeSubscription(this);
        }
    }

    @Override
    public void onError(Throwable e) {
        BaseActivity activity = softReference.get();
        if (activity != null) {
            activity.removeSubscription(this);
        }
    }

    @Override
    public void onNext(T t) {

    }
}
