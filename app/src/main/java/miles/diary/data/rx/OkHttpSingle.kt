package miles.diary.data.rx

import com.google.gson.Gson
import io.reactivex.Single
import io.reactivex.SingleSource
import io.reactivex.functions.Consumer
import io.reactivex.functions.Function
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.util.concurrent.Callable

/**
 * Created by mbpeele on 3/8/16.
 */
class OkHttpSingle<T> {

    @Suppress("RedundantSamConstructor")
    fun execute(url: String,
                gson: Gson,
                clazz: Class<T>,
                client: OkHttpClient): Single<T> {
        return Single.using(Callable<Response> {
            client.newCall(Request.Builder()
                    .url(url)
                    .build()).execute()
        }, Function<Response, SingleSource<T>> { response ->
            val reader = response.body()!!.charStream()
            val responseObject = gson.fromJson<T>(reader, clazz)
            Single.just(responseObject)
        }, Consumer<Response> { response ->
            response.body()?.close()
            response.close()
        }, false)
    }
}
