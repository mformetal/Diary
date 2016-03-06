package miles.diary.data.model.google;

/**
 * Created by mbpeele on 3/2/16.
 */
public class AutoCompleteItem {

    public final CharSequence placeId;
    public final CharSequence description;

    public AutoCompleteItem(CharSequence placeId, CharSequence description) {
        this.placeId = placeId;
        this.description = description;
    }

    @Override
    public String toString() {
        return description.toString();
    }
}
