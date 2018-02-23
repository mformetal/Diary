package miles.diary.newentry

import android.annotation.SuppressLint
import com.google.android.gms.location.places.Place
import com.google.android.gms.location.places.PlaceDetectionClient
import io.reactivex.Single
import io.reactivex.disposables.Disposables
import java.util.*

/**
 * @author - mbpeele on 2/22/18.
 */
interface PlaceDetectionContract {

    fun getCurrentPlace() : Single<Place>

}

class PlaceDetectionApi(private val placeDetectionClient: PlaceDetectionClient) : PlaceDetectionContract {

    @SuppressLint("MissingPermission")
    override fun getCurrentPlace(): Single<Place> {
        @Suppress("RedundantSamConstructor")
        return Single.create { emitter ->
            placeDetectionClient.getCurrentPlace(null).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val place = task.result.maxBy { it.likelihood }?.place
                    if (place == null) {
                        emitter.onError(NoSuchElementException())
                    } else {
                        // Emit copy of Place here
                    }
                } else {
                    emitter.onError(NoSuchElementException())
                }

                emitter.setDisposable(Disposables.fromRunnable {
                    task.result.release()
                })
            }
        }
    }
}