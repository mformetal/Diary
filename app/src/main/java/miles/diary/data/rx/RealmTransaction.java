package miles.diary.data.rx;

import io.realm.RealmObject;
import rx.functions.Action;

/**
 * Created by mbpeele on 3/3/16.
 */
public interface RealmTransaction<T extends RealmObject> extends Action {

    T call();
}
