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
class OkHttpObservable<T> private constructor(builder: Builder<T>) {

    private val url: String?
    private val clazz: Class<T>?
    private val gson: Gson

    init {
        url = builder.url
        clazz = builder.tClass
        gson = builder.gson
    }

    fun execute(client: OkHttpClient): Single<T> {
        return Single.using(Callable<Response> {
            client.newCall(Request.Builder()
                    .url(url)
                    .build()).execute()
        }, Function<Response, SingleSource<T>> {
            val reader = it.body()!!.charStream()
            val responseObject = gson.fromJson<T>(reader, clazz!!)
            Single.just(responseObject)
        }, object : Consumer<Response> {
            override fun accept(response: Response) {
                response.body()?.close()
                response.close()
            }
        }, false)
    }

    class Builder<T> {

        internal var url: String? = null
        internal var tClass: Class<T>? = null
        internal var gson = Gson()

        fun target(tClass: Class<T>): Builder<T> {
            this.tClass = tClass
            return this
        }

        fun url(url: String): Builder<T> {
            this.url = url
            return this
        }

        fun gson(gson: Gson): Builder<T> {
            this.gson = gson
            return this
        }

        fun build(): OkHttpObservable<T> {
            return OkHttpObservable(this)
        }
    }

    companion object {

        fun <L> builder(): Builder<L> {
            return Builder()
        }
    }
}
