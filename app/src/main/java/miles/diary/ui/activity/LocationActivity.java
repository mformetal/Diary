package miles.diary.ui.activity;

import android.Manifest;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.view.View;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.ViewTarget;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import butterknife.Bind;
import butterknife.OnClick;
import miles.diary.R;
import miles.diary.data.ActivitySubscriber;
import miles.diary.data.adapter.AutoCompleteAdapter;
import miles.diary.data.model.weather.Main;
import miles.diary.data.model.weather.Weather;
import miles.diary.ui.PreDrawer;
import miles.diary.ui.widget.TypefaceAutoCompleteTextView;
import miles.diary.ui.widget.TypefaceButton;
import miles.diary.ui.widget.TypefaceTextView;
import miles.diary.util.AnimUtils;
import miles.diary.util.IntentUtils;
import miles.diary.util.Logg;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by mbpeele on 2/6/16.
 */
public class LocationActivity extends BaseActivity
        implements View.OnClickListener,
        GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks {

    private final static int REQUEST_LOCATION_PERMISSION = 1;
    private final static int REQUEST_PLACE_PICKER = 2;
    public final static String RESULT_LOCATION_NAME = "name";
    public final static String RESULT_LOCATION_ID = "id";
    public final static String RESULT_TEMPERATURE = "temperature";
    public final static String RESULT_TEMPERATURE_ICON = "icon";

    @Bind(R.id.activity_location_neg_button) TypefaceButton negButton;
    @Bind(R.id.activity_location_pos_button) TypefaceButton posButton;
    @Bind(R.id.activity_location_weather) TypefaceTextView weatherText;
    @Bind(R.id.activity_location_autocomplete) TypefaceAutoCompleteTextView autoCompleteTextView;

    private GoogleApiClient googleApiClient;
    private AutoCompleteAdapter autoCompleteAdapter;
    private String locationName;
    private String locationId;
    private String temperature;
    private boolean hasPreviousInfo = false;
    private byte[] temperatureIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);

        setupTransitions();

        googleApiClient = googleApiClientBuilder
                .enableAutoManage(this, 0, this)
                .addConnectionCallbacks(this)
                .build();

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            locationName = bundle.getString(RESULT_LOCATION_NAME);
            locationId = bundle.getString(RESULT_LOCATION_ID);
            temperature = bundle.getString(RESULT_TEMPERATURE);
            temperatureIcon = bundle.getByteArray(RESULT_TEMPERATURE_ICON);

            if (locationName != null) {
                hasPreviousInfo = true;
                autoCompleteTextView.setText(locationName, false);
                weatherText.setText(temperature);
                getWeatherIconAndText();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_PLACE_PICKER:
                if (resultCode == RESULT_OK) {
                    Place place = PlacePicker.getPlace(this, data);
                    locationName = place.getName().toString();
                    locationId = place.getId();

                    autoCompleteTextView.setText(locationName, false);
                }
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_LOCATION_PERMISSION:
                if (permissionsGranted(grantResults, 1)) {
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
    @OnClick({R.id.activity_location_pos_button, R.id.activity_location_neg_button,
            R.id.activity_location_image})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.activity_location_pos_button:
                if (locationName != null) {
                    Intent intent = new Intent();
                    intent.putExtra(RESULT_LOCATION_NAME, locationName);
                    intent.putExtra(RESULT_LOCATION_ID, locationId);
                    intent.putExtra(RESULT_TEMPERATURE, weatherText.getStringText());
                    intent.putExtra(RESULT_TEMPERATURE_ICON, temperatureIcon);
                    setResult(RESULT_OK, intent);
                    onBackPressed();
                } else {
                    Snackbar.make(root, R.string.activity_location_no_input,
                            Snackbar.LENGTH_SHORT).show();
                }
                break;
            case R.id.activity_location_neg_button:
                Intent intent = new Intent();
                setResult(RESULT_CANCELED, intent);
                onBackPressed();
                break;
            case R.id.activity_location_image:
                PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();

                try {
                    startActivityForResult(builder.build(this), REQUEST_PLACE_PICKER);
                } catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException e) {
                    Logg.log(e);
                }
                break;
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        if (hasConnection()) {
            String[] permissions = new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION};
            if (hasPermissions(permissions)) {
                Location loc = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
                if (loc != null) {
                    double latitude = loc.getLatitude();
                    double longitude = loc.getLongitude();
                    LatLngBounds bounds = new LatLngBounds(
                            new LatLng(latitude - 0.05, longitude - 0.05),
                            new LatLng(latitude + 0.05, longitude + 0.05));
                    autoCompleteAdapter = new AutoCompleteAdapter(this, R.layout.autocomplete_adapter,
                            googleApiClient, bounds);
                    autoCompleteTextView.setAdapter(autoCompleteAdapter);

                    getWeather();

                    getPlace();
                }
            } else {
                ActivityCompat.requestPermissions(this, permissions, REQUEST_LOCATION_PERMISSION);
            }
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
    }

    private void getPlace() {
        if (!hasPreviousInfo) {
            Places.PlaceDetectionApi.getCurrentPlace(googleApiClient, null)
                    .setResultCallback(placeLikelihoods -> {
                        Place mostLikely = placeLikelihoods.get(0).getPlace();

                        locationId = mostLikely.getId();
                        locationName = mostLikely.getName().toString();

                        autoCompleteTextView.setText(locationName, false);

                        placeLikelihoods.release();
                    });
        }
    }

    private void getWeather() {
        if (!hasPreviousInfo) {
            Location loc = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
            if (loc != null) {
                weatherService.getWeather(loc.getLatitude(), loc.getLongitude())
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .flatMap(weatherResponse -> {
                            Main main = weatherResponse.getMain();

                            Weather weather = weatherResponse.getWeather().get(0);

                            weatherText.setAlpha(0f);
                            weatherText.setScaleX(.8f);

                            weatherText.setText(main.formatTemperature());

                            weatherText.animate()
                                    .alpha(1f)
                                    .scaleX(1f)
                                    .setDuration(AnimUtils.longAnim(LocationActivity.this))
                                    .setInterpolator(new FastOutSlowInInterpolator());

                            return weatherService.getWeatherIcon(weather.getIcon())
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread());
                        })
                        .subscribe(new ActivitySubscriber<byte[]>(this) {
                            @Override
                            public void onNext(byte[] bytes) {
                                temperatureIcon = bytes;
                                getWeatherIconAndText();
                            }
                        });
            }
        }
    }

    private void getWeatherIconAndText() {
        Glide.with(this)
                .load(temperatureIcon)
                .asBitmap()
                .into(new ViewTarget<TypefaceTextView, Bitmap>(weatherText) {
                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                        Drawable drawable = new BitmapDrawable(getResources(), resource);
                        view.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null);
                    }
                });
    }

    @SuppressWarnings("unchecked")
    private void setupTransitions() {
        new PreDrawer(root) {
            @Override
            public void notifyPreDraw(View view) {
                float offset = root.getHeight() / 3;
                for (int i = 0; i < root.getChildCount(); i++) {
                    View v = root.getChildAt(i);
                    v.setTranslationY(offset);
                    v.setAlpha(0f);
                    v.animate()
                            .alpha(1f)
                            .translationY(0f)
                            .setDuration(AnimUtils.shortAnim(LocationActivity.this))
                            .setStartDelay(150)
                            .setInterpolator(new FastOutSlowInInterpolator());

                    offset *= 1.8f;
                }
            }
        };
    }
}
