package mformetal.diary.util.extensions

import android.view.View
import android.widget.EditText

/**
 * @author - mbpeele on 2/24/18.
 */
val EditText.textAsString
    get() = text.toString()

fun View.gone() {
    visibility = View.GONE
}

fun View.visible() {
    visibility = View.VISIBLE
}