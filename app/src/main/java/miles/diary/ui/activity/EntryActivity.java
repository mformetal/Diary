package miles.diary.ui.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.graphics.Palette;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.RelativeSizeSpan;
import android.transition.ArcMotion;
import android.transition.Transition;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.gson.Gson;

import butterknife.Bind;
import miles.diary.R;
import miles.diary.data.model.Entry;
import miles.diary.data.model.weather.WeatherResponse;
import miles.diary.ui.PaletteWindows;
import miles.diary.ui.TypefacerSpan;
import miles.diary.ui.transition.RoundedImageViewTransition;
import miles.diary.ui.transition.SimpleTransitionListener;
import miles.diary.ui.widget.RoundedImageView;
import miles.diary.ui.widget.TypefaceIconTextView;
import miles.diary.ui.widget.TypefaceTextView;
import miles.diary.util.AnimUtils;
import miles.diary.util.TextUtils;
import miles.diary.util.ViewUtils;

/**
 * Created by mbpeele on 2/8/16.
 */
public class EntryActivity extends TransitionActivity implements View.OnClickListener {

    public final static String DATA = "data";
    public static void newIntent(Context context, View view, Entry entry) {
        Intent intent = new Intent(context, EntryActivity.class);
        intent.putExtra(DATA, entry.getBody());
        ActivityOptions options =
                ActivityOptions.makeSceneTransitionAnimation((Activity) context, view,
                        context.getString(R.string.transition_location_image));
        context.startActivity(intent, options.toBundle());
    }

    @Bind(R.id.activity_entry_back)
    ImageButton backButton;
    @Bind(R.id.activity_entry_body)
    TypefaceTextView body;
    @Bind(R.id.activity_entry_image)
    RoundedImageView image;
    @Bind(R.id.activity_entry_place)
    TypefaceTextView place;
    @Bind(R.id.activity_entry_date)
    TypefaceTextView date;
    @Bind(R.id.activity_entry_weather)
    TypefaceIconTextView weather;

    private Entry entry;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entry);

        entry = dataManager.getObject(Entry.class, getIntent().getStringExtra(DATA));

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finishAfterTransition();
            }
        });

        ViewUtils.mutate(place, place.getCurrentTextColor());
        ViewUtils.mutate(date, place.getCurrentTextColor());

        SpannableString spannableString = new SpannableString(Entry.formatDiaryPrefaceText(entry));
        spannableString.setSpan(new RelativeSizeSpan(1.4f), 0, 12, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        spannableString.setSpan(new TypefacerSpan(TextUtils.getFont(this, getString(R.string.default_font))),
                12, spannableString.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        body.setText(spannableString, TextView.BufferType.SPANNABLE);

        place.setText(entry.getPlaceName());

        date.setText(TextUtils.formatDate(entry.getDate()) + TextUtils.LINE_SEPERATOR +
                TextUtils.formatTime(entry.getDate()));

        WeatherResponse weatherResponse = new Gson().fromJson(entry.getWeather(), WeatherResponse.class);
        weather.setText(weatherResponse.getOneLineTemperatureString());

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
                                    .generate(new PaletteWindows(EntryActivity.this, resource));
                            return false;
                        }
                    })
                    .into(image);
        }
    }

    @Override
    boolean shouldRunCustomExitAnimation() {
        return false;
    }

    @Override
    void onEnter(final ViewGroup root, Intent calledIntent, boolean hasSavedInstanceState) {
        ArcMotion arcMotion = new ArcMotion();
        arcMotion.setMinimumHorizontalAngle(50f);
        arcMotion.setMinimumVerticalAngle(50f);

        final int duration = AnimUtils.shortAnim(this);

        RoundedImageViewTransition reveal = new RoundedImageViewTransition(
                Math.max(image.getWidth(), image.getHeight()) / 2f, 0);
        reveal.addTarget(image);
        reveal.setPathMotion(arcMotion);

        reveal.addListener(new SimpleTransitionListener() {
            @Override
            public void onTransitionStart(Transition transition) {
                LinearLayout linearLayout = (LinearLayout) ((ViewGroup) root.getChildAt(1)).getChildAt(0);
                final float offset = root.getHeight() / 4f;
                for (int i = 0; i < linearLayout.getChildCount(); i++) {
                    View v = linearLayout.getChildAt(i);

                    v.setTranslationY(offset);
                    v.setAlpha(0f);

                    v.animate()
                            .translationY(0f)
                            .alpha(1f)
                            .setDuration(duration)
                            .setInterpolator(new DecelerateInterpolator())
                            .setStartDelay(50 + 50 * i)
                            .start();
                }
            }

            @Override
            public void onTransitionEnd(Transition transition) {
                backButton.setVisibility(View.VISIBLE);
                AnimUtils.pop(backButton, 0f, 1f)
                        .setDuration(duration)
                        .start();
            }
        });

        RoundedImageViewTransition unreveal = new RoundedImageViewTransition(
                0, Math.min(image.getWidth(), image.getHeight()) / 2f);
        unreveal.addTarget(image);
        unreveal.setPathMotion(arcMotion);

        unreveal.addListener(new SimpleTransitionListener() {
            @Override
            public void onTransitionStart(Transition transition) {
                super.onTransitionStart(transition);

                AnimUtils.pop(backButton, 1f, 0f)
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

    @Override
    public void onClick(View v) {
    }
}
