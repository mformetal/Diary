package miles.diary.data.adapter;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.util.Date;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.RealmResults;
import miles.diary.R;
import miles.diary.data.ActivitySubscriber;
import miles.diary.data.model.Entry;
import miles.diary.data.model.WeatherResponse;
import miles.diary.ui.activity.BaseActivity;
import miles.diary.ui.activity.NewEntryActivity;
import miles.diary.ui.widget.TypefaceTextView;
import miles.diary.util.AnimUtils;
import rx.functions.Func1;

/**
 * Created by mbpeele on 1/14/16.
 */
public class EntryRecycler extends BackendAdapter<Entry> {

    private static final int TYPE_IMAGE = 0;
    private static final int TYPE_TEXT = 1;
    private static final int TYPE_VIDEO = 2;

    private BaseActivity host;
    private LayoutInflater layoutInflater;

    public EntryRecycler(BaseActivity activity, Realm realm) {
        super(realm);
        setListener((BackendAdapterListener) activity);
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

        if (entry.getUri() == null) {
            bindTextViewHolder((TextViewHolder) holder, entry);
        } else {
            bindImageViewHolder((ImageViewHolder) holder, entry);
        }
    }

    public Entry addEntry(Bundle bundle) {
        String body = bundle.getString(NewEntryActivity.RESULT_BODY);
        Uri uri = bundle.getParcelable(NewEntryActivity.RESULT_URI);
        String placeName = bundle.getString(NewEntryActivity.RESULT_PLACE_NAME);
        String placeId = bundle.getString(NewEntryActivity.RESULT_PLACE_ID);
        WeatherResponse.Weather weather = bundle.getParcelable(NewEntryActivity.RESULT_WEATHER);

        return Entry.construct(realm, body, uri, placeName, placeId, weather);
    }

    private void bindImageViewHolder(ImageViewHolder holder, Entry entry) {
        holder.time.setText(Entry.formatDateString(entry));
        holder.body.setText(entry.getBody());
        String placeName = entry.getPlaceName();
        if (placeName != null) {
            holder.location.setText(entry.getPlaceName());
        } else {
            holder.location.setVisibility(View.GONE);
        }

        Glide.with(host)
                .fromString()
                .load(entry.getUri())
                .animate(AnimUtils.REVEAL)
                .into(holder.image);
    }

    private void bindTextViewHolder(TextViewHolder holder, Entry entry) {
        holder.body.setText(entry.getBody());
        holder.time.setText(Entry.formatDateString(entry));
        String placeName = entry.getPlaceName();
        if (placeName != null) {
            holder.location.setText(entry.getPlaceName());
        } else {
            holder.location.setVisibility(View.GONE);
        }
    }

    @Override
    public void loadData(Realm realm) {
        realm.where(Entry.class)
                .findAllAsync()
                .asObservable()
                .filter(RealmResults::isLoaded)
                .subscribe(new ActivitySubscriber<RealmResults<Entry>>(host) {
                    @Override
                    public void onNext(RealmResults<Entry> entries) {
                        super.onNext(entries);
                        setData(entries);
                        if (getData().isEmpty()) {
                            propogateEmpty();
                        } else {
                            propogateCompletion();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        propogateError(e);
                    }
                });
    }

    final class TextViewHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.adapter_entry_text_body) TypefaceTextView body;
        @Bind(R.id.adapter_entry_text_time) TypefaceTextView time;
        @Bind(R.id.adapter_entry_text_location) TypefaceTextView location;

        public TextViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    final class ImageViewHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.adapter_entry_image_time) TypefaceTextView time;
        @Bind(R.id.adapter_entry_image_view) ImageView image;
        @Bind(R.id.adapter_entry_text_body) TypefaceTextView body;
        @Bind(R.id.adapter_entry_image_location) TypefaceTextView location;

        public ImageViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
