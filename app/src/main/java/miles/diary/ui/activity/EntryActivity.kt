package miles.diary.ui.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.PorterDuff
import android.net.Uri
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.Snackbar
import android.support.v4.content.ContextCompat
import android.text.SpannableString
import android.text.Spanned
import android.text.style.RelativeSizeSpan
import android.transition.ArcMotion
import android.transition.ChangeImageTransform
import android.transition.Transition
import android.transition.TransitionSet
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.animation.DecelerateInterpolator
import android.widget.TextView
import android.widget.Toolbar
import butterknife.BindView
import butterknife.OnClick
import com.google.gson.Gson
import miles.diary.R
import miles.diary.data.model.realm.Entry
import miles.diary.data.model.weather.WeatherResponse
import miles.diary.data.rx.ActivitySubscriber
import miles.diary.ui.TypefacerSpan
import miles.diary.ui.transition.RoundedImageViewTransition
import miles.diary.ui.transition.SimpleTransitionListener
import miles.diary.ui.widget.RoundedImageView
import miles.diary.ui.widget.TypefaceIconTextView
import miles.diary.ui.widget.TypefaceTextView
import miles.diary.util.AnimUtils
import miles.diary.util.TextUtils
import miles.diary.util.ViewUtils

/**
 * Created by mbpeele on 2/8/16.
 */
class EntryActivity : TransitionActivity() {

    @BindView(R.id.activity_entry_place_photos)
    internal lateinit var photosFab: FloatingActionButton
    @BindView(R.id.activity_entry_toolbar)
    internal lateinit var toolbar: Toolbar
    @BindView(R.id.activity_entry_body)
    internal lateinit var body: TypefaceTextView
    @BindView(R.id.activity_entry_image)
    internal lateinit var image: RoundedImageView
    @BindView(R.id.activity_entry_place)
    internal lateinit var place: TypefaceTextView
    @BindView(R.id.activity_entry_date)
    internal lateinit var date: TypefaceTextView
    @BindView(R.id.activity_entry_weather)
    internal lateinit var weatherView: TypefaceIconTextView

    private lateinit var entry: Entry

    enum class Action {
        EDIT,
        DELETE
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_entry)

