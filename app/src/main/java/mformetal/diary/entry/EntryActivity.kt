package mformetal.diary.entry

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.view.Menu
import android.view.MenuItem
import android.view.ViewGroup
import android.view.animation.DecelerateInterpolator
import android.widget.Toolbar
import mformetal.kodi.android.KodiActivity
import mformetal.kodi.core.Kodi
import mformetal.kodi.core.api.ScopeRegistry
import mformetal.diary.R
import mformetal.diary.data.model.realm.Entry
import mformetal.diary.ui.widget.RoundedImageView
import mformetal.diary.ui.widget.TypefaceIconTextView
import mformetal.diary.ui.widget.TypefaceTextView
import mformetal.diary.util.extensions.findView

/**
 * Created by mbpeele on 2/8/16.
 */
class EntryActivity : KodiActivity() {

    val photosFab: FloatingActionButton by findView(R.id.activity_entry_place_photos)
    val toolbar: Toolbar by findView(R.id.activity_entry_toolbar)
    val body: TypefaceTextView by findView(R.id.activity_entry_body)
    val image: RoundedImageView by findView(R.id.activity_entry_image)
    val place: TypefaceTextView by findView(R.id.activity_entry_place)
    val date: TypefaceTextView by findView(R.id.activity_entry_date)
    val weatherView: TypefaceIconTextView by findView(R.id.activity_entry_weather)

    private lateinit var entry: Entry

    enum class Action {
        EDIT,
        DELETE
    }

    override fun installModule(kodi: Kodi): ScopeRegistry {
        return Kodi.EMPTY_REGISTRY
    }

    @SuppressLint("MissingSuperCall")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.entry)

        toolbar.title = ""
        setActionBar(toolbar)
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_entry, menu)
        (0 until menu.size())
                .map { menu.getItem(it) }
                .forEach {
                    if (entry.uri == null) {
                        it.icon.mutate().setColorFilter(Color.BLACK, PorterDuff.Mode.SRC_IN)
                    }
                }
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_entry_edit -> {

            }
            R.id.menu_entry_delete -> setResultAction(Action.DELETE)
            android.R.id.home -> finishAfterTransition()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        when (requestCode) {
            REQUEST_EDIT_ENTRY -> {
                if (resultCode == Activity.RESULT_OK) {

                }
            }
            else -> super.onActivityResult(requestCode, resultCode, data)
        }
    }

    @SuppressLint("SetTextI18n")
    private fun updateView(entry: Entry) {
//        ViewUtils.mutate(place, place.currentTextColor)
//        ViewUtils.mutate(date, place.currentTextColor)
//
//        val text = "Dear Diary, " +
//                TextUtils.repeat(2, TextUtils.LINE_SEPERATOR) +
//                TextUtils.repeat(5, TextUtils.TAB) +
//                entry.body
//
//        val spannableString = SpannableString(text)
//        spannableString.setSpan(RelativeSizeSpan(1.4f), 0, 12, Spanned.SPAN_INCLUSIVE_INCLUSIVE)
//        spannableString.setSpan(TypefacerSpan(TextUtils.getFont(this, getString(R.string.default_font))),
//                12, spannableString.length, Spanned.SPAN_INCLUSIVE_INCLUSIVE)
//        body.setText(spannableString, TextView.BufferType.SPANNABLE)
//
//        val placeName = entry.placeName
//        if (placeName != null) {
//            place.visibility = View.VISIBLE
//            place.text = placeName
//        } else {
//            photosFab.visibility = View.GONE
//            place.visibility = View.GONE
//        }
//
//        date.text = TextUtils.formatDate(entry.date) + TextUtils.LINE_SEPERATOR +
//                TextUtils.formatTime(entry.date)
//
//        val string = entry.weather
//        if (string != null) {
//            weatherView.visibility = View.VISIBLE
//            val weatherResponse = Gson().fromJson(string, WeatherResponse::class.java)
//            weatherView.text = weatherResponse.oneLineTemperatureString
//        } else {
//            weatherView.visibility = View.GONE
//        }
//
//        if (entry.uri != null) {
//            postponeEnterTransition()
//
//            image.visibility = View.VISIBLE
//
////            Glide.with(this)
////                    .fromString()
////                    .asBitmap()
////                    .load(entry.uri)
////                    .centerCrop()
////                    .listener(object : RequestListener<String, Bitmap> {
////                        fun onException(e: Exception, model: String, target: Target<Bitmap>, isFirstResource: Boolean): Boolean {
////                            Logg.log(e)
////                            return false
////                        }
////
////                        fun onResourceReady(resource: Bitmap, model: String, target: Target<Bitmap>,
////                                            isFromMemoryCache: Boolean, isFirstResource: Boolean): Boolean {
////                            Palette.from(resource)
////                                    .maximumColorCount(3)
////                                    .clearFilters()
////                                    .generate(PaletteWindows(this@EntryActivity, resource))
////
////                            startPostponedEnterTransition()
////                            return false
////                        }
////                    })
////                    .into(image)
//        } else {
//            image.visibility = View.GONE
//        }
    }

    private fun slideUpView(root: ViewGroup, duration: Int) {
        val linearLayout = (root.getChildAt(1) as ViewGroup).getChildAt(0) as ViewGroup
        val offset = linearLayout.height / 2f
        for (i in 0 until linearLayout.childCount) {
            val v = linearLayout.getChildAt(i)

            v.translationY = offset
            v.alpha = 0f

            v.animate()
                    .translationY(0f)
                    .alpha(1f)
                    .setDuration(duration.toLong())
                    .setInterpolator(DecelerateInterpolator())
                    .setStartDelay((150 + 50 * i).toLong())
                    .start()
        }
    }

    private fun setResultAction(action: Action) {
//        val intent = Intent()
//        intent.putExtra(INTENT_KEY, action)
//        intent.putExtra(INTENT_ACTION, entry.dateMillis)
//        setResult(Activity.RESULT_OK, intent)
//        finish()
    }

    protected fun fabClick() {

    }

    companion object {

        val INTENT_KEY = "data"
        val INTENT_ACTION = "action"
        val REQUEST_EDIT_ENTRY = 1

        fun newIntent(context: Context, entry: Entry): Intent {
            val intent = Intent(context, EntryActivity::class.java)
//            intent.putExtra(EntryActivity.INTENT_KEY, entry.dateMillis)
            return intent
        }
    }
}
