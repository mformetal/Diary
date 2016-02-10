package miles.diary.data.adapter;

import io.realm.RealmObject;

/**
 * Created by mbpeele on 2/7/16.
 */
public interface BackendAdapterListener<T extends RealmObject> {

    void onLoadCompleted();

    void onLoadError(Throwable throwable);

    void onLoadEmpty();

    void onLoadStart();
}
