package mformetal.diary

import android.annotation.SuppressLint
import android.content.Context
import com.joanzapata.iconify.Iconify
import com.joanzapata.iconify.fonts.WeathericonsModule
import io.realm.Realm
import io.realm.RealmConfiguration
import mformetal.diary.data.api.EntryRepository
import mformetal.diary.data.api.RealmEntryRepository
import mformetal.diary.util.SharedPreferencesStorage
import mformetal.diary.util.Storage
import mformetal.kodi.android.KodiApp
import mformetal.kodi.core.Kodi
import mformetal.kodi.core.api.builder.bind
import mformetal.kodi.core.api.builder.get
import mformetal.kodi.core.provider.component
import mformetal.kodi.core.provider.singleton

/**
 * Created by mbpeele on 1/14/16.
 */
class App : KodiApp() {

    override fun createRootKodi(): Kodi {
        return Kodi.init {
            val app = this@App

            bind<Context>() using component(app)
            bind<App>() using component(app)
            bind<EntryRepository>() using singleton { RealmEntryRepository() }
            bind<Storage>() using singleton { SharedPreferencesStorage(get()) }
        }
    }

    @SuppressLint("MissingSuperCall")
    override fun onCreate() {
        super.onCreate()

        Iconify.with(WeathericonsModule())

        Realm.init(this)

        val realmConfiguration = RealmConfiguration.Builder()
                .name(getString(R.string.realm_name))
                .deleteRealmIfMigrationNeeded()
                .build()
        Realm.setDefaultConfiguration(realmConfiguration)
    }
}
