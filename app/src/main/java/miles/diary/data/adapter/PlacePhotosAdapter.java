package miles.diary.data.adapter;

import android.graphics.Bitmap;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.google.android.gms.location.places.PlacePhotoMetadata;
import com.google.android.gms.location.places.PlacePhotoMetadataBuffer;
import com.google.android.gms.location.places.PlacePhotoResult;

import miles.diary.R;
import miles.diary.data.api.Google;
import miles.diary.data.rx.ActivitySubscriber;
import miles.diary.ui.PreDrawer;
import miles.diary.ui.activity.BaseActivity;
import miles.diary.util.AnimUtils;

/**
 * Created by mbpeele on 3/6/16.
 */
public class PlacePhotosAdapter extends PagerAdapter {

    private PlacePhotoMetadataBuffer buffer;
    private final BaseActivity activity;
    private final Google service;
    private final LayoutInflater inflater;

    public PlacePhotosAdapter(Google googleService, BaseActivity baseActivity) {
        super();
        service = googleService;
        activity = baseActivity;
        inflater = LayoutInflater.from(baseActivity);
    }

    @Override
    public int getItemPosition(Object object) {
        if (buffer != null) {
            return super.getItemPosition(object);
        } else {
            return POSITION_NONE;
        }
    }

    @Override
    public Object instantiateItem(ViewGroup collection, int position) {
        final FrameLayout frameLayout =
                (FrameLayout) inflater.inflate(R.layout.adapter_place_photo_layout, collection, false);
        collection.addView(frameLayout);

        final ImageView imageView = (ImageView) frameLayout.findViewById(R.id.adapter_place_photo_image);
        final ProgressBar progressBar = (ProgressBar) frameLayout.findViewById(R.id.adapter_place_photo_progress);

        if (buffer != null) {
            final PlacePhotoMetadata metadata = buffer.get(position);
            final int width = imageView.getWidth();
            final int height = imageView.getHeight();

            if (width == 0  || height == 0) {
                PreDrawer.addPreDrawer(imageView, new PreDrawer.OnPreDrawListener<ImageView>() {
                    @Override
                    public boolean onPreDraw(final ImageView view) {
                        service.getScaledPhoto(metadata, view.getWidth(), view.getHeight())
                                .subscribe(new ActivitySubscriber<PlacePhotoResult>(activity) {
                                    @Override
                                    public void onNext(PlacePhotoResult placePhotoResult) {
                                        Bitmap bitmap = placePhotoResult.getBitmap();
                                        view.setImageBitmap(bitmap);

                                        AnimUtils.pop(view, 0f, 1f).start();
                                        AnimUtils.gone(progressBar).start();
                                    }
                                });
                        return true;
                    }
                });
            } else {
                service.getScaledPhoto(metadata, width, height)
                        .subscribe(new ActivitySubscriber<PlacePhotoResult>(activity) {
                            @Override
                            public void onNext(PlacePhotoResult placePhotoResult) {
                                Bitmap bitmap = placePhotoResult.getBitmap();
                                imageView.setImageBitmap(bitmap);

                                AnimUtils.pop(imageView, 0f, 1f).start();
                                AnimUtils.gone(progressBar).start();
                            }
                        });
            }
        }

        return frameLayout;
    }

    @Override
    public int getCount() {
        return buffer != null ? buffer.getCount() : 1;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(ViewGroup collection, int position, Object view) {
        collection.removeView((View) view);
    }

    public void setBuffer(PlacePhotoMetadataBuffer placePhotoMetadataBuffer) {
        buffer = placePhotoMetadataBuffer;
        notifyDataSetChanged();
    }

    public void release() {
        if (buffer != null) {
            buffer.release();
        }
    }
}
