package mformetal.diary.util

import android.arch.lifecycle.Observer

/**
 * @author - mbpeele on 2/22/18.
 */
class SafeObserver<T>(private val doOnChange: (T) -> Unit) : Observer<T> {

    override fun onChanged(t: T?) {
        t?.let { doOnChange.invoke(it) }
    }
}

fun <T> safeObserver(doOnChange: (T) -> Unit) : Observer<T> = SafeObserver(doOnChange)