package miles.diary.ui.activity;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
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
import android.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.gson.Gson;

import butterknife.Bind;
import butterknife.OnClick;
import miles.diary.R;
import miles.diary.data.api.LocationService;
import miles.diary.data.api.GoogleService;
import miles.diary.data.model.google.PlaceResponse;
import miles.diary.data.model.realm.Entry;
import miles.diary.data.model.weather.WeatherResponse;
import miles.diary.data.rx.ActivitySubscriber;
import miles.diary.ui.transition.FabDialogHelper;
import miles.diary.ui.widget.CircleImageView;
import miles.diary.ui.widget.TypefaceButton;
import miles.diary.ui.widget.TypefaceEditText;
import miles.diary.util.AnimUtils;
import miles.diary.util.ViewUtils;
import rx.Observable;
import rx.functions.Func1;

public class NewEntryActivity extends BaseActivity implements View.OnClickListener {

    @Bind(R.id.fragment_entry_toolbar) Toolbar toolbar;
    @Bind(R.id.activity_new_entry_body) TypefaceEditText bodyInput;
    @Bind(R.id.activity_new_entry_photo) CircleImageView photo;
    @Bind(R.id.activity_new_entry_location) TypefaceButton location;

    public static final String BODY = "body";
    public static final String URI = "uri";
    public static final String PLACE_NAME = "place";
    public static final String PLACE_ID = "placeId";
    public static final String TEMPERATURE = "temperature";
    private final static int RESULT_LOCATION = 1;
    private final static int RESULT_IMAGE = 2;
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
        FabDialogHelper.makeFabDialogTransition(this, root, 20, AnimUtils.longAnim(this));

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
            case RESULT_LOCATION:
                if (resultCode == RESULT_OK) {
                    Bundle extras = data.getExtras();
                    placeName = extras.getString(NewEntryActivity.PLACE_NAME);
                    placeId = extras.getString(NewEntryActivity.PLACE_ID);
                }
                break;
            case RESULT_IMAGE:
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
                startActivityForResult(intent1, RESULT_LOCATION);
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
        location.setVisibility(View.VISIBLE);
        location.setCompoundDrawablesWithIntrinsicBounds(
                ContextCompat.getDrawable(this, R.drawable.ic_place_24dp), null, null, null);
        location.setText(name);
    }

    private void getPlace(Location location1) {
        if (placeName == null && placeId == null) {
            googleService.searchNearby(location1, 5)
                    .subscribe(new ActivitySubscriber<PlaceResponse>(this) {
                        @Override
                        public void onNext(PlaceResponse placeResponse) {
                            PlaceResponse.PlaceResult result = placeResponse.getResults().get(0);
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
                    addSubscription(
                            googleService.getLocation()
                                    .switchMap(new Func1<Location, Observable<?>>() {
                                        @Override
                                        public Observable<?> call(Location location) {
                                            getWeather(location);
                                            getPlace(location);
                                            return Observable.just(location);
                                        }
                                    }).subscribe());
                } else {
                    LocationService.getLocationAvailabilityUpdate(this, new LocationService.AvailabilityCallback() {
                        @Override
                        public void onLocationEnabled(Context context, BroadcastReceiver receiver, Intent intent) {
                            getLocationData();
                        }
                    });

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
