package miles.diary.ui.activity

import android.content.Context
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AppCompatActivity
import android.util.Pair
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Toolbar
import butterknife.ButterKnife
import butterknife.Unbinder
import miles.diary.App
import miles.diary.R
import miles.diary.data.api.Google
import miles.diary.data.api.Repository
import miles.diary.data.api.Weather
import miles.diary.util.Storage
import miles.kodi.api.ScopeRegistry
import miles.kodi.api.injection.KodiInjector
import miles.kodi.api.injection.register
import miles.kodi.api.scoped
import rx.Subscription
import rx.subscriptions.CompositeSubscription

/**
 * Created by mbpeele on 1/14/16.
 */
abstract class BaseActivity : AppCompatActivity() {

    private val injector = KodiInjector()
    lateinit var scopeRegistry : ScopeRegistry

    internal val google: Google by injector.register()
    internal val repository: Repository by injector.register()
    internal val weather: Weather by injector.register()
    internal val storage: Storage by injector.register()

    protected lateinit var root: ViewGroup
    private lateinit var unbinder: Unbinder

    private val compositeSubscription = CompositeSubscription()

    val app : App
        get() = application as App

    val navigationBarSharedElement: Pair<View, String>
        get() = Pair(navigationBarView, Window.NAVIGATION_BAR_BACKGROUND_TRANSITION_NAME)

    val statusBarSharedElement: Pair<View, String>
        get() = Pair(statusBarView, Window.STATUS_BAR_BACKGROUND_TRANSITION_NAME)

    val navigationBarView: View
        get() {
            val decor = window.decorView
            return decor.findViewById(android.R.id.navigationBarBackground)
        }

    val statusBarView: View
        get() {
            val decor = window.decorView
            return decor.findViewById(android.R.id.statusBarBackground)
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        injector.inject(app.kodi, scoped<BaseActivity>())

        repository.open()
    }

    override fun setContentView(layoutResID: Int) {
        super.setContentView(layoutResID)

        unbinder = ButterKnife.bind(this)

        root = (findViewById<View>(android.R.id.content) as ViewGroup).getChildAt(0) as ViewGroup
    }

    override fun onDestroy() {
        super.onDestroy()

        scopeRegistry.unregister()

        repository.close()

        compositeSubscription.unsubscribe()

        unbinder.unbind()
    }

    fun addSubscription(subscription: Subscription) {
        compositeSubscription.add(subscription)
    }

    fun removeSubscription(subscription: Subscription) {
        compositeSubscription.remove(subscription)
    }

    fun hasConnection(): Boolean {
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        return connectivityManager.activeNetworkInfo != null && connectivityManager.activeNetworkInfo.isConnectedOrConnecting
    }

    fun noInternet() {
        Snackbar.make(root, getString(R.string.error_no_internet), Snackbar.LENGTH_SHORT).show()
    }

    fun hasPermissions(vararg permissions: String): Boolean {
        return permissions.none { ActivityCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED }
    }

    fun permissionsGranted(results: IntArray?): Boolean {
        if (results == null) {
            return false
        }

        return results.none { it != PackageManager.PERMISSION_GRANTED }
    }

    fun getMenuItem(toolbar: Toolbar, id: Int): MenuItem? {
        return try {
            toolbar.menu.getItem(id)
        } catch (e: IndexOutOfBoundsException) {
            null
        }

    }

    companion object {

        protected val CONFIRMATION_DIALOG = "confirmation"
    }
}
