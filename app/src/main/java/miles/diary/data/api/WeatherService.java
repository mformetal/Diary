package miles.diary.data.api;

import android.app.Application;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.io.IOException;
import java.io.Reader;

import miles.diary.R;
import miles.diary.data.model.weather.Weather;
import miles.diary.data.model.weather.WeatherResponse;
import miles.diary.data.rx.OkHttpObservable;
import miles.diary.util.Logg;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by mbpeele on 2/4/16.
 */
public class WeatherService {

    private OkHttpClient client;
    private String baseUrl, apiKey;

    public WeatherService(Application application) {
        client = new OkHttpClient();
        apiKey = application.getResources().getString(R.string.weather_api_key);
        baseUrl = application.getResources().getString(R.string.weather_base);
    }

    public Observable<WeatherResponse> getWeather(final Double latitude, final Double longitude) {
        OkHttpObservable<WeatherResponse> okHttpObservable =
                new OkHttpObservable.Builder<>(client, WeatherResponse.class)
                .url(formatUrl(latitude, longitude))
                .gson(new Gson())
                .build();

        return okHttpObservable.execute()
                .cache()
                .retry(1)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    private String formatUrl(final double latitude, final double longitude) {
        return baseUrl + "data/2.5/weather?" +
                "lat=" + latitude + "&lon=" + longitude + "&APPID=" + apiKey;
    }
}
