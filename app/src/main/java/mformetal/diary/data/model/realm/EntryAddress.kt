package mformetal.diary.data.model.realm

import android.location.Address
import io.realm.RealmObject

/**
 * @author - mbpeele on 2/25/18.
 */
open class EntryAddress(
        var streetAddress: String ?= null,
        var stateAddress: String ?= null,
        var postalCode: String ?= null,
        var countryCode: String ?= null,
        var countryName: String ?= null,
        var latitude: Double = 0.0,
        var longitude: Double = 0.0) : RealmObject() {

    companion object {
        fun fromAddress(address: Address) : EntryAddress {
            return EntryAddress(
                    streetAddress = address.getAddressLine(0),
                    stateAddress = address.getAddressLine(1).let {
                        val split = it.split(" ")
                        split[0] + " " + split[1]
                    },
                    postalCode = address.postalCode,
                    countryCode = address.countryCode,
                    countryName = address.countryName,
                    latitude = address.latitude,
                    longitude = address.longitude)
        }
    }
}