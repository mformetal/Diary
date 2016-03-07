package miles.diary.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import butterknife.Bind;
import miles.diary.R;
import miles.diary.ui.PreDrawer;
import miles.diary.util.FileUtils;
import miles.diary.util.Logg;

/**
 * Created by mbpeele on 3/7/16.
 */
public class UriActivity extends BaseActivity {

    @Bind(R.id.activity_uri_image)
    ImageView imageView;

    private Uri uri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_uri);

        Intent intent = getIntent();

        uri = intent.getData();
        postponeEnterTransition();

        Glide.with(this)
                .load(uri)
                .asBitmap()
                .listener(new RequestListener<Uri, Bitmap>() {
                    @Override
                    public boolean onException(Exception e, Uri model, Target<Bitmap> target, boolean isFirstResource) {
                        Logg.log(e);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Bitmap resource, Uri model, Target<Bitmap> target,
                                                   boolean isFromMemoryCache, boolean isFirstResource) {
                        Logg.log(resource.getWidth(), resource.getHeight());
                        startPostponedEnterTransition();
                        return false;
                    }
                })
                .into(imageView);
    }
}
