package miles.diary.data.adapter

import android.app.Activity
import android.app.ActivityOptions
import android.support.v4.app.ActivityCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.google.gson.Gson
import io.realm.OrderedRealmCollection
import io.realm.RealmRecyclerViewAdapter
import miles.diary.R
import miles.diary.data.model.realm.Entry
import miles.diary.data.model.weather.WeatherResponse
import miles.diary.home.HomeActivity
import miles.diary.ui.activity.EntryActivity
import miles.diary.ui.widget.TypefaceIconTextView
import miles.diary.ui.widget.TypefaceTextView
import miles.diary.util.TextUtils
import miles.diary.util.extensions.findView

/**
 * Created by mbpeele on 1/14/16.
 */
class EntryAdapter(private val host: Activity,
                   data: OrderedRealmCollection<Entry>) : RealmRecyclerViewAdapter<Entry, TypedViewHolder<Entry>>(data, true, true) {

    private val gson = Gson()

    override fun getItemViewType(position: Int): Int {
        val entry = getItem(position)
        return if (entry!!.uri == null) {
            TYPE_TEXT
        } else {
            TYPE_IMAGE
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TypedViewHolder<Entry> {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            TYPE_IMAGE -> {
                ImageViewHolder(inflater.inflate(R.layout.vh_entry_image, parent, false))
            }
            TYPE_TEXT -> {
                TextViewHolder(inflater.inflate(R.layout.vh_entry_text, parent, false))
            }
            else -> {
                throw IllegalArgumentException("Unknown type $viewType")
            }
        }
    }

    override fun onBindViewHolder(holder: TypedViewHolder<Entry>, position: Int) {
        val entry = getItem(position)!!
        holder.bind(entry)
    }

    inner class TextViewHolder(itemView: View) : TypedViewHolder<Entry>(itemView) {

        val body: TypefaceTextView by findView(R.id.adapter_entry_text_body)
        val time: TypefaceTextView by findView(R.id.adapter_entry_text_time)
        val location: TypefaceTextView by findView(R.id.adapter_entry_text_location)
        val weather: TypefaceIconTextView by findView(R.id.adapter_entry_text_temperature)

        override fun bind(model: Entry) {
            itemView.setOnClickListener {
                val intent = EntryActivity.newIntent(host, model)
                val options =
                        ActivityOptions.makeSceneTransitionAnimation(host,
                                body, host.getString(R.string.transition_entry_text))
                ActivityCompat.startActivityForResult(host, intent,
                        HomeActivity.RESULT_CODE_ENTRY, options.toBundle())
            }

            body.text = model.body
            time.text = TextUtils.formatDate(model.createdAt)

            val temperature = model.weather
            if (temperature != null) {
                val weatherResponse = gson.fromJson(temperature, WeatherResponse::class.java)
                weather.text = weatherResponse.oneLineTemperatureString
            }

            val placeName = model.placeName
            if (placeName != null) {
                location.text = model.placeName
            } else {
                location.visibility = View.GONE
            }
        }
    }

    inner class ImageViewHolder(itemView: View) : TypedViewHolder<Entry>(itemView) {

        val time: TypefaceTextView by findView(R.id.adapter_entry_image_time)
        val image: ImageView by findView(R.id.adapter_entry_image_view)
        val body: TypefaceTextView by findView(R.id.adapter_entry_image_body)
        val location: TypefaceTextView by findView(R.id.adapter_entry_image_location)
        val weather: TypefaceIconTextView by findView(R.id.adapter_entry_image_temperature)

        override fun bind(model: Entry) {
            image.setOnClickListener {
                val intent = EntryActivity.newIntent(host, model)
                val options = ActivityOptions.makeSceneTransitionAnimation(host,
                        image, host.getString(R.string.transition_entry_image))
                ActivityCompat.startActivityForResult(host, intent,
                        HomeActivity.RESULT_CODE_ENTRY, options.toBundle())
            }

            time.text = TextUtils.formatDate(model.createdAt)
            body.text = model.body

            val temperature = model.weather
            if (temperature != null) {
                val weatherResponse = gson.fromJson(temperature, WeatherResponse::class.java)
                weather.text = weatherResponse.oneLineTemperatureString
            }

            val placeName = model.placeName
            if (placeName != null) {
                location.text = placeName
            } else {
                location.visibility = View.GONE
            }
        }
    }

    companion object {

        private val TYPE_IMAGE = 0
        private val TYPE_TEXT = 1
        private val TYPE_VIDEO = 2
    }
}
