package miles.diary.ui.activity;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.content.IntentSender;
import android.graphics.Color;
import android.location.Address;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceLikelihoodBuffer;
import com.google.gson.Gson;

import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import icepick.State;
import miles.diary.R;
import miles.diary.data.ActivitySubscriber;
import miles.diary.data.model.weather.WeatherResponse;
import miles.diary.ui.widget.TypefaceEditText;
import miles.diary.ui.widget.TypefaceIconTextView;
import miles.diary.util.AnimUtils;
import miles.diary.util.GoogleUtils;
import miles.diary.util.IntentUtils;
import miles.diary.util.Logg;
import miles.diary.util.TextUtils;
import rx.Observable;
import rx.functions.Func1;

public class NewEntryActivity extends BaseActivity implements View.OnClickListener,
        GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks {

    @Bind(R.id.activity_new_entry_root) CoordinatorLayout root;
    @Bind(R.id.fragment_entry_toolbar) Toolbar toolbar;
    @Bind(R.id.activity_new_entry_body) TypefaceEditText bodyInput;
    @Bind(R.id.activity_new_entry_photo) CircleImageView photo;
    @Bind(R.id.activity_new_entry_location) CircleImageView location;
    @Bind(R.id.activity_new_entry_temperature) TypefaceIconTextView weatherText;

    public static final String BODY = "entryBody";
    public static final String URI = "uri";
    public static final String PLACE_NAME = "entryPlace";
    public static final String PLACE_ID = "placeId";
    public static final String TEMPERATURE = "temperature";
    public static final String ADDRESS = "address";
    private final static int REQUEST_LOCATION = 1;
    private final static int REQUEST_IMAGE = 2;
    private final static int REQUEST_LOCATION_PERMISSION = 3;

    @State String placeName;
    @State String placeId;
    @State String temperature;
    @State Uri imageUri;
    @State String address;
    private WeatherResponse weather;
    private GoogleApiClient googleApiClient;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_entry);

        setActionBar(toolbar);

        googleApiClient = googleApiClientBuilder
                .enableAutoManage(this, 0, this)
                .addConnectionCallbacks(this)
                .build();

        int color = ContextCompat.getColor(this, R.color.accent);
        photo.setColorFilter(color);
        location.setColorFilter(color);
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

                    loadThumbnailFromUri();
                }
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_entry, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_entry_done:
                String body = bodyInput.getTextAsString();
                if (!body.isEmpty()) {
                    Intent result = new Intent();
                    result.putExtra(NewEntryActivity.BODY, body);
                    result.putExtra(NewEntryActivity.URI, imageUri);
                    result.putExtra(NewEntryActivity.PLACE_NAME, placeName);
                    result.putExtra(NewEntryActivity.PLACE_ID, placeId);
                    result.putExtra(NewEntryActivity.TEMPERATURE,
                            new Gson().toJson(weather, WeatherResponse.class));
                    setResult(Activity.RESULT_OK, result);
                    finish();
                } else {
                    Snackbar.make(root, R.string.activity_entry_no_text_error,
                            Snackbar.LENGTH_SHORT).show();
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    @OnClick({R.id.activity_new_entry_photo, R.id.activity_new_entry_location,
                R.id.activity_new_entry_temperature})
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
                Intent intent1 = new Intent(this, LocationActivity.class);
                intent1.putExtra(NewEntryActivity.PLACE_NAME, placeName);
                intent1.putExtra(NewEntryActivity.PLACE_ID, placeId);
                ActivityOptions transitionActivityOptions =
                        ActivityOptions.makeSceneTransitionAnimation(this, location,
                                getString(R.string.transition_location_image));
                startActivityForResult(intent1, REQUEST_LOCATION, transitionActivityOptions.toBundle());
                break;
            case R.id.activity_new_entry_temperature:
                if (temperature != null) {
                    Intent intent2 = new Intent(this, WeatherActivity.class);
                    intent2.putExtra(NewEntryActivity.TEMPERATURE, temperature);
                    intent2.putExtra(NewEntryActivity.ADDRESS, address);
                    startActivity(intent2);
                }
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_LOCATION_PERMISSION:
                if (permissionsGranted(grantResults)) {
                    getWeather();

                    getPlace();
                } else {
                    finish();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        String[] permissions = new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION};
        if (hasPermissions(permissions)) {
            if (hasConnection()) {
                GoogleUtils.getLocation(this)
                        .subscribe(new ActivitySubscriber<Location>(this) {
                            @Override
                            public void onNext(Location location) {
                                super.onNext(location);
                                if (location != null) {
                                    getWeather();

                                    getPlace();
                                }
                            }
                        });
            } else {
                noInternet();
            }
        } else {
            ActivityCompat.requestPermissions(this, permissions, REQUEST_LOCATION_PERMISSION);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

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

    private void getPlace() {
        if (placeName == null && placeId == null) {
            GoogleUtils.getCurrentPlace(googleApiClient, null)
                    .subscribe(new ActivitySubscriber<PlaceLikelihoodBuffer>(this) {
                        @Override
                        public void onNext(PlaceLikelihoodBuffer placeLikelihoods) {
                            super.onNext(placeLikelihoods);
                            Place mostLikely = placeLikelihoods.get(0).getPlace();

                            placeId = mostLikely.getId();
                            placeName = mostLikely.getName().toString();

                            Activity activity = getSubscribedActivity();
                            if (activity != null) {
                                AnimUtils.colorfilter(location,
                                        ContextCompat.getColor(activity, R.color.accent),
                                        Color.WHITE)
                                        .start();
                            }

                            placeLikelihoods.release();
                        }
                    });
        } else {
            location.setColorFilter(Color.WHITE);
        }
    }

    private void getWeather() {
        weatherText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        if (temperature == null) {
            GoogleUtils.getLocation(this)
                    .flatMap(new Func1<Location, Observable<List<Address>>>() {
                        @Override
                        public Observable<List<Address>> call(Location location) {
                            return GoogleUtils.getAddress(NewEntryActivity.this, location);
                        }
                    })
                    .flatMap(new Func1<List<Address>, Observable<WeatherResponse>>() {
                        @Override
                        public Observable<WeatherResponse> call(List<Address> addresses) {
                            Address address1 = addresses.get(0);
                            address = address1.getLocality();
                            return weatherService.getWeather(address1.getLatitude(),
                                    address1.getLongitude());
                        }
                    })
                    .subscribe(new ActivitySubscriber<WeatherResponse>(this) {
                        @Override
                        public void onNext(WeatherResponse weatherResponse) {
                            weather = weatherResponse;

                            temperature = weatherResponse.getTwoLineTemperatureString();

                            AnimUtils.textScale(weatherText, temperature, .2f, 1f).start();
                        }
                    });
        } else {
            weatherText.setText(temperature);
        }
    }

    private void loadThumbnailFromUri() {
        if (imageUri != null) {
            photo.clearColorFilter();
            Glide.with(this)
                    .fromUri()
                    .animate(R.anim.glide_pop)
                    .load(imageUri)
                    .centerCrop()
                    .into(photo);
        }
    }
}
