package miles.diary.ui.activity

import android.content.Intent
import android.os.Bundle
import android.view.ViewGroup

import miles.diary.ui.PreDrawer

/**
 * Created by mbpeele on 2/2/16.
 */
abstract class TransitionActivity : BaseActivity() {

    private var hasSavedInstanceState: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        hasSavedInstanceState = savedInstanceState != null
    }

    override fun setContentView(layoutResID: Int) {
        super.setContentView(layoutResID)
        PreDrawer.addPreDrawer(root) {
            onEnter(root, intent, hasSavedInstanceState)
            true
        }
    }

    override fun onBackPressed() {
        if (overrideTransitions()) {
            onExit(root)
        } else {
            onExit(root)
            super.onBackPressed()
        }
    }

    internal abstract fun overrideTransitions(): Boolean

    internal abstract fun onEnter(root: ViewGroup, calledIntent: Intent, hasSavedInstanceState: Boolean)

    internal abstract fun onExit(root: ViewGroup)
}
