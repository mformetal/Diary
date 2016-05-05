package miles.diary.data.rx;

import java.lang.ref.SoftReference;

import miles.diary.ui.activity.BaseActivity;
import rx.Subscriber;

/**
 * Created by mbpeele on 1/16/16.
 */
public abstract class ActivitySubscriber<T> extends Subscriber<T> {

    private SoftReference<BaseActivity> softReference;

    public ActivitySubscriber(BaseActivity activity) {
        if (activity == null) {
            onError(new NullPointerException("Activity subscriber is null"));
            return;
        }

        activity.addSubscription(this);

        softReference = new SoftReference<>(activity);
    }

    @Override
    public void onCompleted() {
        removeSelf();
    }

    @Override
    public void onError(Throwable e) {
        removeSelf();

        e.printStackTrace();
    }

    @Override
    public void onStart() {
    }

    private void removeSelf() {
        BaseActivity activity = softReference.get();
        if (activity != null) {
            activity.removeSubscription(this);
        }
    }

    public BaseActivity getSubscribedActivity() {
        return softReference.get();
    }
}
