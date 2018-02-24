package mformetal.diary.data.api

import io.realm.OrderedRealmCollection
import mformetal.diary.data.model.realm.Entry

/**
 * Created by mbpeele on 3/2/16.
 */
interface EntryRepository {

    fun open()

    fun getAllEntries() : OrderedRealmCollection<Entry>

    fun close()

}
