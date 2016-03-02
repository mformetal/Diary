package miles.diary.data.rx;

import java.lang.ref.SoftReference;

import miles.diary.data.api.LoadingListener;
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

        isListening = shouldListen;

        activity.addSubscription(this);

        softReference = new SoftReference<>(activity);
    }

    @Override
    public void onCompleted() {
        removeSelf();

        BaseActivity activity = softReference.get();
        if (isListening && activity != null) {
            ((LoadingListener) activity).onLoadComplete();
        }
    }

    @Override
    public void onError(Throwable e) {
        removeSelf();

        BaseActivity activity = softReference.get();
        if (isListening && activity != null) {
            ((LoadingListener) activity).onLoadError(e);
        } else if (activity != null) {
            e.printStackTrace();
            Logg.log("ERROR FROM:", activity.getClass().getName());
        }
    }

    @Override
    public void onStart() {
        BaseActivity activity = softReference.get();
        if (isListening && activity != null) {
            ((LoadingListener) activity).onLoadStart();
        }
    }

    private void removeSelf() {
        BaseActivity activity = softReference.get();
        if (activity != null) {
            activity.removeSubscription(this);
        }
    }
}
