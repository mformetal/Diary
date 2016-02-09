package miles.diary.ui.activity;

import android.animation.ObjectAnimator;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.support.v7.graphics.Palette;
import android.transition.Transition;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import butterknife.Bind;
import miles.diary.R;
import miles.diary.data.model.Entry;
import miles.diary.ui.SimpleTransitionListener;
import miles.diary.ui.widget.CornerImageView;
import miles.diary.ui.widget.TypefaceTextView;
import miles.diary.util.AnimUtils;
import miles.diary.util.Logg;

/**
 * Created by mbpeele on 2/8/16.
 */
public class EntryActivity extends BaseActivity {

    public final static String DATA = "data";

    @Bind(R.id.activity_entry_body) TypefaceTextView body;
    @Bind(R.id.activity_entry_image) CornerImageView imageView;
    @Bind(R.id.activity_entry_time) TypefaceTextView time;

    private Entry entry;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entry);
        setupTransitions();

        entry = realm.where(Entry.class)
                .equalTo(Entry.KEY, getIntent().getStringExtra(DATA))
                .findFirst();

        body.setText(entry.getBody());
        time.setText(Entry.formatDateString(entry));

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
                            Palette.from(resource).generate(palette -> {
//                                Palette.Swatch swatch = palette.getLightVibrantSwatch();
//                                body.setTextColor(swatch.getTitleTextColor());
//                                body.setHighlightColor(swatch.getRgb());
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
