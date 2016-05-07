package miles.diary.ui.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.graphics.Palette;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.RelativeSizeSpan;
import android.transition.ArcMotion;
import android.transition.ChangeImageTransform;
import android.transition.Transition;
import android.transition.TransitionSet;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.TextView;
import android.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.gson.Gson;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.OnClick;
import icepick.State;
import miles.diary.DiaryApplication;
import miles.diary.R;
import miles.diary.data.api.Repository;
import miles.diary.data.model.realm.Entry;
import miles.diary.data.model.weather.WeatherResponse;
import miles.diary.data.rx.ActivitySubscriber;
import miles.diary.data.rx.DataTransaction;
import miles.diary.ui.PaletteWindows;
import miles.diary.ui.TypefacerSpan;
import miles.diary.ui.transition.RoundedImageViewTransition;
import miles.diary.ui.transition.SimpleTransitionListener;
import miles.diary.ui.widget.RoundedImageView;
import miles.diary.ui.widget.TypefaceIconTextView;
import miles.diary.ui.widget.TypefaceTextView;
import miles.diary.util.AnimUtils;
import miles.diary.util.Logg;
import miles.diary.util.TextUtils;
import miles.diary.util.ViewUtils;

/**
 * Created by mbpeele on 2/8/16.
 */
public class EntryActivity extends TransitionActivity {

    public final static String INTENT_KEY = "data";
    public final static String INTENT_ACTION = "action";
    public final static int REQUEST_EDIT_ENTRY = 1;

    public enum Action {
        EDIT,
        DELETE
    }

    @Inject
    Repository repository;

    @Bind(R.id.activity_entry_place_photos)
    FloatingActionButton photosFab;
    @Bind(R.id.activity_entry_toolbar)
    Toolbar toolbar;
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
    @State boolean dataChanged;

    public static Intent newIntent(Context context, Entry entry) {
        Intent intent = new Intent(context, EntryActivity.class);
        intent.putExtra(EntryActivity.INTENT_KEY, entry.getDateMillis());
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entry);

        setActionBar(toolbar);
        if (getActionBar() != null) {
            getActionBar().setDisplayHomeAsUpEnabled(true);
        }

