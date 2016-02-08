package miles.diary.data;

import android.app.Application;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.io.IOException;
import java.io.Reader;

import miles.diary.R;
import miles.diary.data.model.WeatherResponse;
import miles.diary.util.Logg;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import rx.Observable;
import rx.Subscriber;

/**
 * Created by mbpeele on 2/4/16.
 */
public class WeatherService {

    private OkHttpClient client;
    private Gson gson;
    private String baseUrl, apiKey;
    private Observable<WeatherResponse> weatherResponseObservable;

    public WeatherService(Application application) {
        client = new OkHttpClient();
        gson = new Gson();
        apiKey = application.getResources().getString(R.string.weather_api_key);
        baseUrl = application.getResources().getString(R.string.weather_base);
    }

    public Observable<WeatherResponse> getWeather(Double latitude, Double longitude) {
        if (weatherResponseObservable == null) {
            weatherResponseObservable = Observable.create(new Observable.OnSubscribe<WeatherResponse>() {
                @Override
                public void call(Subscriber<? super WeatherResponse> subscriber) {
                    try {
                        String url = baseUrl + "/" + apiKey + "/" + latitude + "," + longitude;

                        Response response = client.newCall(new Request.Builder()
                                .url(url)
                                .build()).execute();

                        Reader reader = response.body().charStream();
                        subscriber.onNext(gson.fromJson(reader, WeatherResponse.class));
                        subscriber.onCompleted();
                        reader.close();
                    } catch (IOException e) {
                        Logg.log(e);
                        subscriber.onError(e);
                    } catch (JsonSyntaxException e1) {
                        Logg.log(e1);
                    }
                }
            });
        }

        return weatherResponseObservable;
    }
}
