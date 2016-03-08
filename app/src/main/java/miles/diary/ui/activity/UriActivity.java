package miles.diary.ui.activity;

import android.app.Activity;
import android.app.SharedElementCallback;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.graphics.Palette;
import android.transition.ChangeImageTransform;
import android.transition.ChangeTransform;
import android.transition.Transition;
import android.transition.TransitionSet;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnticipateOvershootInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import butterknife.Bind;
import miles.diary.R;
import miles.diary.ui.PaletteWindows;
import miles.diary.ui.PreDrawer;
import miles.diary.ui.transition.SimpleTransitionListener;
import miles.diary.util.AnimUtils;
import miles.diary.util.FileUtils;
import miles.diary.util.Logg;
import miles.diary.util.ViewUtils;

/**
 * Created by mbpeele on 3/7/16.
 */
public class UriActivity extends BaseActivity {

    @Bind(R.id.activity_uri_toolbar)
    Toolbar toolbar;
    @Bind(R.id.activity_uri_image)
    ImageView imageView;
    @Bind(R.id.activity_uri_button_row)
    LinearLayout buttons;

    private Uri uri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_uri);
        setupTransitions();
        getWindow().getSharedElementEnterTransition().addListener(enterListener);
        getWindow().getSharedElementReturnTransition().addListener(returnListener);

        setActionBar(toolbar);
        getActionBar().setDisplayShowTitleEnabled(false);
        getActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();

        uri = intent.getData();
        postponeEnterTransition();

        Glide.with(this)
                .load(uri)
                .asBitmap()
                .centerCrop()
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
                                .generate(new PaletteWindows(UriActivity.this, resource,
                                        null, Collections.singletonList(toolbar.getNavigationIcon())));
                        return false;
                    }
                })
                .into(imageView);
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
                finishAfterTransition();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setupTransitions() {
        TransitionSet set = new TransitionSet();
        set.addTransition(getWindow().getSharedElementEnterTransition());
        set.addTransition(new ChangeImageTransform());
        set.addTransition(new ChangeTransform());
        set.setOrdering(TransitionSet.ORDERING_TOGETHER);
        getWindow().setSharedElementEnterTransition(set);

//        TransitionSet set1 = new TransitionSet();
//        set.addTransition(getWindow().getSharedElementReturnTransition());
//        set.addTransition(new ChangeImageTransform());
//        set.setOrdering(TransitionSet.ORDERING_TOGETHER);
//        getWindow().setSharedElementReturnTransition(set1);
    }

    private SimpleTransitionListener enterListener = new SimpleTransitionListener() {
        @Override
        public void onTransitionStart(Transition transition) {
            ViewUtils.invisible(buttons);
        }

        @Override
        public void onTransitionEnd(Transition transition) {
            for (int i = 0; i < buttons.getChildCount(); i++) {
                final View view = buttons.getChildAt(i);

                view.setScaleX(0f);
                view.setScaleY(0f);

                view.animate()
                        .scaleX(1f)
                        .scaleY(1f)
                        .setDuration(200)
                        .setStartDelay(50 + 100 * i)
                        .setInterpolator(new AnticipateOvershootInterpolator())
                        .withStartAction(new Runnable() {
                            @Override
                            public void run() {
                                root.bringChildToFront(view);
                                view.setVisibility(View.VISIBLE);
                            }
                        });
            }
        }
    };

    private SimpleTransitionListener returnListener = new SimpleTransitionListener() {
        @Override
        public void onTransitionStart(Transition transition) {
            ViewUtils.gone(buttons);
        }
    };
}
