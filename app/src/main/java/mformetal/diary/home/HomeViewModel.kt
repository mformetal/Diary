package mformetal.diary.home

import android.arch.lifecycle.ViewModel
import io.realm.OrderedRealmCollection
import io.realm.Realm
import io.realm.Sort
import mformetal.diary.data.model.realm.Entry

/**
 * @author - mbpeele on 2/25/18.
 */
class HomeViewModel : ViewModel() {

    private val realm = Realm.getDefaultInstance()

    val entries : OrderedRealmCollection<Entry> = realm.where(Entry::class.java)
            .sort("createdAtSeconds", Sort.DESCENDING)
            .findAllAsync()

    override fun onCleared() {
        super.onCleared()

        realm.close()
    }
}