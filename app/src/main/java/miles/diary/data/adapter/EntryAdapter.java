package miles.diary.data.adapter;

import android.app.ActivityOptions;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Pair;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;

import java.util.List;
import java.util.Objects;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.realm.RealmObject;
import miles.diary.R;
import miles.diary.data.model.realm.Entry;
import miles.diary.data.model.weather.WeatherResponse;
import miles.diary.ui.activity.BaseActivity;
import miles.diary.ui.activity.EntryActivity;
import miles.diary.ui.activity.HomeActivity;
import miles.diary.ui.widget.TypefaceIconTextView;
import miles.diary.ui.widget.TypefaceTextView;
import miles.diary.util.AnimUtils;
import miles.diary.util.Logg;
import miles.diary.util.TextUtils;

/**
 * Created by mbpeele on 1/14/16.
 */
public class EntryAdapter extends BaseRealmAdapter<Entry, RecyclerView.ViewHolder> {

    private static final int TYPE_IMAGE = 0;
    private static final int TYPE_TEXT = 1;
    private static final int TYPE_VIDEO = 2;

    private final Gson gson;

    public EntryAdapter(BaseActivity activity) {
        super(activity);
        gson = new Gson();
    }

    @Override
    public int getItemViewType(int position) {
        Entry entry = getObject(position);
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
                return new ImageViewHolder(getLayoutInflater().inflate(R.layout.adapter_entry_image, parent, false));
            case TYPE_TEXT:
                return new TextViewHolder(getLayoutInflater().inflate(R.layout.adapter_entry_text, parent, false));
        }
        return null;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        final Entry entry = getObject(position);

        if (holder instanceof TextViewHolder) {
            ((TextViewHolder) holder).bind(entry);
        } else if (holder instanceof ImageViewHolder) {
            ((ImageViewHolder) holder).bind(entry);
        }
    }

    public void addAndSort(Entry entry) {
        List<Entry> entries = getData();
        if (entries.isEmpty()) {
            addObject(entry);
        } else {
            long target = entry.getDateMillis();
            long diff = Integer.MAX_VALUE;
            int ndx = 0;

            for (int i = 0; i < entries.size(); i++) {
                Entry entry1 = entries.get(i);
                long dataTime = entry1.getDateMillis();

                long timeDiff = Math.abs(target - dataTime);
                if (timeDiff < diff) {
                    diff = timeDiff;
                    ndx = i;
                }
            }

            addAtPosition(entry, ndx);
        }
    }

    public void update(Entry entry) {
        List<Entry> entries = getData();
        for (int i = 0; i < entries.size(); i++) {
            Entry entry1 = entries.get(i);
            if (entry.isEqualTo(entry1)) {
                removeObject(i);
                addAtPosition(entry, i);
                break;
            }
        }
    }

    class TextViewHolder extends BindingViewHolder<Entry> {

        @BindView(R.id.adapter_entry_text_body) TypefaceTextView body;
        @BindView(R.id.adapter_entry_text_time) TypefaceTextView time;
        @BindView(R.id.adapter_entry_text_location) TypefaceTextView location;
        @BindView(R.id.adapter_entry_text_temperature) TypefaceIconTextView weather;

        public TextViewHolder(View itemView) {
            super(itemView);
        }

        @Override
        public void bindViews(View itemView) {
            ButterKnife.bind(this, itemView);
        }

        @Override
        public void bind(final Entry entry) {
            ((View) body.getParent()).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = EntryActivity.newIntent(getHost(), entry);
                    ActivityOptions options =
                            ActivityOptions.makeSceneTransitionAnimation(getHost(),
                                    body, getHost().getString(R.string.transition_entry_text));
                    getHost().startActivityForResult(intent, HomeActivity.RESULT_CODE_ENTRY, options.toBundle());
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

        @BindView(R.id.adapter_entry_image_time) TypefaceTextView time;
        @BindView(R.id.adapter_entry_image_view) ImageView image;
        @BindView(R.id.adapter_entry_image_body) TypefaceTextView body;
        @BindView(R.id.adapter_entry_image_location) TypefaceTextView location;
        @BindView(R.id.adapter_entry_image_temperature) TypefaceIconTextView weather;

        public ImageViewHolder(View itemView) {
            super(itemView);
        }

        @Override
        public void bindViews(View itemView) {
            ButterKnife.bind(this, itemView);
        }

        @Override
        public void bind(final Entry entry) {
            image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = EntryActivity.newIntent(getHost(), entry);
                    ActivityOptions options =
                            ActivityOptions.makeSceneTransitionAnimation(getHost(),
                                   image, getHost().getString(R.string.transition_entry_image));
                    getHost().startActivityForResult(intent, HomeActivity.RESULT_CODE_ENTRY, options.toBundle());
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

            Glide.with(getHost())
                    .fromString()
                    .load(entry.getUri())
                    .animate(AnimUtils.REVEAL)
                    .centerCrop()
                    .into(image);
        }
    }
}
