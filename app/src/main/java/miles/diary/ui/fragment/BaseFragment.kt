package miles.diary.ui.fragment

import android.app.Fragment
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import butterknife.ButterKnife
import butterknife.Unbinder
import miles.diary.App

/**
 * Created by mbpeele on 3/7/16.
 */
abstract class BaseFragment : Fragment() {

    protected abstract val layoutId: Int
    private lateinit var unbinder : Unbinder

    fun bind(view: View) {
        unbinder = ButterKnife.bind(this, view)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        unbinder.unbind()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        inject(context.applicationContext as App)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle): View? {
        return inflater.inflate(layoutId, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        ButterKnife.bind(this, view)
    }

    abstract fun inject(app: App)
}
