package miles.diary.data.adapter;

/**
 * Created by mbpeele on 2/7/16.
 */
public interface BackendAdapterListener {

    void onCompleted();

    void onError(Throwable throwable);

    void onEmpty();
}
