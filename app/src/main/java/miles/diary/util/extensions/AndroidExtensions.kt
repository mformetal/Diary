package miles.diary.util.extensions

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.support.annotation.IdRes
import android.support.design.widget.Snackbar
import android.support.v13.app.ActivityCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import miles.diary.R

/**
 * @author - mbpeele on 12/14/17.
 */
fun <T : View> Activity.findView(@IdRes id: Int) : Lazy<T> {
    return lazy(LazyThreadSafetyMode.NONE) {
        findViewById<T>(id)
    }
}

val Activity.root : ViewGroup
    get() = (findViewById<View>(android.R.id.content) as ViewGroup).getChildAt(0) as ViewGroup

fun <T : View> RecyclerView.ViewHolder.findView(@IdRes id: Int) : Lazy<T> {
    return lazy(LazyThreadSafetyMode.NONE) {
        itemView.findViewById<T>(id)
    }
}

fun Context.hasConnection(): Boolean {
    val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    return connectivityManager.activeNetworkInfo != null && connectivityManager.activeNetworkInfo.isConnectedOrConnecting
}

fun AppCompatActivity.hasPermissions(vararg permissions: String): Boolean {
    return permissions.none { ActivityCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED }
}

fun Activity.permissionsGranted(results: IntArray?): Boolean {
    if (results == null) {
        return false
    }

    return results.none { it != PackageManager.PERMISSION_GRANTED }
}

fun AppCompatActivity.noInternet() {
    Snackbar.make(root, getString(R.string.error_no_internet), Snackbar.LENGTH_SHORT).show()
}