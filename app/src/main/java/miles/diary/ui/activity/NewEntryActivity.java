package miles.diary.ui.activity;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.gson.Gson;

import butterknife.Bind;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import miles.diary.R;
import miles.diary.data.api.LocationService;
import miles.diary.data.model.realm.Entry;
import miles.diary.data.model.google.PlaceResponse;
import miles.diary.data.rx.ActivitySubscriber;
import miles.diary.data.model.weather.WeatherResponse;
import miles.diary.ui.transition.FabDialogHelper;
import miles.diary.ui.widget.TypefaceEditText;
import miles.diary.ui.widget.TypefaceIconTextView;
import miles.diary.util.AnimUtils;
import miles.diary.data.api.google.GoogleService;
import miles.diary.util.Logg;
import miles.diary.util.ViewUtils;

public class NewEntryActivity extends BaseActivity implements View.OnClickListener {

    @Bind(R.id.fragment_entry_toolbar) Toolbar toolbar;
    @Bind(R.id.activity_new_entry_body) TypefaceEditText bodyInput;
    @Bind(R.id.activity_new_entry_photo) CircleImageView photo;
    @Bind(R.id.activity_new_entry_location) ImageView location;
    @Bind(R.id.activity_new_entry_temperature) TypefaceIconTextView weatherText;

    public static final String BODY = "body";
    public static final String URI = "uri";
    public static final String PLACE_NAME = "place";
    public static final String PLACE_ID = "placeId";
    public static final String TEMPERATURE = "temperature";
    private final static int REQUEST_LOCATION = 1;
    private final static int REQUEST_IMAGE = 2;
    private final static int REQUEST_LOCATION_PERMISSION = 3;

    private String placeName;
    private String placeId;
    private String temperature;
    private Uri imageUri;
    private WeatherResponse weather;
    private GoogleService googleService;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_entry);
        FabDialogHelper.makeFabDialogTransition(this, root, 20);

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

        ViewUtils.mutate(location, ContextCompat.getColor(this, R.color.accent));

        googleService = new GoogleService(this, googleApiClientBuilder,
                new GoogleService.GoogleServiceCallback() {
                    @Override
                    public void onConnected(Bundle bundle, GoogleApiClient client, BaseActivity activity) {
                        getLocationData();
                    }
                });
    }

    @Override
    protected void onStop() {
        super.onStop();
        googleService.cleanup();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_LOCATION:
                if (resultCode == RESULT_OK) {
                    Bundle extras = data.getExtras();
                    placeName = extras.getString(NewEntryActivity.PLACE_NAME);
                    placeId = extras.getString(NewEntryActivity.PLACE_ID);
                }
                break;
            case REQUEST_IMAGE:
                if (resultCode == RESULT_OK) {
                    imageUri = data.getData();

                    loadThumbnailFromUri(imageUri);
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
                    result.putExtra(NewEntryActivity.BODY, body);
                    result.putExtra(NewEntryActivity.URI, imageUri);
                    result.putExtra(NewEntryActivity.PLACE_NAME, placeName);
                    result.putExtra(NewEntryActivity.PLACE_ID, placeId);
                    if (weather != null) {
                        result.putExtra(NewEntryActivity.TEMPERATURE,
                                new Gson().toJson(weather, WeatherResponse.class));
                    }
                    setResult(RESULT_OK, result);
                    finishAfterTransition();
                } else {
                    Snackbar.make(root, R.string.activity_entry_no_text_error,
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
                Intent intent = new Intent(this, UriActivity.class);
                intent.setData(imageUri);

                if (imageUri == null) {
                    startActivityForResult(intent, REQUEST_IMAGE);
                } else {
                    ActivityOptions transitionActivityOptions =
                            ActivityOptions.makeSceneTransitionAnimation(this, photo,
                                    getString(R.string.transition_location_image));
                    startActivityForResult(intent, REQUEST_IMAGE, transitionActivityOptions.toBundle());
                }
                break;
            case R.id.activity_new_entry_location:
                if (placeName != null && placeId != null) {
                    Intent intent1 = new Intent(this, LocationActivity.class);
                    intent1.putExtra(NewEntryActivity.PLACE_NAME, placeName);
                    intent1.putExtra(NewEntryActivity.PLACE_ID, placeId);
                    ActivityOptions transitionActivityOptions =
                            ActivityOptions.makeSceneTransitionAnimation(this, location,
                                    getString(R.string.transition_location_image));
                    startActivityForResult(intent1, REQUEST_LOCATION, transitionActivityOptions.toBundle());
                }
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
                AnimUtils.textScale(weatherText,
                        weather.getTwoLineTemperatureString(), .2f, 1f).start();
            }

            placeName = entry.getPlaceName();
            placeId = entry.getPlaceId();
            if (entry.getPlaceName() != null) {
                location.setColorFilter(Color.WHITE);
            }
        }
    }

    private void getPlace(Location location1) {
        if (placeName == null && placeId == null) {
            googleService.searchNearby(location1, 5)
                    .subscribe(new ActivitySubscriber<PlaceResponse>(this) {
                        @Override
                        public void onNext(PlaceResponse placeResponse) {
                            PlaceResponse.Result result = placeResponse.getResults().get(0);
                            placeName = result.getName();
                            placeId = result.getId();

                            Logg.log("END NEARBY SEARCH", placeName);

                            Logg.log(placeName);

                            Activity activity = getSubscribedActivity();
                            if (activity != null) {
                                AnimUtils.colorFilter(location,
                                        ContextCompat.getColor(activity, R.color.accent),
                                        Color.WHITE)
                                        .start();
                            }
                        }

                        @Override
                        public void onStart() {
                            super.onStart();
                            Logg.log("START NEARBY SEARCH");
                        }
                    });
        } else {
            AnimUtils.colorFilter(location,
                    ContextCompat.getColor(NewEntryActivity.this, R.color.accent),
                    Color.WHITE)
                    .start();
        }
    }

    private void getWeather(Location location) {
        if (temperature == null) {
            weatherService.getWeather(location.getLatitude(),
                    location.getLongitude())
                    .subscribe(new ActivitySubscriber<WeatherResponse>(this) {
                        @Override
                        public void onNext(WeatherResponse weatherResponse) {
                            weather = weatherResponse;

                            temperature = weatherResponse.getTwoLineTemperatureString();

                            AnimUtils.textScale(weatherText, temperature, .2f, 1f).start();
                        }
                    });
        }
    }

    private void loadThumbnailFromUri(Uri uri) {
        if (uri != null) {
            photo.clearColorFilter();
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
            photo.clearColorFilter();
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
                                public void onNext(Location location) {
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
            } else {
                noInternet();
            }
        } else {
            ActivityCompat.requestPermissions(this, permissions, REQUEST_LOCATION_PERMISSION);
        }
    }
}
