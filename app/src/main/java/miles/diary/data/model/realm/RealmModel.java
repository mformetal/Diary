package miles.diary.data.model.realm;

/**
 * Created by mbpeele on 3/10/16.
 */
public interface RealmModel<T> {

    String KEY = "KEY";

    Object getPrimaryKey();

    boolean isEqualTo(T object);
}
