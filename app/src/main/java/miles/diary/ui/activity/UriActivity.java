package miles.diary.ui.activity;

import android.app.ActionBar;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.graphics.Palette;
import android.transition.Transition;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import butterknife.Bind;
import icepick.State;
import miles.diary.DiaryApplication;
import miles.diary.R;
import miles.diary.ui.PaletteWindows;
import miles.diary.ui.ZoomController;
import miles.diary.ui.transition.ScalingImageTransition;
import miles.diary.ui.transition.SimpleTransitionListener;
import miles.diary.util.FileUtils;
import miles.diary.util.Logg;
import miles.diary.util.UriType;

/**
 * Created by mbpeele on 3/7/16.
 */
public class UriActivity extends BaseActivity {

    @Bind(R.id.activity_uri_toolbar)
    Toolbar toolbar;
    @Bind(R.id.activity_uri_image)
    ImageView imageView;

    @State Uri uri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_uri);
        setupTransitions();

        setActionBar(toolbar);

        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowTitleEnabled(false);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
        }

        new ZoomController(imageView);

        Intent intent = getIntent();

        uri = intent.getData();
        load();
    }

    @Override
    public void inject(DiaryApplication diaryApplication) {
        diaryApplication.getApplicationComponent().inject(this);
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
    }

    private void load() {
        UriType uriType = FileUtils.getUriType(this, uri);
        switch (uriType) {
            case IMAGE:
                loadImage();
                break;
            case GIF:
                loadGif();
                break;
            case VIDEO:
                break;
        }
    }


    private void loadGif() {
        postponeEnterTransition();

        Glide.with(this)
                .load(uri)
                .asBitmap()
                .listener(new RequestListener<Uri, Bitmap>() {
                    @Override
                    public boolean onException(Exception e, Uri model, Target<Bitmap> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(final Bitmap resource, Uri model, Target<Bitmap> target,
                                                   boolean isFromMemoryCache, boolean isFirstResource) {
                        getWindow().getSharedElementEnterTransition().addListener(new SimpleTransitionListener() {
                            @Override
                            public void onTransitionEnd(Transition transition) {
                                Glide.with(UriActivity.this)
                                        .load(uri)
                                        .override(resource.getWidth(), resource.getHeight())
                                        .into(imageView);
                            }
                        });

                        startPostponedEnterTransition();
                        return false;
                    }
                })
                .into(imageView);
    }

    private void loadImage() {
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
                        Palette.from(resource)
                                .maximumColorCount(3)
                                .clearFilters()
                                .generate(new PaletteWindows(UriActivity.this, resource));

                        startPostponedEnterTransition();
                        return false;
                    }
                })
                .into(imageView);
    }
}
