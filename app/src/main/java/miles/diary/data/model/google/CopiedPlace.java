package miles.diary.data.model.google;

import com.google.android.gms.location.places.Place;

/**
 * Created by mbpeele on 5/7/16.
 */
public class CopiedPlace {

    private final String name;
    private final String id;

    public CopiedPlace(Place place) {
        name = place.getName().toString();
        id = place.getId();
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }
}
