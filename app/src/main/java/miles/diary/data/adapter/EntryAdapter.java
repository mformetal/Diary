package miles.diary.data.adapter;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import miles.diary.R;
import miles.diary.data.RealmUtils;
import miles.diary.data.model.Entry;
import miles.diary.ui.activity.HomeActivity;
import miles.diary.ui.widget.TypefaceTextView;
import miles.diary.util.ColorUtils;
import miles.diary.util.Logg;

/**
 * Created by mbpeele on 1/14/16.
 */
public class EntryAdapter extends RecyclerView.Adapter<EntryAdapter.EntryViewHolder> {

    private HomeActivity activity;
    private LayoutInflater mLayoutInflater;
    private ArrayList<Entry> mDataList;

    public EntryAdapter(HomeActivity context) {
        activity = context;
        mLayoutInflater = LayoutInflater.from(activity);
        mDataList = new ArrayList<>();
    }

    @Override
    public EntryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mLayoutInflater.inflate(R.layout.adapter_entry, parent, false);
        return new EntryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final EntryViewHolder holder, int position) {
        final Entry entry = getItem(position);

        holder.title.setText(entry.getTitle());
        holder.time.setText(RealmUtils.formatDateString(entry));

        Glide.with(activity)
                .load(entry.getBytes())
                .animate(android.R.anim.fade_in)
                .into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return mDataList.size();
    }

    public void addData(Entry entry) {
        if (!mDataList.contains(entry)) {
            mDataList.add(entry);
            notifyItemInserted(mDataList.size());
        }
    }

    public Entry getItem(int position) {
        return mDataList.get(position);
    }

    public Entry removeItem(int position) {
        Entry removed = mDataList.remove(position);
        notifyItemRemoved(position);
        return removed;
    }

    public void clear() {
        mDataList.clear();
        notifyDataSetChanged();
    }

    final class EntryViewHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.adapter_entry_image) ImageView imageView;
        @Bind(R.id.adapter_entry_title) TypefaceTextView title;
        @Bind(R.id.adapter_entry_time) TypefaceTextView time;

        public EntryViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
