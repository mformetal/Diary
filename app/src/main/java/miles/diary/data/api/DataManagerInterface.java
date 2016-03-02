package miles.diary.data.api;

import android.content.Intent;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.RealmResults;
import miles.diary.data.model.Entry;
import rx.Observable;

/**
 * Created by mbpeele on 3/2/16.
 */
public interface DataManagerInterface {

    void init();

    void close();

    <T extends RealmObject> Observable<List<T>> loadObjects(Class<T> tClass);

    <T extends RealmObject> T getObject(Class<T> tClass, String key);

    <T extends RealmObject> T uploadObject(RealmObject realmObject);

    boolean isDataValid(RealmResults realmResults);

    boolean isDataValid(RealmObject realmObject);

    boolean hasConnection();
}
