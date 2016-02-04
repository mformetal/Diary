package miles.diary.data;

import java.lang.ref.SoftReference;

import miles.diary.ui.activity.BaseActivity;
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
        removeSelf();
    }

    @Override
    public void onError(Throwable e) {
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
