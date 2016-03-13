package miles.diary.ui.activity;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.ActivityOptions;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.transition.ArcMotion;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Interpolator;
import android.widget.Toast;
import android.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Locale;

import butterknife.Bind;
import butterknife.OnClick;
import miles.diary.R;
import miles.diary.data.api.LocationService;
import miles.diary.data.api.GoogleService;
import miles.diary.data.api.WeatherService;
import miles.diary.data.model.google.PlaceResponse;
import miles.diary.data.model.realm.Entry;
import miles.diary.data.model.weather.WeatherResponse;
import miles.diary.data.rx.ActivitySubscriber;
import miles.diary.ui.transition.ContainerFabTransition;
import miles.diary.ui.transition.FabContainerTransition;
import miles.diary.ui.widget.CircleImageView;
import miles.diary.ui.widget.TypefaceButton;
import miles.diary.ui.widget.TypefaceEditText;
import miles.diary.util.AnimUtils;
import miles.diary.util.Logg;
import miles.diary.util.ViewUtils;
import rx.functions.Action1;

public class NewEntryActivity extends BaseActivity implements View.OnClickListener {

    @Bind(R.id.fragment_entry_toolbar) Toolbar toolbar;
    @Bind(R.id.activity_new_entry_body) TypefaceEditText bodyInput;
    @Bind(R.id.activity_new_entry_photo) CircleImageView photo;
    @Bind(R.id.activity_new_entry_location) TypefaceButton locationName;

    public static final String BODY = "body";
    public static final String URI = "uri";
    public static final String PLACE_NAME = "place";
    public static final String PLACE_ID = "placeId";
    public static final String LOCATION = "locationName";
    public static final String TEMPERATURE = "temperature";
    private final static int RESULT_LOCATION = 1;
    private final static int RESULT_IMAGE = 2;
    private final static int RESULT_SPEECH = 3;
    private final static int REQUEST_LOCATION_PERMISSION = 3;

