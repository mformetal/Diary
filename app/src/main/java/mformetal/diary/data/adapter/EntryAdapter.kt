package mformetal.diary.data.adapter

import android.app.Activity
import android.app.ActivityOptions
import android.support.v4.app.ActivityCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import io.realm.OrderedRealmCollection
import io.realm.RealmRecyclerViewAdapter
import mformetal.diary.R
import mformetal.diary.data.model.realm.Entry
import mformetal.diary.entry.EntryActivity
import mformetal.diary.home.HomeActivity
import mformetal.diary.ui.widget.TypefaceIconTextView
import mformetal.diary.ui.widget.TypefaceTextView
import mformetal.diary.util.TextUtils
import mformetal.diary.util.extensions.findView
import mformetal.diary.util.extensions.gone
import mformetal.diary.util.extensions.visible
import org.threeten.bp.Instant
import org.threeten.bp.LocalDateTime
import org.threeten.bp.ZoneId

/**
 * Created by mbpeele on 1/14/16.
 */
class EntryAdapter(private val host: Activity,
                   data: OrderedRealmCollection<Entry>) : RealmRecyclerViewAdapter<Entry, TypedViewHolder<Entry>>(data, true, true) {

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
            time.text = TextUtils.formatDate(LocalDateTime.ofInstant(Instant.ofEpochSecond(model.createdAtSeconds), ZoneId.systemDefault()))

            if (model.address == null) {
                location.gone()
            } else {
                location.visible()
                location.text = model.address?.shortStateAddress
            }

            weather.text = model.weather
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

            time.text = TextUtils.formatDate(LocalDateTime.ofInstant(Instant.ofEpochSecond(model.createdAtSeconds), ZoneId.systemDefault()))
            body.text = model.body

            if (model.address == null) {
                location.gone()
            } else {
                location.visible()
                location.text = model.address?.shortStateAddress
            }

            weather.text = model.weather
        }
    }

    companion object {

        private val TYPE_IMAGE = 0
        private val TYPE_TEXT = 1
        private val TYPE_VIDEO = 2
    }
}
