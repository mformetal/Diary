package miles.diary.ui.activity;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.support.v7.graphics.Palette;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.RelativeSizeSpan;
import android.text.style.TypefaceSpan;
import android.transition.ArcMotion;
import android.transition.Transition;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.gson.Gson;

import butterknife.Bind;
import miles.diary.R;
import miles.diary.data.model.Entry;
import miles.diary.data.model.weather.WeatherResponse;
import miles.diary.ui.TypefacerSpan;
import miles.diary.ui.transition.CornerTransition;
import miles.diary.ui.transition.SimpleTransitionListener;
import miles.diary.ui.widget.CornerImageView;
import miles.diary.ui.widget.TypefaceIconTextView;
import miles.diary.ui.widget.TypefaceTextView;
import miles.diary.util.AnimUtils;
import miles.diary.util.ColorsUtils;
import miles.diary.util.IntentUtils;
import miles.diary.util.Logg;
import miles.diary.util.TextUtils;
import miles.diary.util.ViewUtils;

/**
 * Created by mbpeele on 2/8/16.
 */
public class EntryActivity extends TransitionActivity {

    public final static String DATA = "data";
    public static void newIntent(Context context, View view, Entry entry) {
        Intent intent = new Intent(context, EntryActivity.class);
        intent.putExtra(DATA, entry.getBody());
        ActivityOptions options =
                ActivityOptions.makeSceneTransitionAnimation((Activity) context, view,
                        context.getString(R.string.transition_location_image));
        context.startActivity(intent, options.toBundle());
    }

    @Bind(R.id.activity_entry_menu_show)
    FloatingActionButton menuFab;
    @Bind(R.id.activity_entry_back)
    ImageButton backButton;
    @Bind(R.id.activity_entry_body)
    TypefaceTextView entryBody;
    @Bind(R.id.activity_entry_image)
    CornerImageView entryImage;
    @Bind(R.id.activity_entry_place)
    TypefaceTextView entryPlace;
    @Bind(R.id.activity_entry_date)
    TypefaceTextView entryDate;
    @Bind(R.id.activity_entry_weather)
    TypefaceIconTextView entryWeather;

    private Entry entry;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entry);
        setupTransitions();

        entry = realm.where(Entry.class)
                .equalTo(Entry.KEY, getIntent().getStringExtra(DATA))
                .findFirst();

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finishAfterTransition();
            }
        });

        ViewUtils.mutate(entryPlace, entryPlace.getCurrentTextColor());
        ViewUtils.mutate(entryDate, entryPlace.getCurrentTextColor());

        SpannableString spannableString = new SpannableString(Entry.formatDiaryPrefaceText(entry));
        spannableString.setSpan(new RelativeSizeSpan(1.4f), 0, 12, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        spannableString.setSpan(new TypefacerSpan(TextUtils.getFont(this, getString(R.string.light_italic))),
                12, spannableString.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        entryBody.setText(spannableString, TextView.BufferType.SPANNABLE);

        entryPlace.setText(entry.getPlaceName());

        entryDate.setText(TextUtils.formatDate(entry.getDate()) + TextUtils.LINE_SEPERATOR +
                TextUtils.formatTime(entry.getDate()));

        WeatherResponse weatherResponse = new Gson().fromJson(entry.getWeather(), WeatherResponse.class);
        entryWeather.setText(weatherResponse.getOneLineTemperatureString());

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
                            startPostponedEnterTransition();

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
                            return false;
                        }
                    })
                    .into(entryImage);
        }
    }

    @Override
    boolean shouldRunCustomExitAnimation() {
        return false;
    }

    @Override
    void onEnter(ViewGroup root, Intent calledIntent, boolean hasSavedInstanceState) {
        ArcMotion arcMotion = new ArcMotion();
        arcMotion.setMinimumHorizontalAngle(50f);
        arcMotion.setMinimumVerticalAngle(50f);

        CornerTransition reveal = new CornerTransition(
                Math.max(entryImage.getWidth(), entryImage.getHeight()) / 2f, 0);
        reveal.addTarget(entryImage);
        reveal.setPathMotion(arcMotion);

        reveal.addListener(new SimpleTransitionListener() {
            @Override
            public void onTransitionStart(Transition transition) {
                super.onTransitionStart(transition);
                backButton.setVisibility(View.GONE);
                menuFab.setVisibility(View.GONE);
            }

            @Override
            public void onTransitionEnd(Transition transition) {
                super.onTransitionEnd(transition);
                int duration = 150;

                backButton.setVisibility(View.VISIBLE);
                AnimUtils.pop(backButton, 0f, 1f)
                        .setDuration(duration)
                        .start();

                menuFab.setVisibility(View.VISIBLE);
                AnimUtils.pop(menuFab, 0f, 1f)
                        .setDuration(duration)
                        .start();
            }
        });

        CornerTransition unreveal = new CornerTransition(
                0, Math.min(entryImage.getWidth(), entryImage.getHeight()) / 2f);
        unreveal.addTarget(entryImage);
        unreveal.setPathMotion(arcMotion);

        unreveal.addListener(new SimpleTransitionListener() {
            @Override
            public void onTransitionStart(Transition transition) {
                super.onTransitionStart(transition);

                int duration = 150;

                AnimUtils.pop(backButton, 1f, 0f)
                        .setDuration(duration)
                        .start();

                AnimUtils.pop(menuFab, 1f, 0f)
                        .setDuration(duration)
                        .start();
            }
        });

        getWindow().setSharedElementEnterTransition(reveal);
        getWindow().setSharedElementReturnTransition(unreveal);
    }

    @Override
    void onExit(ViewGroup root) {
    }

    private void setupTransitions() {
//        PreDrawer.addPreDrawer(entryImage, new PreDrawer.OnPreDrawListener<CornerImageView>() {
//            @Override
//            public boolean onPreDraw(CornerImageView view) {
////                CornerTransition reveal = new CornerTransition(
////                        Math.max(view.getWidth(), view.getHeight()) / 2f, 0);
////                reveal.addTarget(view);
////
////                CornerTransition unreveal = new CornerTransition(
////                        0, Math.min(view.getWidth(), view.getHeight()) / 2f);
////                unreveal.addTarget(view);
////
////                getWindow().setSharedElementEnterTransition(reveal);
////                getWindow().setSharedElementReturnTransition(unreveal);
//
//                return true;
//            }
//        });
    }

//    @Override
//    public void onClick(View v) {
//        switch (v.getId()) {
//            case R.id.activity_entry_fab_edit_entry:
//                break;
//            case R.id.activity_entry_fab_favorite_entry:
//                break;
//            case R.id.activity_entry_fab_delete_entry:
//                break;
//        }
//    }
}
