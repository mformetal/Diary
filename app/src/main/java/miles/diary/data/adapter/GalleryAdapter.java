package miles.diary.data.adapter;

import android.app.ActivityOptions;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.CursorAdapter;
import android.support.v7.widget.RecyclerView;
import android.transition.Fade;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.io.File;

import butterknife.Bind;
import butterknife.ButterKnife;
import miles.diary.R;
import miles.diary.data.model.weather.Weather;
import miles.diary.data.model.weather.WeatherResponse;
import miles.diary.data.rx.OkHttpObservable;
import miles.diary.ui.SquareTransformation;
import miles.diary.ui.activity.BaseActivity;
import miles.diary.ui.activity.EntryActivity;
import miles.diary.ui.activity.GalleryActivity;
import miles.diary.ui.activity.HomeActivity;
import miles.diary.ui.activity.UriActivity;
import miles.diary.util.FileUtils;
import miles.diary.util.Logg;
import miles.diary.util.UriType;

/**
 * Created by mbpeele on 3/7/16.
 */
public class GalleryAdapter extends RecyclerView.Adapter<GalleryAdapter.GalleryViewHolder> {

    private final BaseActivity host;
    private final LayoutInflater inflater;
    private Cursor cursor;

    public GalleryAdapter(BaseActivity activity) {
        super();
        host = activity;
        inflater = LayoutInflater.from(activity);
    }

    @Override
    public GalleryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new GalleryViewHolder(inflater.inflate(R.layout.adapter_gallery_layout, parent, false));
    }

    @Override
    public void onBindViewHolder(GalleryViewHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        return cursor != null ? cursor.getCount() : 0;
    }

    public void setCursor(final Cursor cursor) {
        if (cursor != null) {
            this.cursor = cursor;
            notifyDataSetChanged();
        }
    }

    class GalleryViewHolder extends BindingViewHolder<Integer> {

        @Bind(R.id.adapter_gallery_image)
        ImageView imageView;

        public GalleryViewHolder(View itemView) {
            super(itemView);
        }

        @Override
        public void bindViews(View itemView) {
            ButterKnife.bind(this, itemView);
        }

        @Override
        public void bind(final Integer position) {
            if (cursor.moveToPosition(position)) {
                final int index = cursor.getColumnIndex(MediaStore.Images.Media.DATA);
                final String path = cursor.getString(index);

                File file = new File(path);
                if (file.length() != 0) {
                    final Uri uri = Uri.fromFile(file);

                    loadImage(uri);

                    imageView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String name = host.getString(R.string.transition_gallery_to_uri);
                            imageView.setTransitionName(name);

                            Intent intent = new Intent(host, UriActivity.class);
                            ActivityOptions options =
                                    ActivityOptions.makeSceneTransitionAnimation(host,
                                            imageView,
                                            host.getString(R.string.transition_gallery_to_uri));
                            intent.setData(uri);
                            host.startActivityForResult(intent, GalleryActivity.RESULT_SELECT,
                                    options.toBundle());
                        }
                    });
                }
            }
        }

        private void loadImage(Uri uri) {
            Glide.with(host)
                    .load(uri)
                    .error(R.drawable.ic_error_24dp)
                    .into(imageView);
        }
    }
}
