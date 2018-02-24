package mformetal.diary.data.model.realm

import io.realm.RealmObject
import io.realm.annotations.Required
import java.util.*

open class Entry(
        @Required
        var createdAt: Date = Date(),
        var body: String = "",
        var uri: String = "",
        var placeName: String ?= null,
        var placeId: String = "",
        var weather: String ?= null,
        var latitude: Double = 0.0,
        var longitude: Double = 0.0) : RealmObject() {

}
