package miles.diary.data.rx;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import miles.diary.data.api.db.DataLoadingListener;
import rx.Subscriber;

/**
 * Created by mbpeele on 3/2/16.
 */
public abstract class LoadingSubscriber<T> extends Subscriber<T> implements DataLoadingListener {

    private List<DataLoadingListener> loadingListeners;

    public LoadingSubscriber(DataLoadingListener listener) {
        loadingListeners = new ArrayList<>();
        loadingListeners.add(listener);
    }

    public LoadingSubscriber(DataLoadingListener... listeners) {
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
        for (DataLoadingListener listener: loadingListeners) {
            listener.onLoadComplete();
        }
    }

    @Override
    public void onLoadStart() {
        for (DataLoadingListener listener: loadingListeners) {
            listener.onLoadStart();
        }
    }

    @Override
    public void onLoadError(Throwable throwable) {
        for (DataLoadingListener listener: loadingListeners) {
            listener.onLoadError(throwable);
        }
    }
}
