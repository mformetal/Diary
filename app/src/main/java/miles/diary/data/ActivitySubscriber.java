package miles.diary.data;

import android.support.annotation.Nullable;

import java.lang.ref.SoftReference;

import miles.diary.ui.activity.BaseActivity;
import miles.diary.util.Logg;
import rx.Subscriber;

/**
 * Created by mbpeele on 1/16/16.
 */
public class ActivitySubscriber<T> extends Subscriber<T> {

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
    }

    @Override
    public void onError(Throwable e) {
        BaseActivity activity = softReference.get();
        if (activity != null) {
            e.printStackTrace();
            Logg.log("ERROR FROM:", activity.getClass().getName());
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

    @Nullable
    public BaseActivity getSubscribedActivity() {
        return softReference.get();
    }
}
