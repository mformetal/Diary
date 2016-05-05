package miles.diary.data.rx;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.io.IOException;
import java.io.Reader;

import miles.diary.util.Logg;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import rx.Observable;
import rx.Subscriber;

/**
 * Created by mbpeele on 3/8/16.
 */
public class OkHttpObservable<T> {

    private final String url;
    private final Class<T> clazz;
    private final Gson gson;

    public OkHttpObservable(Builder<T> builder) {
        url = builder.url;
        clazz = builder.tClass;
        gson = builder.gson;
    }

    public Observable<T> execute(final OkHttpClient client) {
        return Observable.create(new Observable.OnSubscribe<T>() {
            @Override
            public void call(Subscriber<? super T> subscriber) {
                subscriber.onStart();
                try {
                    Response response = client.newCall(new Request.Builder()
                            .url(url)
                            .build()).execute();

                    Reader reader = response.body().charStream();
                    subscriber.onNext(gson.fromJson(reader, clazz));
                    subscriber.onCompleted();

                    reader.close();
                } catch (IOException e) {
                    Logg.log(e);
                    subscriber.onError(e);
                } catch (JsonSyntaxException e1) {
                    subscriber.onError(e1);
                    Logg.log(e1);
                }
            }});
    }

    public static <L> Builder<L> builder(Class<L> lClass) {
        return new Builder<>(lClass);
    }

    public static class Builder<T> {

        private String url;
        private Class<T> tClass;
        private Gson gson;

        public Builder(Class<T> tClass) {
            this.tClass = tClass;
        }

        public Builder<T> url(String url) {
            this.url = url;
            return this;
        }

        public Builder<T> gson(Gson gson) {
            this.gson = gson;
            return this;
        }

        public OkHttpObservable<T> build() {
            return new OkHttpObservable<>(this);
        }
    }
}
