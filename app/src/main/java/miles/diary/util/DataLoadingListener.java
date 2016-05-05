package miles.diary.util;

/**
 * Created by mbpeele on 3/2/16.
 */
public interface DataLoadingListener {

    void onLoadEmpty();

    void onLoadComplete();

    void onLoadStart();

    void onLoadError(Throwable throwable);

}
