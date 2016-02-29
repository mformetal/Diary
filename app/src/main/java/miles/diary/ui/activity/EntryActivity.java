package miles.diary.ui.activity;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.support.v7.graphics.Palette;
import android.text.Layout;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.AlignmentSpan;
import android.transition.Transition;
import android.view.View;
import android.view.animation.Interpolator;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.gson.Gson;

import java.util.ArrayList;

import butterknife.Bind;
import miles.diary.R;
import miles.diary.data.ActivitySubscriber;
import miles.diary.data.model.Entry;
import miles.diary.data.model.weather.WeatherResponse;
import miles.diary.ui.transition.SimpleTransitionListener;
import miles.diary.ui.widget.CornerImageView;
import miles.diary.ui.widget.NotebookTextView;
import miles.diary.ui.widget.TypefaceIconTextView;
import miles.diary.ui.widget.TypefaceTextView;
import miles.diary.util.AnimUtils;
import miles.diary.util.ColorsUtils;
import miles.diary.util.GoogleUtils;
import miles.diary.util.IntentUtils;
import miles.diary.util.Logg;
import miles.diary.util.ViewUtils;

/**
 * Created by mbpeele on 2/8/16.
 */
public class EntryActivity extends BaseActivity
        implements GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks,
        View.OnClickListener {

    public final static String DATA = "data";
    public static void newIntent(Context context, View view, Entry entry) {
        Intent intent = new Intent(context, EntryActivity.class);
        intent.putExtra(DATA, entry.getBody());
        ActivityOptions options =
                ActivityOptions.makeSceneTransitionAnimation((Activity) context, view,
                        context.getString(R.string.transition_location_image));
        context.startActivity(intent, options.toBundle());
    }

    @Bind(R.id.activity_entry_root)
    CoordinatorLayout layout;
    @Bind(R.id.activity_entry_body)
    NotebookTextView entryBody;
    @Bind(R.id.activity_entry_image)
    CornerImageView entryImage;
    @Bind(R.id.activity_entry_place)
    TypefaceTextView entryPlace;
    @Bind(R.id.activity_entry_date)
    TypefaceTextView entryDate;
    @Bind(R.id.activity_entry_weather)
    TypefaceIconTextView entryWeather;

    private Entry entry;
    private GoogleApiClient googleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupTransitions();
        getWindow().getDecorView().setBackgroundColor(Color.WHITE);
        setContentView(R.layout.activity_entry);

        entry = realm.where(Entry.class)
                .equalTo(Entry.KEY, getIntent().getStringExtra(DATA))
                .findFirst();

        googleApiClient = googleApiClientBuilder
                .enableAutoManage(this, 0, this)
                .addConnectionCallbacks(this)
                .build();

        findViewById(R.id.activity_entry_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        int color = ContextCompat.getColor(this, R.color.accent);
        ViewUtils.mutate(entryPlace.getCompoundDrawables(), color);
        ViewUtils.mutate(entryDate.getCompoundDrawables(), color);
        entryBody.setText(entry.getBody());
        entryPlace.setText(entry.getPlaceName());
        entryDate.setText(Entry.formatDateString(entry));

        WeatherResponse weatherResponse = new Gson().fromJson(entry.getWeather(), WeatherResponse.class);
        String[] parts = weatherResponse.getTemperatureParts();
        String temperature = parts[0] + "\n" + parts[1];
        entryWeather.setText(temperature);

        if (entry.getUri() != null) {
            postponeEnterTransition();
            Glide.with(EntryActivity.this)
                    .fromString()
                    .asBitmap()
                    .load(entry.getUri())
                    .dontAnimate()
                    .centerCrop()
                    .listener(new RequestListener<String, Bitmap>() {
                        @Override
                        public boolean onException(Exception e, String model, Target<Bitmap> target, boolean isFirstResource) {
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(final Bitmap resource, String model, Target<Bitmap> target,
                                                       boolean isFromMemoryCache, boolean isFirstResource) {
                            Palette.from(resource)
                                    .maximumColorCount(3)
                                    .clearFilters()
                                    .generate(new Palette.PaletteAsyncListener() {
                                        @Override
                                        public void onGenerated(Palette palette) {
                                            boolean isDark;
                                            @ColorsUtils.Lightness int lightness = ColorsUtils.isDark(palette);
                                            if (lightness == ColorsUtils.LIGHTNESS_UNKNOWN) {
                                                isDark = ColorsUtils.isDark(resource,
                                                        resource.getWidth() / 2, 0);
                                            } else {
                                                isDark = lightness == ColorsUtils.IS_DARK;
                                            }

                                            int statusBarColor = getWindow().getStatusBarColor();
                                            Palette.Swatch topColor = ColorsUtils.getMostPopulousSwatch(palette);
                                            if (topColor != null &&
                                                    (isDark || Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)) {
                                                statusBarColor = ColorsUtils.scrimify(topColor.getRgb(),
                                                        isDark, .075f);

                                                if (!isDark && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                    ViewUtils.setLightStatusBar(entryImage);
                                                }
                                            }

                                            if (statusBarColor != getWindow().getStatusBarColor()) {
                                                ValueAnimator statusBarColorAnim = ValueAnimator.ofArgb(getWindow
                                                        ().getStatusBarColor(), statusBarColor);
                                                statusBarColorAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                                                    @Override
                                                    public void onAnimationUpdate(ValueAnimator animation) {
                                                        getWindow().setStatusBarColor(
                                                                (int) animation.getAnimatedValue());
                                                    }
                                                });
                                                statusBarColorAnim.setDuration(1000);
                                                statusBarColorAnim.setInterpolator(new FastOutSlowInInterpolator());
                                                statusBarColorAnim.start();
                                            }
                                        }
                                    });

                            startPostponedEnterTransition();
                            return false;
                        }
                    })
                    .into(entryImage);
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        if (connectionResult.hasResolution()) {
            try {
                connectionResult.startResolutionForResult(this, IntentUtils.GOOGLE_API_CLIENT_FAILED_CODE);
            } catch (IntentSender.SendIntentException e) {
                e.printStackTrace();
            }
        } else {
            Logg.log("CONNECTION FAILED WITH CODE: " + connectionResult.getErrorCode());
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        if (hasConnection()) {
            if (!realm.isClosed()) {
                String placeId = entry.getPlaceId();
                if (placeId != null) {
//                GoogleUtils.getPlaceById(googleApiClient, placeId)
//                        .subscribe(new ActivitySubscriber<PlaceBuffer>(this) {
//                            @Override
//                            public void onNext(PlaceBuffer places) {
//                                super.onNext(places);
//                                places.release();
//                            }
//                        });
                }
            }
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    private void setupTransitions() {
        final Transition enterTransition = getWindow().getSharedElementEnterTransition();
        final Transition returnTransition = getWindow().getSharedElementReturnTransition();

        if (enterTransition != null && returnTransition != null) {
            enterTransition.addListener(new SimpleTransitionListener() {
                @Override
                public void onTransitionStart(Transition transition) {
                    super.onTransitionStart(transition);
                    enterTransition.removeListener(this);

                    returnTransition.addListener(new SimpleTransitionListener() {
                        @Override
                        public void onTransitionStart(Transition transition) {
                            super.onTransitionEnd(transition);

                            ObjectAnimator corner = ObjectAnimator.ofFloat(entryImage,
                                    CornerImageView.CORNERS,
                                    0, Math.min(entryImage.getWidth(), entryImage.getHeight()) / 2f);
                            corner.setDuration(AnimUtils.longAnim(getApplicationContext()));
                            corner.setInterpolator(new FastOutSlowInInterpolator());
                            corner.start();
                        }
                    });

                    ArrayList<Animator> animators = new ArrayList<Animator>();

                    float offset = layout.getHeight() / 4;
                    int duration = AnimUtils.mediumAnim(EntryActivity.this);
                    int delay = 100, delayInc = 25;
                    Interpolator interpolator = new FastOutSlowInInterpolator();
                    for (int i = 0; i < layout.getChildCount(); i++) {
                        View v = root.getChildAt(i);
                        if (!(v instanceof AppBarLayout)) {
                            v.setTranslationY(offset);
                            v.setAlpha(0f);

                            ObjectAnimator objectAnimator = ObjectAnimator.ofPropertyValuesHolder(v,
                                    PropertyValuesHolder.ofFloat(View.TRANSLATION_Y, 0f),
                                    PropertyValuesHolder.ofFloat(View.ALPHA, 1f));
                            objectAnimator.setDuration(duration);
                            objectAnimator.setInterpolator(interpolator);
                            objectAnimator.setStartDelay(delay);

                            animators.add(objectAnimator);

                            delay += delayInc;
                            offset *= 1.8f;
                        }
                    }

                    animators.add(AnimUtils.alpha(findViewById(R.id.activity_entry_back), 0f, 1f));

                    ObjectAnimator corner = ObjectAnimator.ofFloat(entryImage,
                            CornerImageView.CORNERS,
                            Math.max(entryImage.getWidth(), entryImage.getHeight()) / 2f, 0);
                    corner.setDuration(AnimUtils.longAnim(EntryActivity.this));
                    corner.setInterpolator(interpolator);

                    animators.add(corner);

                    AnimatorSet animatorSet = new AnimatorSet();
                    animatorSet.playTogether(animators);
                    animatorSet.start();
                }
            });
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.activity_entry_fab_edit_entry:
                break;
            case R.id.activity_entry_fab_favorite_entry:
                break;
            case R.id.activity_entry_fab_delete_entry:
                break;
        }
    }
}
