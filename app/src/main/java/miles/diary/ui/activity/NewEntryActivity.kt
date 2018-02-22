package miles.diary.ui.activity

import android.Manifest
import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v4.view.animation.FastOutSlowInInterpolator
import android.transition.ArcMotion
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toolbar
import com.google.gson.Gson
import mformetal.kodi.android.KodiActivity
import mformetal.kodi.core.Kodi
import mformetal.kodi.core.api.ScopeRegistry
import miles.diary.R
import miles.diary.data.model.realm.Entry
import miles.diary.data.model.weather.WeatherResponse
import miles.diary.ui.transition.ContainerFabTransition
import miles.diary.ui.transition.FabContainerTransition
import miles.diary.ui.widget.CircleImageView
import miles.diary.ui.widget.TypefaceButton
import miles.diary.ui.widget.TypefaceEditText
import miles.diary.util.AnimUtils
import miles.diary.util.LocationUtils
import miles.diary.util.ViewUtils
import miles.diary.util.extensions.*

class NewEntryActivity : KodiActivity(), View.OnClickListener {

    val toolbar: Toolbar by findView(R.id.fragment_entry_toolbar)
    val bodyInput: TypefaceEditText by findView(R.id.activity_new_entry_body)
    val photo: CircleImageView by findView(R.id.activity_new_entry_photo)
    val locationName: TypefaceButton by findView(R.id.activity_new_entry_location)

    internal var placeName: String? = null
    internal var placeId: String? = null
    internal var temperature: String? = null
    internal var imageUri: Uri? = null
    internal var location: Location? = null
    private var weatherResponse: WeatherResponse? = null

    override fun installModule(kodi: Kodi): ScopeRegistry {
        return Kodi.EMPTY_REGISTRY
    }

    @SuppressLint("MissingSuperCall")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_entry)
        setupTransitions()

        setActionBar(toolbar)

        val intent = intent
        val bundle = intent.extras
        if (bundle != null) {
            val id = bundle.getLong(EntryActivity.INTENT_KEY, -1L)
            if (id != -1L) {

            }
        }

        ViewUtils.mutate(locationName, ContextCompat.getColor(this, R.color.accent))
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        when (requestCode) {
            RESULT_LOCATION -> if (resultCode == Activity.RESULT_OK) {
                val extras = data.extras
                placeName = extras!!.getString(NewEntryActivity.PLACE_NAME)
                placeId = extras.getString(NewEntryActivity.PLACE_ID)

                setLocationText(placeName)
            }
            RESULT_IMAGE -> if (resultCode == Activity.RESULT_OK) {
                imageUri = data.data

                loadThumbnailFromUri(imageUri)
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
                    val result = Intent()
                    result.putExtra(BODY, body)
                    result.putExtra(URI, imageUri)
                    result.putExtra(PLACE_NAME, placeName)
                    result.putExtra(PLACE_ID, placeId)
                    result.putExtra(LOCATION, location)
                    if (weatherResponse != null) {
                        result.putExtra(TEMPERATURE, Gson().toJson(weatherResponse, WeatherResponse::class.java))
                    }
                    setResult(Activity.RESULT_OK, result)
                    finishAfterTransition()
                } else {
                    Snackbar.make(root, R.string.new_entry_no_input_error,
                            Snackbar.LENGTH_SHORT).show()
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.activity_new_entry_photo -> {

            }
            R.id.activity_new_entry_location -> {

            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            REQUEST_LOCATION_PERMISSION -> if (permissionsGranted(grantResults)) {
                getLocationData()
            } else {
                finish()
            }
            else -> super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }

    private fun updateViews(entry: Entry?) {
        if (entry != null) {
            val stringUri = entry.uri
            if (stringUri != null) {
                loadThumbnailFromUri(stringUri)
                imageUri = Uri.parse(stringUri)
            }

            bodyInput.setText(entry.body)

            val string = entry.uri
            if (string != null) {
                loadThumbnailFromUri(string)
            }

            val entryWeather = entry.weather
            if (entryWeather != null) {
                weatherResponse = Gson().fromJson(entryWeather, WeatherResponse::class.java)
                temperature = weatherResponse!!.twoLineTemperatureString
            }

            placeName = entry.placeName
            placeId = entry.placeId
            if (placeName != null) {
                setLocationText(placeName)
            }
        }
    }

    private fun setLocationText(name: String?) {
        val objectAnimator = AnimUtils.visible(locationName)
        objectAnimator.startDelay = 400
        objectAnimator.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationStart(animation: Animator) {
                locationName!!.text = name
            }
        })
        objectAnimator.start()
    }

    private fun getPlace(location1: Location?) {
        if (placeName == null && placeId == null) {
            val pulse = ObjectAnimator.ofFloat(locationName, View.SCALE_X, .95f, 1f)
            pulse.duration = AnimUtils.mediumAnim(this).toLong()
            pulse.repeatCount = ValueAnimator.INFINITE
            pulse.repeatMode = ValueAnimator.REVERSE
            pulse.start()
        } else {
            setLocationText(placeName)
        }
    }

    private fun getWeather(location: Location?) {
        if (temperature == null) {

        }
    }

    private fun loadThumbnailFromUri(uri: Uri?) {
        if (uri != null) {
//            Glide.with(this)
//                    .fromUri()
//                    .animate(R.anim.glide_pop)
//                    .load(uri)
//                    .centerCrop()
//                    .into(photo)
        }
    }

    private fun loadThumbnailFromUri(uri: String?) {
        if (uri != null) {
//            Glide.with(this)
//                    .fromString()
//                    .animate(R.anim.glide_pop)
//                    .load(uri)
//                    .centerCrop()
//                    .into(photo)
        }
    }

    private fun getLocationData() {
        val permissions = arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION)
        if (hasPermissions(*permissions)) {
            if (hasConnection()) {
                if (LocationUtils.isLocationEnabled(this)) {
                    if (location == null) {

                    } else {
                        setLocationText(placeName)
                    }
                } else {
                    Snackbar.make(root,
                            R.string.location_not_enabled,
                            Snackbar.LENGTH_LONG)
                            .setAction(android.R.string.ok) { startActivity(Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS)) }
                            .show()
                }
            } else {
                noInternet()
            }
        } else {
            ActivityCompat.requestPermissions(this, permissions, REQUEST_LOCATION_PERMISSION)
        }
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

        val BODY = "body"
        val URI = "uri"
        val PLACE_NAME = "place"
        val PLACE_ID = "placeId"
        val LOCATION = "locationName"
        val TEMPERATURE = "temperature"
        private val RESULT_LOCATION = 1
        private val RESULT_IMAGE = 2
        private val REQUEST_LOCATION_PERMISSION = 3
    }
}
