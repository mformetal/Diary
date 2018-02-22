package miles.diary.data.api

import io.realm.OrderedRealmCollection
import miles.diary.data.model.realm.Entry

/**
 * Created by mbpeele on 3/2/16.
 */
interface Repository {

    fun open()

    fun getAllEntries() : OrderedRealmCollection<Entry>

    fun close()

}
