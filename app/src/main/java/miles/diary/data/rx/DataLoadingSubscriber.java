package miles.diary.data.rx;

import miles.diary.data.DataLoadingListener;
import rx.Subscriber;

/**
 * Created by mbpeele on 5/8/16.
 */
public class DataLoadingSubscriber<T> extends Subscriber<T> {

    private DataLoadingListener<T> dataLoadingListener;

    public DataLoadingSubscriber(DataLoadingListener dataLoadingListener) {
        this.dataLoadingListener = dataLoadingListener;
    }

    @Override
    public void onCompleted() {
        dataLoadingListener.onLoadComplete();
    }

    @Override
    public void onError(Throwable e) {
        dataLoadingListener.onLoadError(e);
    }

    @Override
    public void onNext(T t) {
        dataLoadingListener.onLoadData(t);
    }

    @Override
    public void onStart() {
        dataLoadingListener.onLoadStart();
    }
}
