package miles.diary.home

import android.annotation.SuppressLint
import android.app.Activity
import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.NavigationView
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toolbar
import mformetal.kodi.android.KodiActivity
import mformetal.kodi.core.Kodi
import mformetal.kodi.core.api.ScopeRegistry
import mformetal.kodi.core.api.injection.register
import miles.diary.R
import miles.diary.data.adapter.EntryAdapter
import miles.diary.data.api.EntryRepository
import miles.diary.newentry.NewEntryActivity
import miles.diary.ui.TintingSearchListener
import miles.diary.ui.addPreDrawer
import miles.diary.ui.transition.FabContainerTransition
import miles.diary.ui.widget.SearchWidget
import miles.diary.util.AnimUtils
import miles.diary.util.Storage
import miles.diary.util.extensions.findView
import miles.diary.util.extensions.root

class HomeActivity : KodiActivity() {

    internal val storage: Storage by injector.register()
    internal val entryRepository: EntryRepository by injector.register {
        it.open()
    }

    val recyclerView: RecyclerView by findView(R.id.activity_home_recycler)
    val toolbar: Toolbar by findView(R.id.activity_home_toolbar)
    val progressBar: ProgressBar by findView(R.id.activity_home_loading)
    val fab: FloatingActionButton by findView(R.id.activity_home_fab)
    val searchWidget: SearchWidget by findView(R.id.activity_home_search_widget)
    val drawerLayout: DrawerLayout by findView(R.id.activity_home_drawer)
    val navigationView: NavigationView by findView(R.id.activity_home_navigation_view)

    lateinit var entryAdapter: EntryAdapter
    var emptyView: View? = null

    override fun installModule(kodi: Kodi): ScopeRegistry {
        return Kodi.EMPTY_REGISTRY
    }

    @SuppressLint("MissingSuperCall")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.home)

        entryRepository.open()

        runEnterAnimation()

        setActionBar(toolbar)
        toolbar.setNavigationIcon(R.drawable.ic_menu_24dp)
        toolbar.setNavigationOnClickListener {
            if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                drawerLayout.closeDrawer(GravityCompat.START)
            } else {
                drawerLayout.openDrawer(GravityCompat.START)
            }
        }

        addNavigationViewClickListener()

        if (storage.getBoolean("firstTime", true)) {
            drawerLayout.openDrawer(GravityCompat.START)
            storage.setBoolean("firstTime", false)
        }

        entryAdapter = EntryAdapter(this, entryRepository.getAllEntries())
        val layoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = entryAdapter

        fab.setOnClickListener { startNewEntryActivity() }

        addSearchListener()
    }

    @SuppressLint("MissingSuperCall")
    override fun onDestroy() {
        super.onDestroy()

        entryRepository.close()
    }

    override fun onBackPressed() {
        if (searchWidget.interceptBackButton()) {
            return
        }

        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
            return
        }

        super.onBackPressed()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_home, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_home_search -> {
                val pos = IntArray(2)
                val search = toolbar.findViewById<View>(R.id.menu_home_search)
                search.getLocationOnScreen(pos)
                searchWidget.toggle(pos)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            RESULT_CODE_NEW_ENTRY -> {
                if (resultCode == Activity.RESULT_OK) {
                    scrollToTop()

                    if (emptyView != null) {
                        emptyView!!.visibility = View.GONE
                    }
                }
            }
            else -> {
                super.onActivityResult(requestCode, resultCode, data)
            }
        }
    }

    private fun addNavigationViewClickListener() {
        navigationView.setNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_menu_home_calendar -> {
                }
                R.id.navigation_menu_home_map -> {

                }
                R.id.navigation_menu_home_ideas -> {
                }
            }
            true
        }
    }

    private fun addSearchListener() {
        val searchListener = object : TintingSearchListener(root, ContextCompat.getColor(this, R.color.window_background)) {
            override fun onSearchShow(position: IntArray) {
                super.onSearchShow(position)
                fab.animate().alpha(.4f).duration = 350
            }

            override fun onSearchDismiss(position: IntArray) {
                super.onSearchDismiss(position)
                fab.animate().alpha(1f).duration = 350
            }
        }

        searchWidget.addSearchListener(searchListener)
    }

    private fun scrollToTop() {
        val linearLayoutManager = recyclerView.layoutManager as LinearLayoutManager
        if (linearLayoutManager.findFirstCompletelyVisibleItemPosition() == 0) {
            linearLayoutManager.scrollToPosition(0)
        }
    }

    private fun runEnterAnimation() {
        root.addPreDrawer {
            fab.startAnimation(AnimationUtils.loadAnimation(it.context, R.anim.glide_pop))

            val textView = toolbar.getChildAt(0) as TextView

            AnimUtils.textScale(textView, null, .8f, 1f)
                    .setDuration(AnimUtils.longAnim(it.context).toLong())
                    .start()

            AnimUtils.alpha(textView, 0f, 1f)
                    .setDuration(AnimUtils.longAnim(it.context).toLong())
                    .start()
        }
    }

    private fun startNewEntryActivity() {
        val intent = Intent(this, NewEntryActivity::class.java)
        intent.putExtra(FabContainerTransition.START_COLOR, ContextCompat.getColor(this, R.color.accent))
        intent.putExtra(FabContainerTransition.END_COLOR, ContextCompat.getColor(this, R.color.window_background))
        val options = ActivityOptions.makeSceneTransitionAnimation(this, fab,
                getString(R.string.transition_fab_dialog_new_entry))
        ActivityCompat.startActivityForResult(this, intent, RESULT_CODE_ENTRY,
                options.toBundle())
    }

    private fun dismissLoading() {
        if (progressBar.visibility != View.GONE) {
            AnimUtils.gone(progressBar)
                    .setDuration(AnimUtils.longAnim(this).toLong())
                    .start()
        }
    }

    private fun showLoading() {
        if (progressBar.visibility != View.VISIBLE) {
            AnimUtils.visible(progressBar)
                    .setDuration(AnimUtils.longAnim(this).toLong())
                    .start()
        }
    }

    companion object {

        private val RESULT_CODE_NEW_ENTRY = 1
        val RESULT_CODE_ENTRY = 2
    }
}
