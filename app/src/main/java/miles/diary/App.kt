package miles.diary

import android.app.Application
import android.content.Context
import com.joanzapata.iconify.Iconify
import com.joanzapata.iconify.fonts.WeathericonsModule
import io.realm.Realm
import io.realm.RealmConfiguration
import miles.diary.data.api.Google
import miles.diary.data.api.Repository
import miles.diary.data.api.RepositoryImpl
import miles.diary.data.api.Weather
import miles.kodi.Kodi
import miles.kodi.api.builder.bind
import miles.kodi.api.builder.get
import miles.kodi.provider.component
import miles.kodi.provider.singleton

/**
 * Created by mbpeele on 1/14/16.
 */
class App : Application() {

    lateinit var kodi: Kodi

    override fun onCreate() {
        super.onCreate()
        Iconify.with(WeathericonsModule())

        val realmConfiguration = RealmConfiguration.Builder()
                .name(getString(R.string.realm_name))
                .deleteRealmIfMigrationNeeded()
                .build()
        Realm.setDefaultConfiguration(realmConfiguration)

        kodi = Kodi.init {
            val app = this@App

            bind<Context>() using component(app)
            bind<App>() using component(app)
            bind<Repository>() using singleton { RepositoryImpl() }
            bind<Weather>() using singleton { Weather(get()) }
            bind<Google>() using singleton { Google(get()) }
        }
    }
}
