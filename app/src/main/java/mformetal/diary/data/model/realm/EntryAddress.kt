package mformetal.diary.data.model.realm

import android.location.Address
import io.realm.RealmObject

/**
 * @author - mbpeele on 2/25/18.
 */
open class EntryAddress(
        var streetNumber: String ?= null,
        var streetName: String ?= null,
        var sublocality: String ?= null,
        var locality: String ?= null,
        var postalCode: String ?= null,
        var countryCode: String ?= null,
        var countryName: String ?= null,
        var latitude: Double = 0.0,
        var longitude: Double = 0.0) : RealmObject() {

    val shortStateAddress: String
        get() = "$sublocality $locality"
    val longStateAddress: String
        get() = "$sublocality, $locality $postalCode"
    val streetAddress: String
        get() = "$streetNumber $streetName"
    val fullAddress: String
        get() = "$streetAddress $longStateAddress"

    companion object {
        fun fromAddress(address: Address) : EntryAddress {
            return EntryAddress(
                    streetNumber = address.featureName,
                    streetName = address.thoroughfare,
                    sublocality = address.subLocality,
                    locality = address.adminArea,
                    postalCode = address.postalCode,
                    countryCode = address.countryCode,
                    countryName = address.countryName,
                    latitude = address.latitude,
                    longitude = address.longitude)
        }
    }
}