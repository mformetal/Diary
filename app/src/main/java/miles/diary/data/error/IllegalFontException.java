package miles.diary.data.error;

/**
 * Created by mbpeele on 5/7/16.
 */
public class IllegalFontException extends IllegalArgumentException {

    public IllegalFontException(String fontName) {
        super("Supplied font name " + fontName + " must match file name in assets/fonts/ directory");
    }
}
