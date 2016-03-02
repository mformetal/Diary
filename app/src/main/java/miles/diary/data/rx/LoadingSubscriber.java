package miles.diary.data.rx;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import miles.diary.data.api.LoadingListener;
import rx.Subscriber;

/**
 * Created by mbpeele on 3/2/16.
 */
public abstract class LoadingSubscriber<T> extends Subscriber<T> implements LoadingListener {

    private List<LoadingListener> loadingListeners;

    public LoadingSubscriber(LoadingListener listener) {
        loadingListeners = new ArrayList<>();
        loadingListeners.add(listener);
    }

    public LoadingSubscriber(LoadingListener... listeners) {
        loadingListeners = new ArrayList<>();
        Collections.addAll(loadingListeners, listeners);
    }

    @Override
    public void onCompleted() {
        onLoadComplete();
    }

    @Override
    public void onError(Throwable e) {
        onLoadError(e);
    }

    @Override
    public void onLoadEmpty() {
        // no op
    }

    @Override
    public void onStart() {
        onLoadStart();
    }

    @Override
    public void onLoadComplete() {
        for (LoadingListener listener: loadingListeners) {
            listener.onLoadComplete();
        }
    }

    @Override
    public void onLoadStart() {
        for (LoadingListener listener: loadingListeners) {
            listener.onLoadStart();
        }
    }

    @Override
    public void onLoadError(Throwable throwable) {
        for (LoadingListener listener: loadingListeners) {
            listener.onLoadError(throwable);
        }
    }
}
