package mformetal.diary.newentry

import android.annotation.SuppressLint
import android.location.Location
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.gson.Gson
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import mformetal.diary.data.model.weather.WeatherResponse
import mformetal.diary.data.rx.OkHttpSingle
import okhttp3.OkHttpClient

/**
 * @author - mbpeele on 2/22/18.
 */
interface GetWeather {

    fun getCurrentWeather(): Single<WeatherResponse>

}

class GetWeatherUseCase(
        private val locationProvider: FusedLocationProviderClient,
        private val okHttpClient: OkHttpClient,
        private val gson: Gson,
        private val baseUrl: String,
        private val apikey: String) : GetWeather {

    @SuppressLint("MissingPermission")
    override fun getCurrentWeather(): Single<WeatherResponse> {
        return Single.create<Location> { emitter ->
            val task = locationProvider.lastLocation
            task.addOnSuccessListener {
                emitter.onSuccess(it)
            }

            task.addOnFailureListener {
                emitter.onError(it)
            }
        }.flatMap {
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