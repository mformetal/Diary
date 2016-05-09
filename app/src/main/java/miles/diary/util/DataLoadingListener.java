package miles.diary.util;

/**
 * Created by mbpeele on 3/2/16.
 */
public interface DataLoadingListener<T> {

    void onLoadEmpty();

    void onLoadData(T data);

    void onLoadComplete();

    void onLoadStart();

    void onLoadError(Throwable throwable);

}
