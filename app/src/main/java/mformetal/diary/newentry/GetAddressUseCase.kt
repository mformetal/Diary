package mformetal.diary.newentry

import android.annotation.SuppressLint
import android.location.Address
import android.location.Geocoder
import android.location.Location
import com.google.android.gms.location.FusedLocationProviderClient
import io.reactivex.Single

/**
 * @author - mbpeele on 2/22/18.
 */
interface GetAddress {

    fun getCurrentAddress(): Single<Address>

}

class GetAddressUseCase(private val locationProvider: FusedLocationProviderClient,
                        private val geocoder: Geocoder) : GetAddress {

    @SuppressLint("MissingPermission")
    override fun getCurrentAddress(): Single<Address> {
        @Suppress("RedundantSamConstructor")
        return Single.create<Location> { emitter ->
            val task = locationProvider.lastLocation
            task.addOnSuccessListener {
                emitter.onSuccess(it)
            }

            task.addOnFailureListener {
                emitter.onError(it)
            }
        }.map {
            geocoder.getFromLocation(it.latitude, it.longitude, 1).first()
        }
    }
}