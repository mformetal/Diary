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

    private String url;
    private Class<T> clazz;
    private OkHttpClient client;
    private Gson gson;

    private OkHttpObservable(Builder builder) {
        this.url = builder.url;
        this.clazz = builder.clazz;
        this.client = builder.client;
        this.gson = builder.gson;
    }

    public Observable<T> execute() {
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

    public static class Builder<T> {

        public String url;
        public Class clazz;
        public OkHttpClient client;
        public Gson gson;

        public Builder(OkHttpClient client, Class<T> clazz) {
            this.client = client;
            this.clazz = clazz;
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
