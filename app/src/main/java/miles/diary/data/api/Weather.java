package miles.diary.data.api;

import android.content.Context;

import com.google.gson.Gson;

import miles.diary.R;
import miles.diary.data.model.weather.WeatherResponse;
import miles.diary.data.rx.OkHttpObservable;
import miles.diary.util.URLFormatter;
import okhttp3.OkHttpClient;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

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

    public Observable<WeatherResponse> getWeather(final Double latitude, final Double longitude) {
        String url = URLFormatter.weather(context, latitude, longitude);

        OkHttpObservable<WeatherResponse> okHttpObservable = OkHttpObservable.<WeatherResponse>builder()
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
