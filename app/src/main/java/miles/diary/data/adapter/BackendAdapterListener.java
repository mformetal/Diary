package miles.diary.data.adapter;

import io.realm.RealmObject;

/**
 * Created by mbpeele on 2/7/16.
 */
public interface BackendAdapterListener<T extends RealmObject> {

    void onCompleted();

    void onError(Throwable throwable);

    void onEmpty();
}
