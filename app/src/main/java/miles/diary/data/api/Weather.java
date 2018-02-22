package miles.diary.data.api;

import android.content.Context;

import com.google.gson.Gson;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import miles.diary.data.model.weather.WeatherResponse;
import miles.diary.data.rx.OkHttpObservable;
import miles.diary.util.URLFormatter;
import okhttp3.OkHttpClient;

/**
 * Created by mbpeele on 2/4/16.
 */
public class Weather {

    private OkHttpClient client;
    private Context context;
    private Gson gson;

    public Weather(Context cxt) {
        context = cxt;
        client = new OkHttpClient();
        gson = new Gson();
    }

    public Single<WeatherResponse> getWeather(final Double latitude, final Double longitude) {
        String url = URLFormatter.weather(context, latitude, longitude);

        OkHttpObservable<WeatherResponse> okHttpObservable = OkHttpObservable.Companion.<WeatherResponse>builder()
                .target(WeatherResponse.class)
                .url(url)
                .gson(gson)
                .build();

        return okHttpObservable.execute(client)
                .cache()
                .retry(1)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
}
