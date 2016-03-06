package miles.diary.data.model.google;

import com.google.android.gms.location.places.Place;
import com.google.android.gms.maps.model.LatLng;

/**
 * Created by mbpeele on 3/5/16.
 */
public class PlaceInfo {

    private final CharSequence address;
    private final CharSequence name;
    private final String id;
    private final LatLng latLng;

    public PlaceInfo(Place place) {
        address = place.getAddress();
        name = place.getName();
        id = place.getId();
        latLng = place.getLatLng();
    }

    public String getAddress() {
        return address.toString();
    }

    public String getName() {
        return name.toString();
    }

    public String getId() {
        return id;
    }

    public LatLng getLatLng() {
        return latLng;
    }
}
