package miles.diary.util

import android.content.Context
import android.location.Location

import miles.diary.R

/**
 * Created by mbpeele on 5/5/16.
 */
object URLFormatter {

    fun nearbySearch(context: Context, location: Location, radius: Float): String {
        return context.getString(R.string.maps_url) + "maps/api/place/nearbysearch/json?" + "location=" +
                location.latitude + ',' + location.longitude + "&radius=" + radius + "&key=" +
                context.getString(R.string.google_web_api_key)
    }

    fun weather(baseUrl: String, apiKey: String, latitude: Double, longitude: Double): String {
        return baseUrl + "data/2.5/weatherView?" + "lat=" + latitude + "&lon=" + longitude + "&APPID=" + apiKey
    }
}
