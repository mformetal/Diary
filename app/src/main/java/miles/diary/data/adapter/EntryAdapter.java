package miles.diary.data.adapter;

import android.app.ActivityOptions;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;

import butterknife.Bind;
import miles.diary.R;
import miles.diary.data.model.Entry;
import miles.diary.data.model.weather.WeatherResponse;
import miles.diary.ui.activity.BaseActivity;
import miles.diary.ui.activity.EntryActivity;
import miles.diary.ui.activity.HomeActivity;
import miles.diary.ui.widget.TypefaceIconTextView;
import miles.diary.ui.widget.TypefaceTextView;
import miles.diary.util.AnimUtils;
import miles.diary.util.TextUtils;

/**
 * Created by mbpeele on 1/14/16.
 */
public class EntryAdapter extends BaseAdapter<RecyclerView.ViewHolder> {

    private static final int TYPE_IMAGE = 0;
    private static final int TYPE_TEXT = 1;
    private static final int TYPE_VIDEO = 2;

    private Gson gson;

    public EntryAdapter(BaseActivity activity) {
        super(activity);
        gson = new Gson();
    }

    @Override
    public int getItemViewType(int position) {
        Entry entry = getItem(position);
        if (entry.getUri() == null) {
            return TYPE_TEXT;
        } else {
            return TYPE_IMAGE;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case TYPE_IMAGE:
                return new ImageViewHolder(layoutInflater.inflate(R.layout.adapter_entry_image, parent, false));
            case TYPE_TEXT:
                return new TextViewHolder(layoutInflater.inflate(R.layout.adapter_entry_text, parent, false));
        }
        return null;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        final Entry entry = getItem(position);

        if (entry != null) {
            if (holder instanceof TextViewHolder) {
                ((TextViewHolder) holder).bind(entry);
            } else if (holder instanceof ImageViewHolder) {
                ((ImageViewHolder) holder).bind(entry);
            }
        }
    }

    @Override
    public Entry getItem(int position) {
        return (Entry) data.get(position);
    }

    class TextViewHolder extends BindingViewHolder<Entry> {

        @Bind(R.id.adapter_entry_text_body) TypefaceTextView body;
        @Bind(R.id.adapter_entry_text_time) TypefaceTextView time;
        @Bind(R.id.adapter_entry_text_location) TypefaceTextView location;
        @Bind(R.id.adapter_entry_text_temperature) TypefaceIconTextView weather;

        public TextViewHolder(View itemView) {
            super(itemView);
        }

        @Override
        public void bind(final Entry entry) {
            ((View) body.getParent()).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = EntryActivity.newIntent(host, entry);
                    host.startActivityForResult(intent, HomeActivity.RESULT_CODE_ENTRY);
                }
            });

            body.setText(entry.getBody());
            time.setText(TextUtils.formatDate(entry.getDate()));

            String temperature = entry.getWeather();
            if (temperature != null) {
                WeatherResponse weatherResponse = gson.fromJson(temperature, WeatherResponse.class);
                weather.setText(weatherResponse.getOneLineTemperatureString());
            }

            String placeName = entry.getPlaceName();
            if (placeName != null) {
                location.setText(entry.getPlaceName());
            } else {
                location.setVisibility(View.GONE);
            }
        }
    }

    class ImageViewHolder extends BindingViewHolder<Entry> {

        @Bind(R.id.adapter_entry_image_time) TypefaceTextView time;
        @Bind(R.id.adapter_entry_image_view) ImageView image;
        @Bind(R.id.adapter_entry_image_body) TypefaceTextView body;
        @Bind(R.id.adapter_entry_image_location) TypefaceTextView location;
        @Bind(R.id.adapter_entry_image_temperature) TypefaceIconTextView weather;

        public ImageViewHolder(View itemView) {
            super(itemView);
        }

        @Override
        public void bind(final Entry entry) {
            image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = EntryActivity.newIntent(host, entry);
                    ActivityOptions options =
                            ActivityOptions.makeSceneTransitionAnimation(host, image,
                                    host.getString(R.string.transition_image));
                    host.startActivityForResult(intent, HomeActivity.RESULT_CODE_ENTRY, options.toBundle());
                }
            });
            time.setText(TextUtils.formatDate(entry.getDate()));
            body.setText(entry.getBody());

            String temperature = entry.getWeather();
            if (temperature != null) {
                WeatherResponse weatherResponse = gson.fromJson(temperature, WeatherResponse.class);
                weather.setText(weatherResponse.getOneLineTemperatureString());
            }

            String placeName = entry.getPlaceName();
            if (placeName != null) {
                location.setText(placeName);
            } else {
                location.setVisibility(View.GONE);
            }

            Glide.with(host)
                    .fromString()
                    .load(entry.getUri())
                    .animate(AnimUtils.REVEAL)
                    .centerCrop()
                    .into(image);
        }
    }
}
