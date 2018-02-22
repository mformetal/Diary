package miles.diary.data.rx

import com.google.android.gms.common.api.PendingResult
import com.google.android.gms.common.api.Result
import io.reactivex.Single
import io.reactivex.disposables.Disposables

/**
 * Created by mbpeele on 3/9/16.
 */
object GoogleObservable {

    fun <T : Result> execute(result: PendingResult<T>): Single<T> {
        return Single.create { emitter ->
            val complete = booleanArrayOf(false)

            result.setResultCallback { t -> emitter.onSuccess(t) }

            emitter.setDisposable(Disposables.fromRunnable {
                if (!complete[0]) {
                    result.cancel()
                }
            })
        }
    }
}
