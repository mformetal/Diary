package miles.diary.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.graphics.Palette;
import android.transition.ChangeBounds;
import android.transition.ChangeImageTransform;
import android.transition.ChangeTransform;
import android.transition.Transition;
import android.transition.TransitionSet;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnticipateOvershootInterpolator;
import android.widget.ImageView;
import android.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import miles.diary.DiaryApplication;
import miles.diary.R;
import miles.diary.ui.PaletteWindows;
import miles.diary.ui.transition.ScalingImageTransition;
import miles.diary.ui.transition.SimpleTransitionListener;
import miles.diary.util.Logg;
import miles.diary.util.ViewUtils;
import uk.co.senab.photoview.PhotoViewAttacher;

/**
 * Created by mbpeele on 3/7/16.
 */
public class UriActivity extends BaseActivity {

    @Bind(R.id.activity_uri_toolbar)
    Toolbar toolbar;
    @Bind(R.id.activity_uri_image)
    ImageView imageView;

    private Uri uri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_uri);
        setupTransitions();

        setActionBar(toolbar);
        toolbar.inflateMenu(R.menu.menu_uri);
        getActionBar().setDisplayShowTitleEnabled(false);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setDisplayShowHomeEnabled(true);

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
                        startPostponedEnterTransition();

                        Palette.from(resource)
                                .maximumColorCount(3)
                                .clearFilters()
                                .generate(new PaletteWindows(UriActivity.this, resource));
                        return false;
                    }
                })
                .into(imageView);
    }

    @Override
    public void inject(DiaryApplication diaryApplication) {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_uri, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_uri_confirm:
                Intent intent = new Intent();
                intent.setData(uri);
                setResult(RESULT_OK, intent);
                finish();
                break;
            case android.R.id.home:
                finishAfterTransition();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setupTransitions() {
        getWindow().setSharedElementEnterTransition(new ScalingImageTransition());
        getWindow().setSharedElementReturnTransition(new ScalingImageTransition());

        getWindow().getSharedElementEnterTransition().addListener(new SimpleTransitionListener() {
            @Override
            public void onTransitionEnd(Transition transition) {
                ViewUtils.setZoomControls(imageView);
            }
        });
    }
}
