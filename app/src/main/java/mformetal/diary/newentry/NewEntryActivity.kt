package mformetal.diary.newentry

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.location.Geocoder
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.ActivityCompat
import android.support.v4.view.animation.FastOutSlowInInterpolator
import android.support.v4.widget.NestedScrollView
import android.support.v7.widget.Toolbar
import android.transition.ArcMotion
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.gson.Gson
import mformetal.diary.R
import mformetal.diary.entry.EntryActivity
import mformetal.diary.ui.addPreDrawer
import mformetal.diary.ui.transition.ContainerFabTransition
import mformetal.diary.ui.transition.FabContainerTransition
import mformetal.diary.util.AnimUtils
import mformetal.diary.util.extensions.*
import mformetal.diary.util.safeObserver
import mformetal.kodi.android.KodiActivity
import mformetal.kodi.core.Kodi
import mformetal.kodi.core.api.ScopeRegistry
import mformetal.kodi.core.api.builder.bind
import mformetal.kodi.core.api.builder.get
import mformetal.kodi.core.api.injection.register
import mformetal.kodi.core.api.scoped
import mformetal.kodi.core.provider.component
import mformetal.kodi.core.provider.provider
import okhttp3.OkHttpClient

class NewEntryActivity : KodiActivity() {

    val toolbar: Toolbar by findView(R.id.toolbar)
    val bodyInput: EditText by findView(R.id.body)
    val photo: ImageView by findView(R.id.photo)
    val address: Button by findView(R.id.address)
    val weather: Button by findView(R.id.weather)
    val nestedScroll: NestedScrollView by findView(R.id.nested_scroll)

    val viewModel: NewEntryViewModel by injector.register()

    override fun installModule(kodi: Kodi): ScopeRegistry {
        return kodi.scopeBuilder()
                .build(scoped<NewEntryActivity>()) {
                    bind<Activity>() using component(this@NewEntryActivity)
                    bind<FusedLocationProviderClient>() using component(LocationServices.getFusedLocationProviderClient(get()))
                    bind<GetWeather>() using provider {
                        GetWeatherUseCase(
                                get(), OkHttpClient(), Gson(),
                                getString(R.string.weather_base),
                                getString(R.string.weather_api_key))
                    }
                    bind<GetAddress>() using provider {
                        GetAddressUseCase(get(), Geocoder(get()))
                    }
                    bind<NewEntryViewModel>() using provider {
                        NewEntryViewModel(get(), get())
                    }
                }
    }

    @SuppressLint("MissingSuperCall")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.new_entry)
        setupTransitions()

        root.addPreDrawer {
            val nestedScrollHeight = nestedScroll.measuredHeight
            val lineHeight = bodyInput.lineHeight
            bodyInput.minLines = nestedScrollHeight.div(lineHeight)
        }

        setSupportActionBar(toolbar)

        intent.extras?.let { bundle ->
            val id = bundle.getLong(EntryActivity.INTENT_KEY, -1L)
            if (id != -1L) {

            }
        }

        requestActivityPermissions(
                permissions = arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION),
                doIfGranted = {
                    observeWeatherAndLocation()
                },
                doIfDenied = {
                    ActivityCompat.requestPermissions(this, it, REQUEST_LOCATION_PERMISSION)
                })
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            REQUEST_LOCATION_PERMISSION -> {
                if (permissionsGranted(grantResults)) {
                    observeWeatherAndLocation()
                } else {
                    weather.gone()
                    address.gone()
                }
            }
            else -> super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        when (requestCode) {
            RESULT_LOCATION -> {
                if (resultCode == Activity.RESULT_OK) {

                }
            }
            RESULT_IMAGE -> {
                if (resultCode == Activity.RESULT_OK) {

                }
            }
            else -> super.onActivityResult(requestCode, resultCode, data)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_new_entry, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_new_entry_done -> {
                val body = bodyInput.textAsString
                if (body.isNotEmpty()) {
                    TODO("add Entry to Realm")
                } else {
                    Snackbar.make(root, R.string.new_entry_no_input_error,
                            Snackbar.LENGTH_SHORT).show()
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun observeWeatherAndLocation() {
        viewModel.getPlace().observe(this, safeObserver {
            address.text = it.getAddressLine(1)
        })

        viewModel.getWeather().observe(this, safeObserver {
            val temperatureString = it.oneLineTemperatureString
            weather.text = temperatureString
        })
    }

    private fun setupTransitions() {
        val dur = AnimUtils.longAnim(this)
        val start = intent.getIntExtra(FabContainerTransition.START_COLOR, Color.TRANSPARENT)
        val end = intent.getIntExtra(FabContainerTransition.END_COLOR, Color.TRANSPARENT)
        val easeInOut = FastOutSlowInInterpolator()

        val sharedEnter = FabContainerTransition(start, end,
                resources.getDimensionPixelSize(R.dimen.fab_corner_radius))
        val arcMotion = ArcMotion()
        arcMotion.minimumHorizontalAngle = 30f
        arcMotion.minimumVerticalAngle = 30f

        sharedEnter.pathMotion = arcMotion
        sharedEnter.interpolator = easeInOut
        sharedEnter.addTarget(root)
        sharedEnter.duration = dur.toLong()

        val sharedReturn = ContainerFabTransition(end, start)
        val returnArc = ArcMotion()
        returnArc.minimumHorizontalAngle = 70f
        returnArc.minimumVerticalAngle = 70f

        sharedReturn.pathMotion = returnArc
        sharedReturn.interpolator = easeInOut
        sharedReturn.addTarget(root)
        sharedReturn.duration = dur.toLong()

        window.sharedElementEnterTransition = sharedEnter
        window.sharedElementReturnTransition = sharedReturn
    }

    companion object {

        private val RESULT_LOCATION = 1
        private val RESULT_IMAGE = 2
        private val REQUEST_LOCATION_PERMISSION = 3
    }
}
