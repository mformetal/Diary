package miles.diary.ui.activity;

import android.animation.ObjectAnimator;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.support.v7.graphics.Palette;
import android.transition.Transition;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import butterknife.Bind;
import miles.diary.R;
import miles.diary.data.model.Entry;
import miles.diary.ui.PreDrawer;
import miles.diary.ui.SimpleTransitionListener;
import miles.diary.ui.widget.CornerImageView;
import miles.diary.util.AnimUtils;
import miles.diary.util.Logg;
import miles.diary.util.ViewUtils;

/**
 * Created by mbpeele on 2/8/16.
 */
public class EntryActivity extends BaseActivity {

    public final static String DATA = "data";

    @Bind(R.id.activity_entry_body) TextView textView;
    @Bind(R.id.activity_entry_image) CornerImageView imageView;
    @Bind(R.id.activity_entry_toolbar) Toolbar toolbar;

    private Entry entry;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entry);
        setupTransitions();

        entry = realm.where(Entry.class)
                .equalTo(Entry.KEY, getIntent().getStringExtra(DATA))
                .findFirst();

        setActionBar(toolbar);
        getActionBar().setTitle(Entry.formatDateString(entry));

        textView.setText(entry.getBody());

        if (entry.getUri() != null) {
            Glide.with(EntryActivity.this)
                    .fromString()
                    .asBitmap()
                    .load(entry.getUri())
                    .dontAnimate()
                    .listener(new RequestListener<String, Bitmap>() {
                        @Override
                        public boolean onException(Exception e, String model, Target<Bitmap> target, boolean isFirstResource) {
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Bitmap resource, String model, Target<Bitmap> target,
                                                       boolean isFromMemoryCache, boolean isFirstResource) {
                            Palette.from(resource).generate(new Palette.PaletteAsyncListener() {
                                @Override
                                public void onGenerated(Palette palette) {
                                    Palette.Swatch swatch = palette.getVibrantSwatch();
                                    textView.setTextColor(swatch.getTitleTextColor());
                                    textView.setHighlightColor(swatch.getRgb());
                                }
                            });
                            return false;
                        }
                    })
                    .into(imageView);
        }
    }

    private void setupTransitions() {
        Transition enterTransition = getWindow().getSharedElementEnterTransition();
        Transition returnTransition = getWindow().getSharedElementReturnTransition();

        if (enterTransition != null && returnTransition != null) {
            enterTransition.addListener(new SimpleTransitionListener() {
                @Override
                public void onTransitionStart(Transition transition) {
                    super.onTransitionStart(transition);
                    ObjectAnimator corner = ObjectAnimator.ofFloat(imageView,
                            CornerImageView.CORNERS,
                            Math.max(imageView.getWidth(), imageView.getHeight()) / 2f, 0);
                    corner.setDuration(AnimUtils.longAnim(getApplicationContext()));
                    corner.setInterpolator(new FastOutSlowInInterpolator());
                    corner.start();
                }
            });

            returnTransition.addListener(new SimpleTransitionListener() {
                @Override
                public void onTransitionStart(Transition transition) {
                    super.onTransitionEnd(transition);

                    ObjectAnimator corner = ObjectAnimator.ofFloat(imageView,
                            CornerImageView.CORNERS,
                            0, Math.min(imageView.getWidth(), imageView.getHeight()) / 2f);
                    corner.setDuration(AnimUtils.longAnim(getApplicationContext()));
                    corner.setInterpolator(new FastOutSlowInInterpolator());
                    corner.start();
                }
            });
        }
    }
}