        toolbar.title = ""
        setActionBar(toolbar)
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
        }

        entry = repository.get(Entry::class.java, intent.getLongExtra(INTENT_KEY, -1))
        updateView(entry)
    }

    override fun overrideTransitions(): Boolean {
        return false
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
                val intent = Intent(this, NewEntryActivity::class.java)
                intent.putExtra(EntryActivity.INTENT_KEY, entry.dateMillis)
                startActivityForResult(intent, REQUEST_EDIT_ENTRY)
            }
            R.id.menu_entry_delete -> setResultAction(Action.DELETE)
            android.R.id.home -> finishAfterTransition()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        when (requestCode) {
            REQUEST_EDIT_ENTRY -> if (resultCode == Activity.RESULT_OK) {
                val bundle = data.extras
                val body = bundle.getString(NewEntryActivity.BODY)
                val uri = bundle.getParcelable<Uri>(NewEntryActivity.URI)
                val placeName = bundle.getString(NewEntryActivity.PLACE_NAME)
                val placeId = bundle.getString(NewEntryActivity.PLACE_ID)

                repository.updateObject({ entry.update(body, uri, placeName, placeId) }).subscribe(object : ActivitySubscriber<Entry>(this) {
                    override fun onNext(entry1: Entry) {
                        entry = entry1
                        updateView(entry)
                    }
                })
            }
            else -> super.onActivityResult(requestCode, resultCode, data)
        }
    }

    override fun onEnter(root: ViewGroup, calledIntent: Intent, hasSavedInstanceState: Boolean) {
        if (entry.uri == null) {
            val color = ContextCompat.getColor(this, R.color.dark_icons)
            toolbar.navigationIcon.mutate().setColorFilter(color, PorterDuff.Mode.SRC_IN)
            toolbar.setTitleTextColor(color)
        } else {
            val arcMotion = ArcMotion()
            arcMotion.minimumHorizontalAngle = 50f
            arcMotion.minimumVerticalAngle = 50f

            val reveal = RoundedImageViewTransition(
                    Math.max(image.width, image.height) / 2f, 0f)
            reveal.addTarget(image)
            reveal.pathMotion = arcMotion

            reveal.addListener(object : SimpleTransitionListener() {
                override fun onTransitionStart(transition: Transition) {
                    toolbar.visibility = View.GONE
                    slideUpView(root, AnimUtils.shortAnim(this@EntryActivity))
                }

                override fun onTransitionEnd(transition: Transition) {
                    AnimUtils.visible(toolbar).start()
                    ViewUtils.setZoomControls(image)
                }
            })

            val returnSet = TransitionSet()

            val unreveal = RoundedImageViewTransition(
                    0f, Math.min(image.width, image.height) / 2f)
            unreveal.addTarget(image)
            unreveal.pathMotion = arcMotion
            unreveal.addListener(object : SimpleTransitionListener() {
                override fun onTransitionStart(transition: Transition) {
                    toolbar.visibility = View.GONE
                }
            })

            returnSet.addTransition(unreveal)
            returnSet.addTransition(ChangeImageTransform())

            window.sharedElementEnterTransition = reveal
            window.sharedElementReturnTransition = returnSet
        }
    }

    internal override fun onExit(root: ViewGroup) {}

    @SuppressLint("SetTextI18n")
    private fun updateView(entry: Entry) {
        ViewUtils.mutate(place, place.currentTextColor)
        ViewUtils.mutate(date, place.currentTextColor)

        val text = "Dear Diary, " +
                TextUtils.repeat(2, TextUtils.LINE_SEPERATOR) +
                TextUtils.repeat(5, TextUtils.TAB) +
                entry.body

        val spannableString = SpannableString(text)
        spannableString.setSpan(RelativeSizeSpan(1.4f), 0, 12, Spanned.SPAN_INCLUSIVE_INCLUSIVE)
        spannableString.setSpan(TypefacerSpan(TextUtils.getFont(this, getString(R.string.default_font))),
                12, spannableString.length, Spanned.SPAN_INCLUSIVE_INCLUSIVE)
        body.setText(spannableString, TextView.BufferType.SPANNABLE)

        val placeName = entry.placeName
        if (placeName != null) {
            place.visibility = View.VISIBLE
            place.text = placeName
        } else {
            photosFab.visibility = View.GONE
            place.visibility = View.GONE
        }

        date.text = TextUtils.formatDate(entry.date) + TextUtils.LINE_SEPERATOR +
                TextUtils.formatTime(entry.date)

        val string = entry.weather
        if (string != null) {
            weatherView.visibility = View.VISIBLE
            val weatherResponse = Gson().fromJson(string, WeatherResponse::class.java)
            weatherView.text = weatherResponse.oneLineTemperatureString
        } else {
            weatherView.visibility = View.GONE
        }

        if (entry.uri != null) {
            postponeEnterTransition()

            image.visibility = View.VISIBLE

//            Glide.with(this)
//                    .fromString()
//                    .asBitmap()
//                    .load(entry.uri)
//                    .centerCrop()
//                    .listener(object : RequestListener<String, Bitmap> {
//                        fun onException(e: Exception, model: String, target: Target<Bitmap>, isFirstResource: Boolean): Boolean {
//                            Logg.log(e)
//                            return false
//                        }
//
//                        fun onResourceReady(resource: Bitmap, model: String, target: Target<Bitmap>,
//                                            isFromMemoryCache: Boolean, isFirstResource: Boolean): Boolean {
//                            Palette.from(resource)
//                                    .maximumColorCount(3)
//                                    .clearFilters()
//                                    .generate(PaletteWindows(this@EntryActivity, resource))
//
//                            startPostponedEnterTransition()
//                            return false
//                        }
//                    })
//                    .into(image)
        } else {
            image.visibility = View.GONE
        }
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
        val intent = Intent()
        intent.putExtra(INTENT_KEY, action)
        intent.putExtra(INTENT_ACTION, entry.dateMillis)
        setResult(Activity.RESULT_OK, intent)
        finish()
    }

    @OnClick(R.id.activity_entry_place_photos)
    protected fun fabClick() {
        if (hasConnection()) {
            val intent = Intent(this, PlacePhotosActivity::class.java)
            intent.putExtra(PlacePhotosActivity.ID, entry.placeId)
            intent.putExtra(PlacePhotosActivity.NAME, entry.placeName)
            startActivity(intent)
        } else {
            Snackbar.make(root, R.string.error_no_internet, Snackbar.LENGTH_SHORT).show()
        }
    }

    companion object {

        val INTENT_KEY = "data"
        val INTENT_ACTION = "action"
        val REQUEST_EDIT_ENTRY = 1

        fun newIntent(context: Context, entry: Entry): Intent {
            val intent = Intent(context, EntryActivity::class.java)
            intent.putExtra(EntryActivity.INTENT_KEY, entry.dateMillis)
            return intent
        }
    }
}
