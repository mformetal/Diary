package miles.diary.ui.activity;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.content.IntentSender;
import android.graphics.Bitmap;
import android.graphics.drawable.VectorDrawable;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceLikelihoodBuffer;

import butterknife.Bind;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import icepick.State;
import miles.diary.R;
import miles.diary.data.ActivitySubscriber;
import miles.diary.data.model.weather.Main;
import miles.diary.data.model.weather.Weather;
import miles.diary.data.model.weather.WeatherResponse;
import miles.diary.ui.widget.TypefaceEditText;
import miles.diary.ui.widget.TypefaceIconTextView;
import miles.diary.util.AnimUtils;
import miles.diary.util.FileUtils;
import miles.diary.util.GoogleUtils;
import miles.diary.util.IntentUtils;
import miles.diary.util.Logg;
import miles.diary.util.TextUtils;

public class NewEntryActivity extends BaseActivity implements View.OnClickListener,
        GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks {

    @Bind(R.id.activity_new_entry_root) CoordinatorLayout root;
    @Bind(R.id.fragment_entry_toolbar) Toolbar toolbar;
    @Bind(R.id.activity_new_entry_body) TypefaceEditText bodyInput;
    @Bind(R.id.activity_new_entry_photo) CircleImageView photo;
    @Bind(R.id.activity_new_entry_location) CircleImageView location;
    @Bind(R.id.activity_new_entry_temperature) TypefaceIconTextView weatherText;

    public static final String RESULT_BODY = "body";
    public static final String RESULT_URI = "uri";
    public static final String RESULT_PLACE_NAME = "place";
    public static final String RESULT_PLACE_ID = "placeId";
    public static final String RESULT_TEMPERATURE = "temperature";
    public static final String LOCATION_IMAGE = "image";
    private final static int REQUEST_LOCATION = 1;
    private final static int REQUEST_IMAGE = 2;
    private final static int REQUEST_LOCATION_PERMISSION = 3;

    @State String placeName;
    @State String placeId;
    @State String temperature;
    @State Uri imageUri;
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
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        FileUtils.deleteBitmapFile(this, LOCATION_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_LOCATION:
                if (resultCode == RESULT_OK) {
                    Bundle extras = data.getExtras();
                    placeName = extras.getString(NewEntryActivity.RESULT_PLACE_NAME);
                    placeId = extras.getString(NewEntryActivity.RESULT_PLACE_ID);

                    getPlacePhoto();
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
                    result.putExtra(NewEntryActivity.RESULT_BODY, body);
                    result.putExtra(NewEntryActivity.RESULT_URI, imageUri);
                    result.putExtra(NewEntryActivity.RESULT_PLACE_NAME, placeName);
                    result.putExtra(NewEntryActivity.RESULT_PLACE_ID, placeId);
                    result.putExtra(NewEntryActivity.RESULT_TEMPERATURE, temperature);
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
                                    getString(R.string.transition_image));
                    startActivityForResult(intent, REQUEST_IMAGE, transitionActivityOptions.toBundle());
                }
                break;
            case R.id.activity_new_entry_location:
                Intent intent1 = new Intent(this, LocationActivity.class);
                intent1.putExtra(NewEntryActivity.RESULT_PLACE_NAME, placeName);
                intent1.putExtra(NewEntryActivity.RESULT_PLACE_ID, placeId);
                ActivityOptions transitionActivityOptions =
                        ActivityOptions.makeSceneTransitionAnimation(this, location,
                                getString(R.string.transition_image));
                startActivityForResult(intent1, REQUEST_LOCATION, transitionActivityOptions.toBundle());
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_LOCATION_PERMISSION:
                if (permissionsGranted(grantResults, 1)) {
                    getWeather();

                    getPlace();
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

                            getPlacePhoto();

                            placeLikelihoods.release();
                        }
                    });
        }
    }

    private void getWeather() {
        if (temperature == null) {
           GoogleUtils.getLocation(this)
                    .flatMap(location1 -> weatherService.getWeather(location1.getLatitude(), location1.getLongitude()))
                    .subscribe(new ActivitySubscriber<WeatherResponse>(this) {
                        @Override
                        public void onNext(WeatherResponse weatherResponse) {
                            Main main = weatherResponse.getMain();

                            Weather weather = weatherResponse.getWeather().get(0);

                            temperature = TextUtils.getWeatherIcon(weather.getIcon()) + "\n" +
                                    main.formatTemperature();

                            weatherText.setText(temperature);
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

    private void getPlacePhoto() {
        if (FileUtils.isFileAvailable(this, LOCATION_IMAGE)) {
            FileUtils.getBitmapBytes(this, NewEntryActivity.LOCATION_IMAGE)
                    .subscribe(new ActivitySubscriber<byte[]>(this) {
                        @Override
                        public void onNext(byte[] bytes) {
                            super.onNext(bytes);
                            Glide.with(getSubscribedActivity())
                                    .load(bytes)
                                    .asBitmap()
                                    .animate(AnimUtils.REVEAL)
                                    .into(location);
                        }
                    });
        } else {
            if (placeId != null) {
                GoogleUtils.getPlacePhoto(placeId, googleApiClient)
                        .flatMap(placePhotoResult -> {
                            Bitmap bitmap = placePhotoResult.getBitmap();
                            location.setImageBitmap(bitmap);
                            AnimUtils.pop(location, -1);

                            return FileUtils.saveBitmap(NewEntryActivity.this, bitmap,
                                    LOCATION_IMAGE);
                        })
                        .subscribe(new ActivitySubscriber<>(this));
            }
        }
    }
}
