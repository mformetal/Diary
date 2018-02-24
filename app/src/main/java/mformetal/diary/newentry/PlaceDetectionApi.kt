package mformetal.diary.newentry

import android.annotation.SuppressLint
import com.google.android.gms.location.places.Place
import com.google.android.gms.location.places.PlaceDetectionClient
import io.reactivex.Single
import io.reactivex.disposables.Disposables

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
                task.addOnSuccessListener {
                    val place = task.result.maxBy { it.likelihood }!!.place
                    emitter.onSuccess(place)
                }

                task.addOnFailureListener {
                    emitter.onError(it)
                }

                emitter.setDisposable(Disposables.fromRunnable {
                    if (task.isSuccessful) {
                        task.result.release()
                    }
                })
            }
        }
    }
}