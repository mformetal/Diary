package miles.diary.data.api.google;

import android.location.Location;

/**
 * Created by mbpeele on 3/8/16.
 */
public class GoogleUrlFormatter {

    public static String searchNearby(String base, Location location, float radius, String key) {
        return base + "maps/api/place/nearbysearch/json?" + "location=" +
                location.getLatitude() + ',' + location.getLongitude() + "&radius=" + radius + "&key=" +
                key;
    }
}
