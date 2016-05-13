package miles.diary.data.error;

/**
 * Created by mbpeele on 5/9/16.
 */
public class RealmClosedException extends IllegalStateException {

    public RealmClosedException() {
        super("Attempt to call a Realm method while the instance is closed");
    }
}
