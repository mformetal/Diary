package miles.diary.data.rx;

import java.lang.ref.SoftReference;

import miles.diary.data.api.db.DataLoadingListener;
import miles.diary.ui.activity.BaseActivity;
import miles.diary.util.Logg;
import rx.Subscriber;

/**
 * Created by mbpeele on 1/16/16.
 */
public abstract class ActivitySubscriber<T> extends Subscriber<T> {

    private SoftReference<BaseActivity> softReference;
    private boolean isListening;

    public ActivitySubscriber(BaseActivity activity) {
        if (activity == null) {
            onError(new NullPointerException("Activity subscriber is null"));
            return;
        }

        activity.addSubscription(this);

        softReference = new SoftReference<>(activity);
    }

    public ActivitySubscriber(BaseActivity activity, boolean shouldListen) {
        if (activity == null) {
            onError(new NullPointerException("Activity subscriber is null"));
            return;
        }

        activity.addSubscription(this);

        isListening = shouldListen;

        softReference = new SoftReference<>(activity);
    }

    @Override
    public void onCompleted() {
        removeSelf();

        BaseActivity activity = softReference.get();
        if (isListening && activity != null) {
            ((DataLoadingListener) activity).onLoadComplete();
        }
    }

    @Override
    public void onError(Throwable e) {
        removeSelf();

        e.printStackTrace();

        BaseActivity activity = softReference.get();
        if (isListening && activity != null) {
            ((DataLoadingListener) activity).onLoadError(e);
        }
    }

    @Override
    public void onStart() {
        BaseActivity activity = softReference.get();
        if (isListening && activity != null) {
            ((DataLoadingListener) activity).onLoadStart();
        }
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
