package miles.diary.util;

import android.content.Context;
import android.location.Location;

import miles.diary.R;

/**
 * Created by mbpeele on 5/5/16.
 */
public class URLFormatter {

    public static String nearbySearch(Context context, Location location, float radius) {
        return context.getString(R.string.maps_url) + "maps/api/place/nearbysearch/json?" + "location=" +
                location.getLatitude() + ',' + location.getLongitude() + "&radius=" + radius + "&key=" +
                context.getString(R.string.google_web_api_key);
    }

    public static String weather(Context context, double latitude, double longitude) {
        return context.getString(R.string.weather_base) + "data/2.5/weatherView?" +
                "lat=" + latitude + "&lon=" + longitude + "&APPID=" + context.getString(R.string.weather_api_key);
    }
}
