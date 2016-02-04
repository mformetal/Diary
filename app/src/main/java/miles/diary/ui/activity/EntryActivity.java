package miles.diary.ui.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toolbar;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlacePicker;

import butterknife.Bind;
import butterknife.OnClick;
import butterknife.OnTouch;
import miles.diary.R;
import miles.diary.ui.widget.TypefaceEditText;
import miles.diary.util.IntentUtils;
import miles.diary.util.Logg;

/**
 * Created by mbpeele on 1/16/16.
 */
public class EntryActivity extends BaseActivity implements View.OnClickListener, View.OnTouchListener,
        GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks {

    @Bind(R.id.activity_entry_root) CoordinatorLayout root;
    @Bind(R.id.activity_entry_toolbar) Toolbar toolbar;
    @Bind(R.id.activity_entry_body) TypefaceEditText bodyInput;

    public static final String RESULT_BODY = "body";
    public static final String RESULT_URI = "uri";
    private final static int REQUEST_CAMERA_PERMISSION = 1;
    private final static int REQUEST_LOCATION_PERMISSION = 2;
    private final static int REQUEST_PLACE_PICKER = 3;
    private final static int REQUEST_IMAGE = 4;
    private final static int REQUEST_RESOLVE_ERROR = 5;

    private GoogleApiClient mGoogleApiClient;
    private Location mLocation;
    private Place mPlace;
    private Uri mImageUri;

    private float lastX, lastY;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entry);

        setActionBar(toolbar);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, 0, this)
                .addApi(Places.GEO_DATA_API)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .build();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CAMERA_PERMISSION:
                if (permissionsGranted(grantResults, 2)) {
                    startImageChooserActivity();
                }
                break;
            case REQUEST_LOCATION_PERMISSION:
                if (permissionsGranted(grantResults, 1)) {
                    startPlaceChooserActivity();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_PLACE_PICKER:
                if (resultCode == RESULT_OK) {
                    mPlace = PlacePicker.getPlace(this, data);
                    Logg.log(mPlace.getName());
                }
                break;
            case REQUEST_IMAGE:
                if (resultCode == RESULT_OK) {
                    mImageUri = data.getData();
                }
                break;
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
                    result.putExtra(EntryActivity.RESULT_BODY, body);
                    result.putExtra(EntryActivity.RESULT_URI, mImageUri);
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
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        if (connectionResult.hasResolution()) {
            try {
                connectionResult.startResolutionForResult(this, REQUEST_RESOLVE_ERROR);
            } catch (IntentSender.SendIntentException e) {
                e.printStackTrace();
            }
        } else {
            Logg.log("CONNECTION FAILED WITH CODE: " + connectionResult.getErrorCode());
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        String perm = Manifest.permission.ACCESS_COARSE_LOCATION;
        if (ContextCompat.checkSelfPermission(this, perm) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{perm}, REQUEST_LOCATION_PERMISSION);
        } else {
            mLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    @OnClick({R.id.activity_entry_photo, R.id.activity_entry_location, R.id.activity_entry_weather,
        R.id.activity_entry_hashtag})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.activity_entry_photo:
                if (getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
                    String[] camera = new String[] {Manifest.permission.CAMERA,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE};
                    if (!hasPermissions(camera)) {
                        ActivityCompat.requestPermissions(this, camera, REQUEST_CAMERA_PERMISSION);
                    } else {
                        startImageChooserActivity();
                    }
                } else {
                    Snackbar.make(root, R.string.activity_entry_no_camera_error,
                            Snackbar.LENGTH_SHORT).show();
                }
                break;
            case R.id.activity_entry_location:
                String[] location = new String[] {Manifest.permission.ACCESS_COARSE_LOCATION};
                if (!hasPermissions(location)) {
                    ActivityCompat.requestPermissions(this, location, REQUEST_LOCATION_PERMISSION);
                } else {
                    startPlaceChooserActivity();
                }
                break;
            case R.id.activity_entry_weather:
                break;
            case R.id.activity_entry_hashtag:
                break;
        }
    }

    @Override
    @OnTouch({R.id.activity_entry_photo, R.id.activity_entry_location, R.id.activity_entry_weather,
            R.id.activity_entry_hashtag})
    public boolean onTouch(View v, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            lastX = event.getRawX();
            lastY = event.getRawY();
        }
        return false;
    }

    private void startPlaceChooserActivity() {
        PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();

        try {
            startActivityForResult(builder.build(this), REQUEST_PLACE_PICKER);
        } catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException e) {
            Logg.log(e);
        }
    }

    private void startImageChooserActivity() {
        Intent intent = new Intent(this, UriActivity.class);
        intent.putExtra(IntentUtils.TOUCH_X, lastX);
        intent.putExtra(IntentUtils.TOUCH_Y, lastY);
        intent.setData(mImageUri);
        startActivityForResult(intent, REQUEST_IMAGE);

        overridePendingTransition(0, 0);
    }
}
