package miles.diary.data.error;

/**
 * Created by mbpeele on 3/9/16.
 */
public class NoRealmObjectKeyException extends IllegalArgumentException {

    public NoRealmObjectKeyException() {
        super("RealmObject does not have a field declared as 'KEY'");
    }
}
