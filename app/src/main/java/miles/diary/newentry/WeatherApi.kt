package miles.diary.newentry

import android.annotation.SuppressLint
import android.location.Location
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.gson.Gson
import io.reactivex.Maybe
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import miles.diary.data.model.weather.WeatherResponse
import miles.diary.data.rx.OkHttpSingle
import okhttp3.OkHttpClient

/**
 * @author - mbpeele on 2/22/18.
 */
interface WeatherApiContract {

    fun getCurrentWeather(): Single<WeatherResponse>

}

class WeatherApi(
        private val locationProvider: FusedLocationProviderClient,
        private val okHttpClient: OkHttpClient,
        private val gson: Gson,
        private val baseUrl: String,
        private val apikey: String) : WeatherApiContract {

    @SuppressLint("MissingPermission")
    override fun getCurrentWeather(): Single<WeatherResponse> {
        return Maybe.create<Location> { emitter ->
            locationProvider.lastLocation.addOnCompleteListener {
                if (it.isSuccessful) {
                    emitter.onSuccess(it.result)
                } else {
                    emitter.onComplete()
                }
            }
        }.flatMapSingle {
            fetchWeather(it)
        }
    }

    private fun fetchWeather(location: Location): Single<WeatherResponse> {
        val url = baseUrl + "data/2.5/weather?" + "lat=" + location.latitude + "&lon=" + location.longitude + "&APPID=" + apikey

        return OkHttpSingle<WeatherResponse>()
                .execute(url, gson, WeatherResponse::class.java, okHttpClient)
                .subscribeOn(Schedulers.io())
    }
}