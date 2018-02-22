package miles.diary.ui

import android.view.View
import android.view.ViewTreeObserver

import java.lang.ref.SoftReference

/**
 * Created by mbpeele on 2/2/16.
 */
fun <T : View> T.addPreDrawer(receiver: (T) -> (Unit)) {
    val softReference = SoftReference(this)

    val viewTreeObserver = viewTreeObserver
    viewTreeObserver.addOnPreDrawListener(object : ViewTreeObserver.OnPreDrawListener {
        override fun onPreDraw(): Boolean {
            if (viewTreeObserver.isAlive) {
                viewTreeObserver.removeOnPreDrawListener(this)
            } else {
                this@addPreDrawer.viewTreeObserver.removeOnPreDrawListener(this)
            }

            val reference = softReference.get()
            return if (reference == null) {
                false
            } else {
                receiver.invoke(reference)
                true
            }
        }
    })
}
