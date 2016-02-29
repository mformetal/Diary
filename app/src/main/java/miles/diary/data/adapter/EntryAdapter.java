package miles.diary.data.adapter;

import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;

import butterknife.Bind;
import io.realm.Realm;
import io.realm.RealmResults;
import miles.diary.R;
import miles.diary.data.ActivitySubscriber;
import miles.diary.data.model.Entry;
import miles.diary.data.model.weather.WeatherResponse;
import miles.diary.ui.activity.BaseActivity;
import miles.diary.ui.activity.EntryActivity;
import miles.diary.ui.activity.NewEntryActivity;
import miles.diary.ui.widget.TypefaceIconTextView;
import miles.diary.ui.widget.TypefaceTextView;
import miles.diary.util.AnimUtils;
import rx.functions.Func1;

/**
 * Created by mbpeele on 1/14/16.
 */
public class EntryAdapter extends BackendAdapter<Entry, RecyclerView.ViewHolder> {

    private static final int TYPE_IMAGE = 0;
    private static final int TYPE_TEXT = 1;
    private static final int TYPE_VIDEO = 2;

    private BaseActivity host;
    private LayoutInflater layoutInflater;
    private Gson gson;

    public EntryAdapter(BaseActivity activity, Realm realm) {
        super(realm);
        setListener((BackendAdapterListener) activity);

        gson = new Gson();
        host = activity;
        layoutInflater = LayoutInflater.from(activity);

        loadData(realm);
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

        if (holder instanceof TextViewHolder) {
            ((TextViewHolder) holder).bind(entry);
        } else if (holder instanceof ImageViewHolder) {
            ((ImageViewHolder) holder).bind(entry);
        }
    }

    @Override
    public void loadData(Realm realm) {
        if (verifyDataSource(realm)) {
            realm.where(Entry.class)
                    .findAllAsync()
                    .asObservable()
                    .filter(new Func1<RealmResults<Entry>, Boolean>() {
                        @Override
                        public Boolean call(RealmResults<Entry> entries) {
                            return entries.isLoaded();
                        }
                    })
                    .subscribe(new ActivitySubscriber<RealmResults<Entry>>(host) {
                        @Override
                        public void onNext(RealmResults<Entry> entries) {
                            super.onNext(entries);
                            setData(entries);
                            if (getData().isEmpty()) {
                                propagateEmpty();
                            } else {
                                propagateCompletion();
                            }
                        }

                        @Override
                        public void onError(Throwable e) {
                            super.onError(e);
                            propagateError(e);
                        }

                        @Override
                        public void onStart() {
                            propagateStart();
                        }
                    });
        }
    }

    @Override
    public void addData(Entry object) {
        loadData(realm);
    }

    @Override
    public boolean verifyDataSource(Realm realm) {
        return !realm.isClosed();
    }

    public Entry addEntry(Bundle bundle) {
        String body = bundle.getString(NewEntryActivity.BODY);
        Uri uri = bundle.getParcelable(NewEntryActivity.URI);
        String placeName = bundle.getString(NewEntryActivity.PLACE_NAME);
        String placeId = bundle.getString(NewEntryActivity.PLACE_ID);
        String weather = bundle.getString(NewEntryActivity.TEMPERATURE);

//        WeatherResponse weatherResponse = new Gson().fromJson(weather, WeatherResponse.class);

        Entry entry = Entry.construct(realm, body, uri, placeName, placeId, weather);
        addData(entry);
        return entry;
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
        public void bind(Entry entry) {
            body.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
            body.setText(entry.getBody());
            time.setText(Entry.formatDateString(entry));

            String temperature = entry.getWeather();
            if (temperature != null) {
                WeatherResponse weatherResponse = gson.fromJson(temperature, WeatherResponse.class);
                String[] parts = weatherResponse.getTemperatureParts();
                String text = parts[0] + " " + parts[1];
                weather.setText(text);
            }

            String placeName = entry.getPlaceName();
            if (placeName != null) {
                location.setText(entry.getPlaceName());
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
                    EntryActivity.newIntent(host, image, entry);
                }
            });
            time.setText(Entry.formatDateString(entry));
            body.setText(entry.getBody());

            String temperature = entry.getWeather();
            if (temperature != null) {
                WeatherResponse weatherResponse = gson.fromJson(temperature, WeatherResponse.class);
                String[] parts = weatherResponse.getTemperatureParts();
                String text = parts[0] + " " + parts[1];
                weather.setText(text);
            }

            String placeName = entry.getPlaceName();
            if (placeName != null) {
                location.setText(placeName);
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
