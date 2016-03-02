package miles.diary.data.api;

/**
 * Created by mbpeele on 3/2/16.
 */
public interface LoadingListener {

    void onLoadEmpty();

    void onLoadComplete();

    void onLoadStart();

    void onLoadError(Throwable throwable);

}
