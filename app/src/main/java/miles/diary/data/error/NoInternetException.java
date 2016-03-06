package miles.diary.data.error;

/**
 * Created by mbpeele on 3/3/16.
 */
public class NoInternetException extends Exception {

    public NoInternetException() {
        super("No connection could be established.");
    }
}
