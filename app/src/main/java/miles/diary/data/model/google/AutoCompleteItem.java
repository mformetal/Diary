package miles.diary.data.model.google;

import com.google.android.gms.location.places.AutocompletePrediction;

/**
 * Created by mbpeele on 3/2/16.
 */
public class AutoCompleteItem {

    public final String placeId;
    public final String description;
    public final String primaryText;

    public AutoCompleteItem(AutocompletePrediction prediction) {
        placeId = prediction.getPlaceId();
        description = prediction.getDescription();
        primaryText = prediction.getPrimaryText(null).toString();
    }

    @Override
    public String toString() {
        return description.toString();
    }
}
