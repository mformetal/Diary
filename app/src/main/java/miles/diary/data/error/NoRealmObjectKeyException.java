package miles.diary.data.error;

/**
 * Created by mbpeele on 3/9/16.
 */
public class NoRealmObjectKeyException extends IllegalArgumentException {

    public NoRealmObjectKeyException() {
        super("RealmObject does not have a public final static variable named 'KEY'");
    }
}