    private String placeName;
    private String placeId;
    private String temperature;
    private Uri imageUri;
    private WeatherResponse weather;
    private GoogleService googleService;
    private WeatherService weatherService;
    private Location location;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_entry);
        setupTransitions();

        setActionBar(toolbar);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            long id =  bundle.getLong(EntryActivity.INTENT_KEY, -1L);
            if (id != -1L) {
                dataManager.getObject(Entry.class, id)
                        .subscribe(new ActivitySubscriber<Entry>(this) {
                            @Override
                            public void onNext(Entry entry) {
                                updateViews(entry);
                            }
                        });
            }
        }

        ViewUtils.mutate(locationName, ContextCompat.getColor(this, R.color.accent));

        weatherService = new WeatherService(this);

        googleService = new GoogleService(this, googleApiClientBuilder,
                new GoogleService.GoogleServiceCallback() {
                    @Override
                    public void onConnected(Bundle bundle) {
                        getLocationData();
                    }
                });

        bodyInput.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                        RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
                intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                        getString(R.string.activity_new_entry_speech_prompt));
                try {
                    startActivityForResult(intent, RESULT_SPEECH);
                    return true;
                } catch (ActivityNotFoundException a) {
                    Toast.makeText(v.getContext(),
                            getString(R.string.activity_new_entry_speech_not_supported),
                            Toast.LENGTH_SHORT).show();
                    return false;
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (location == null && LocationService.isLocationEnabled(this)) {
            googleService.getLocation()
                    .subscribe(new ActivitySubscriber<Location>(this) {
                        @Override
                        public void onNext(Location location1) {
                            location = location1;
                            getWeather(location);
                            getPlace(location);
                        }
                    });
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case RESULT_LOCATION:
                if (resultCode == RESULT_OK) {
                    Bundle extras = data.getExtras();
                    placeName = extras.getString(NewEntryActivity.PLACE_NAME);
                    placeId = extras.getString(NewEntryActivity.PLACE_ID);

                    setLocationText(placeName);
                }
                break;
            case RESULT_IMAGE:
                if (resultCode == RESULT_OK) {
                    imageUri = data.getData();

                    loadThumbnailFromUri(imageUri);
                }
                break;
            case RESULT_SPEECH:
                if (resultCode == RESULT_OK && data != null) {
                    ArrayList<String> result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    bodyInput.setText(result.get(0));
                }
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_new_entry, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_new_entry_done:
                String body = bodyInput.getTextAsString();
                if (!body.isEmpty()) {
                    Intent result = new Intent();
                    result.putExtra(BODY, body);
                    result.putExtra(URI, imageUri);
                    result.putExtra(PLACE_NAME, placeName);
                    result.putExtra(PLACE_ID, placeId);
                    result.putExtra(LOCATION, location);
                    if (weather != null) {
                        result.putExtra(TEMPERATURE, new Gson().toJson(weather, WeatherResponse.class));
                    }
                    setResult(RESULT_OK, result);
                    finishAfterTransition();
                } else {
                    Snackbar.make(root, R.string.activity_new_entry_no_input_error,
                            Snackbar.LENGTH_SHORT).show();
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    @OnClick({R.id.activity_new_entry_photo, R.id.activity_new_entry_location})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.activity_new_entry_photo:
                Intent intent = new Intent(this, GalleryActivity.class);
                startActivityForResult(intent, RESULT_IMAGE);
                break;
            case R.id.activity_new_entry_location:
                Intent intent1 = new Intent(this, LocationActivity.class);
                ActivityOptions options =
                        ActivityOptions.makeSceneTransitionAnimation(this, locationName,
                                getString(R.string.transition_location));
                intent1.putExtra(PLACE_NAME, placeName);
                intent1.putExtra(PLACE_ID, placeId);
                startActivityForResult(intent1, RESULT_LOCATION, options.toBundle());
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_LOCATION_PERMISSION:
                if (permissionsGranted(grantResults)) {
                    getLocationData();
                } else {
                    finish();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private void updateViews(Entry entry) {
        if (entry != null) {
            String stringUri = entry.getUri();
            if (stringUri != null) {
                loadThumbnailFromUri(stringUri);
                imageUri = Uri.parse(stringUri);
            }

            bodyInput.setText(entry.getBody());

            String string = entry.getUri();
            if (string != null) {
                loadThumbnailFromUri(string);
            }

            String entryWeather = entry.getWeather();
            if (entryWeather != null) {
                weather = new Gson().fromJson(entryWeather, WeatherResponse.class);
                temperature = weather.getTwoLineTemperatureString();
            }

            placeName = entry.getPlaceName();
            placeId = entry.getPlaceId();
            if (entry.getPlaceName() != null) {
                setLocationText(placeName);
            }
        }
    }

    private void setLocationText(String name) {
        Drawable drawable = ContextCompat.getDrawable(
                NewEntryActivity.this, R.drawable.ic_place_24dp);
        locationName.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null);
        locationName.setText(name);

        ObjectAnimator.ofFloat(locationName, View.SCALE_X, 0f, 1f)
                .setDuration(AnimUtils.longAnim(this))
                .start();
    }

    private void getPlace(Location location1) {
        if (placeName == null && placeId == null) {
            final ObjectAnimator pulse = ObjectAnimator.ofFloat(locationName, View.SCALE_X, .95f, 1f);
            pulse.setDuration(AnimUtils.mediumAnim(this));
            pulse.setRepeatCount(ValueAnimator.INFINITE);
            pulse.setRepeatMode(ValueAnimator.REVERSE);
            pulse.start();

            googleService.searchNearby(location1, 3)
                    .subscribe(new ActivitySubscriber<PlaceResponse>(this) {
                        @Override
                        public void onNext(PlaceResponse placeResponse) {
                            pulse.end();

                            PlaceResponse.PlaceResult result =
                                    placeResponse.getResults().get(0);
                            placeName = result.getName();
                            placeId = result.getId();

                            setLocationText(placeName);
                        }
                    });
        } else {
            setLocationText(placeName);
        }
    }

    private void getWeather(Location location) {
        if (temperature == null) {
            weatherService.getWeather(location.getLatitude(), location.getLongitude())
                    .subscribe(new ActivitySubscriber<WeatherResponse>(this) {
                        @Override
                        public void onNext(WeatherResponse weatherResponse) {
                            weather = weatherResponse;
                            temperature = weatherResponse.getTwoLineTemperatureString();
                        }
                    });
        }
    }

    private void loadThumbnailFromUri(Uri uri) {
        if (uri != null) {
            Glide.with(this)
                    .fromUri()
                    .animate(R.anim.glide_pop)
                    .load(uri)
                    .centerCrop()
                    .into(photo);
        }
    }

    private void loadThumbnailFromUri(String uri) {
        if (uri != null) {
            Glide.with(this)
                    .fromString()
                    .animate(R.anim.glide_pop)
                    .load(uri)
                    .centerCrop()
                    .into(photo);
        }
    }

    private void getLocationData() {
        String[] permissions = new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION};
        if (hasPermissions(permissions)) {
            if (hasConnection()) {
                if (LocationService.isLocationEnabled(this)) {
                    googleService.getLocation()
                            .subscribe(new ActivitySubscriber<Location>(this) {
                                @Override
                                public void onNext(Location location1) {
                                    location = location1;
                                    getWeather(location);
                                    getPlace(location);
                                }
                            });
                } else {
                    Snackbar.make(root,
                            R.string.activity_location_not_enabled,
                            Snackbar.LENGTH_LONG)
                            .setAction(android.R.string.ok, new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                                }
                            })
                            .show();
                }
            }
        } else {
            ActivityCompat.requestPermissions(this, permissions, REQUEST_LOCATION_PERMISSION);
        }
    }

    private void setupTransitions() {
        int dur = AnimUtils.longAnim(this);
        int start = getIntent().getIntExtra(FabContainerTransition.START_COLOR, Color.TRANSPARENT);
        int end = getIntent().getIntExtra(FabContainerTransition.END_COLOR, Color.TRANSPARENT);
        Interpolator easeInOut = new FastOutSlowInInterpolator();

        FabContainerTransition sharedEnter = new FabContainerTransition(start, end,
                getResources().getDimensionPixelSize(R.dimen.fab_corner_radius));
        ArcMotion arcMotion = new ArcMotion();
        arcMotion.setMinimumHorizontalAngle(30f);
        arcMotion.setMinimumVerticalAngle(30f);

        sharedEnter.setPathMotion(arcMotion);
        sharedEnter.setInterpolator(easeInOut);
        sharedEnter.addTarget(root);
        sharedEnter.setDuration(dur);

        ContainerFabTransition sharedReturn = new ContainerFabTransition(end, start);
        ArcMotion returnArc = new ArcMotion();
        returnArc.setMinimumHorizontalAngle(70f);
        returnArc.setMinimumVerticalAngle(70f);

        sharedReturn.setPathMotion(returnArc);
        sharedReturn.setInterpolator(easeInOut);
        sharedReturn.addTarget(root);
        sharedReturn.setDuration(dur);

        getWindow().setSharedElementEnterTransition(sharedEnter);
        getWindow().setSharedElementReturnTransition(sharedReturn);
    }
}
