package mformetal.diary.data.model.realm

import io.realm.RealmObject

open class Entry(
        var createdAtSeconds: Long = 0L,
        var body: String = "",
        var uri: String ?= null,
        var weather: String ?= null,
        var address: EntryAddress ?= null) : RealmObject()