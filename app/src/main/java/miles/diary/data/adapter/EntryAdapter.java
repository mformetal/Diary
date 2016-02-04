package miles.diary.data.adapter;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.UUID;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmResults;
import miles.diary.R;
import miles.diary.data.ActivitySubscriber;
import miles.diary.data.RealmUtils;
import miles.diary.data.model.Entry;
import miles.diary.ui.activity.HomeActivity;
import miles.diary.ui.widget.TypefaceTextView;
import miles.diary.util.AnimUtils;
import miles.diary.util.FileUtils;
import miles.diary.util.Logg;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by mbpeele on 1/14/16.
 */
public class EntryAdapter extends BackendAdapter<Entry, RecyclerView.ViewHolder> {

    private static final int TYPE_IMAGE = 0;
    private static final int TYPE_TEXT = 1;
    private static final int TYPE_VIDEO = 2;

    public EntryAdapter(HomeActivity context, Realm realm) {
        super(context, realm);
    }

    @Override
    public int getItemViewType(int position) {
        Entry entry = getItem(position);
        if (entry.getUriString() == null) {
            return TYPE_TEXT;
        } else {
            return TYPE_IMAGE;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case TYPE_IMAGE:
                return new ImageViewHolder(mLayoutInflater.inflate(R.layout.adapter_entry_image, parent, false));
            case TYPE_TEXT:
                return new TextViewHolder(mLayoutInflater.inflate(R.layout.adapter_entry_text, parent, false));
        }
        return null;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        final Entry entry = getItem(position);

        if (entry.getUriString() == null) {
            bindTextViewHolder((TextViewHolder) holder, entry);
        } else {
            bindImageViewHolder((ImageViewHolder) holder, entry);
        }
    }

    @Override
    public int getItemCount() {
        return mResults.size();
    }

    @Override
    protected void loadData(Realm realm) {
        realm.where(Entry.class)
                .findAllAsync()
                .asObservable()
                .cache()
                .filter(RealmResults::isLoaded)
                .subscribe(new ActivitySubscriber<RealmResults<Entry>>(mActivity) {
                    @Override
                    public void onError(Throwable e) {
                        propogateError(e);
                    }

                    @Override
                    public void onNext(RealmResults<Entry> entries) {
                        mResults = entries;
                        if (entries.isEmpty()) {
                            propogateEmpty();
                        } else {
                            propogateCompletion();
                        }
                    }
                });
    }

    @Override
    protected void setData(RealmResults<Entry> data) {

    }

    public Entry addEntry(String body, Uri uri) {
        mRealm.beginTransaction();
        Entry entry = mRealm.createObject(Entry.class);
        entry.setId(UUID.randomUUID().toString());
        entry.setBody(body);
        entry.setDate(new Date());
        if (uri != null) {
            entry.setUriString(uri.toString());
        }
        mRealm.commitTransaction();
        mRealm.refresh();
        return entry;
    }

    private void bindImageViewHolder(ImageViewHolder holder, Entry entry) {
        holder.time.setText(RealmUtils.formatDateString(entry));
        holder.title.setText(entry.getBody());
        holder.location.setText("Washington, D.c");

        Glide.with(mActivity)
                .fromString()
                .load(entry.getUriString())
                .animate(AnimUtils.REVEAL)
                .into(holder.image);
    }

    private void bindTextViewHolder(TextViewHolder holder, Entry entry) {
        holder.title.setText(entry.getBody());
        holder.time.setText(RealmUtils.formatDateString(entry));
        holder.location.setText("Washington, D.C.");
    }

    final class TextViewHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.adapter_entry_text_title) TypefaceTextView title;
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
        @Bind(R.id.adapter_entry_image_title) TypefaceTextView title;
        @Bind(R.id.adapter_entry_image_location) TypefaceTextView location;

        public ImageViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