        entry = repository.get(Entry.class, getIntent().getLongExtra(INTENT_KEY, -1));
        updateView(entry);
    }

    @Override
    public void inject(DiaryApplication diaryApplication) {
        diaryApplication.getContextComponent().inject(this);
    }

    @Override
    public void onBackPressed() {
        if (dataChanged) {
            Logg.log("DATA CHANGED");
            setResultAction(Action.EDIT);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    boolean overrideTransitions() {
        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_entry, menu);
        for (int i = 0; i < menu.size(); i++) {
            MenuItem item = menu.getItem(i);
            if (entry.getUri() == null) {
                item.getIcon().mutate().setColorFilter(Color.BLACK, PorterDuff.Mode.SRC_IN);
            }
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_entry_edit:
                Intent intent = new Intent(this, NewEntryActivity.class);
                intent.putExtra(EntryActivity.INTENT_KEY, entry.getDateMillis());
                startActivityForResult(intent, REQUEST_EDIT_ENTRY);
                break;
            case R.id.menu_entry_delete:
                setResultAction(Action.DELETE);
                break;
            case android.R.id.home:
                finishAfterTransition();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    @SuppressWarnings("ConstantConditions")
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_EDIT_ENTRY:
                if (resultCode == RESULT_OK) {
                    Bundle bundle = data.getExtras();
                    final String body = bundle.getString(NewEntryActivity.BODY);
                    final Uri uri = bundle.getParcelable(NewEntryActivity.URI);
                    final String placeName = bundle.getString(NewEntryActivity.PLACE_NAME);
                    final String placeId = bundle.getString(NewEntryActivity.PLACE_ID);

                    repository.updateObject(new DataTransaction<Entry>() {
                        @Override
                        public Entry call() {
                            return entry.update(body, uri, placeName, placeId);
                        }
                    }).subscribe(new ActivitySubscriber<Entry>(this) {
                        @Override
                        public void onNext(Entry entry1) {
                            dataChanged = true;
                            entry = entry1;
                            updateView(entry);
                        }
                    });
                }
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    void onEnter(final ViewGroup root, Intent calledIntent, boolean hasSavedInstanceState) {
        if (entry.getUri() == null) {
            int color = ContextCompat.getColor(this, R.color.dark_icons);
            toolbar.getNavigationIcon().mutate().setColorFilter(color, PorterDuff.Mode.SRC_IN);
            toolbar.setTitleTextColor(color);
        } else {
            ArcMotion arcMotion = new ArcMotion();
            arcMotion.setMinimumHorizontalAngle(50f);
            arcMotion.setMinimumVerticalAngle(50f);

            RoundedImageViewTransition reveal = new RoundedImageViewTransition(
                    Math.max(image.getWidth(), image.getHeight()) / 2f, 0);
            reveal.addTarget(image);
            reveal.setPathMotion(arcMotion);

            reveal.addListener(new SimpleTransitionListener() {
                @Override
                public void onTransitionStart(Transition transition) {
                    toolbar.setVisibility(View.GONE);
                    slideUpView(root, AnimUtils.shortAnim(EntryActivity.this));
                }

                @Override
                public void onTransitionEnd(Transition transition) {
                    AnimUtils.visible(toolbar).start();
                    ViewUtils.setZoomControls(image);
                }
            });

            TransitionSet returnSet = new TransitionSet();

            RoundedImageViewTransition unreveal = new RoundedImageViewTransition(
                    0, Math.min(image.getWidth(), image.getHeight()) / 2f);
            unreveal.addTarget(image);
            unreveal.setPathMotion(arcMotion);
            unreveal.addListener(new SimpleTransitionListener() {
                @Override
                public void onTransitionStart(Transition transition) {
                    toolbar.setVisibility(View.GONE);
                }
            });

            returnSet.addTransition(unreveal);
            returnSet.addTransition(new ChangeImageTransform());

            getWindow().setSharedElementEnterTransition(reveal);
            getWindow().setSharedElementReturnTransition(returnSet);
        }
    }

    @Override
    void onExit(ViewGroup root) {
    }

    @SuppressLint("SetTextI18n")
    private void updateView(Entry entry) {
        ViewUtils.mutate(place, place.getCurrentTextColor());
        ViewUtils.mutate(date, place.getCurrentTextColor());

        String text = "Dear Diary, " +
                TextUtils.repeat(2, TextUtils.LINE_SEPERATOR) +
                TextUtils.repeat(5, TextUtils.TAB) +
                entry.getBody();

        SpannableString spannableString = new SpannableString(text);
        spannableString.setSpan(new RelativeSizeSpan(1.4f), 0, 12, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        spannableString.setSpan(new TypefacerSpan(TextUtils.getFont(this, getString(R.string.default_font))),
                12, spannableString.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        body.setText(spannableString, TextView.BufferType.SPANNABLE);

        String placeName = entry.getPlaceName();
        if (placeName != null) {
            place.setVisibility(View.VISIBLE);
            place.setText(placeName);
        } else {
            photosFab.setVisibility(View.GONE);
            place.setVisibility(View.GONE);
        }

        date.setText(TextUtils.formatDate(entry.getDate()) + TextUtils.LINE_SEPERATOR +
                TextUtils.formatTime(entry.getDate()));

        String string = entry.getWeather();
        if (string != null) {
            weather.setVisibility(View.VISIBLE);
            WeatherResponse weatherResponse = new Gson().fromJson(string, WeatherResponse.class);
            weather.setText(weatherResponse.getOneLineTemperatureString());
        } else {
            weather.setVisibility(View.GONE);
        }

        if (entry.getUri() != null) {
            postponeEnterTransition();

            image.setVisibility(View.VISIBLE);

            Glide.with(this)
                    .fromString()
                    .asBitmap()
                    .load(entry.getUri())
                    .centerCrop()
                    .listener(new RequestListener<String, Bitmap>() {
                        @Override
                        public boolean onException(Exception e, String model, Target<Bitmap> target, boolean isFirstResource) {
                            Logg.log(e);
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(final Bitmap resource, String model, Target<Bitmap> target,
                                                       boolean isFromMemoryCache, boolean isFirstResource) {
                            Palette.from(resource)
                                    .maximumColorCount(3)
                                    .clearFilters()
                                    .generate(new PaletteWindows(EntryActivity.this, resource));

                            startPostponedEnterTransition();
                            return false;
                        }
                    })
                    .into(image);
        } else {
            image.setVisibility(View.GONE);
        }
    }

    private void slideUpView(ViewGroup root, int duration) {
        ViewGroup linearLayout = (ViewGroup) ((ViewGroup) root.getChildAt(1)).getChildAt(0);
        final float offset = linearLayout.getHeight() / 2f;
        for (int i = 0; i < linearLayout.getChildCount(); i++) {
            View v = linearLayout.getChildAt(i);

            v.setTranslationY(offset);
            v.setAlpha(0f);

            v.animate()
                    .translationY(0f)
                    .alpha(1f)
                    .setDuration(duration)
                    .setInterpolator(new DecelerateInterpolator())
                    .setStartDelay(150 + 50 * i)
                    .start();
        }
    }

    private void setResultAction(Action action) {
        Intent intent = new Intent();
        intent.putExtra(INTENT_KEY, action);
        intent.putExtra(INTENT_ACTION, entry.getDateMillis());
        setResult(RESULT_OK, intent);
        finish();
    }

    @OnClick(R.id.activity_entry_place_photos)
    protected void fabClick() {
        if (hasConnection()) {
            Intent intent = new Intent(this, PlacePhotosActivity.class);
            intent.putExtra(PlacePhotosActivity.ID, entry.getPlaceId());
            intent.putExtra(PlacePhotosActivity.NAME, entry.getPlaceName());
            startActivity(intent);
        } else {
            Snackbar.make(root, R.string.error_no_internet, Snackbar.LENGTH_SHORT).show();
        }
    }
}
