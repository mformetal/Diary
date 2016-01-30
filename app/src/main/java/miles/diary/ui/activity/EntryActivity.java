package miles.diary.ui.activity;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
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

import java.io.File;

import butterknife.Bind;
import butterknife.OnClick;
import miles.diary.R;
import miles.diary.ui.widget.EntryFabMenu;
import miles.diary.util.Logg;
import miles.diary.util.PhotoFileUtils;

/**
 * Created by mbpeele on 1/16/16.
 */
public class EntryActivity extends BaseActivity implements View.OnClickListener,
        GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks {

    @Bind(R.id.activity_entry_toolbar) Toolbar toolbar;
    @Bind(R.id.activity_entry_fab_menu) EntryFabMenu menu;

    public static final String RESULT_TITLE = "title";
    public static final String RESULT_BODY = "body";
    public static final String RESULT_URI = "uri";
    private final static int REQUEST_CAMERA_PERMISSION = 1;
    private final static int REQUEST_LOCATION_PERMISSION = 2;
    private final static int REQUEST_PLACE_PICKER = 3;
    private final static int REQUEST_IMAGE = 4;

    private GoogleApiClient mGoogleApiClient;
    private Location mLocation;
    private Place mPlace;
    private Uri mImageUri;

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
                if (grantResults.length == 1 || grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                }
                break;
            case REQUEST_LOCATION_PERMISSION:
                if (grantResults.length == 1 || grantResults[0] == PackageManager.PERMISSION_GRANTED) {
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
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

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
        String perm;
        switch (v.getId()) {
            case R.id.activity_entry_photo:
                perm = Manifest.permission.CAMERA;
                if (ContextCompat.checkSelfPermission(this, perm) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, new String[]{perm}, REQUEST_CAMERA_PERMISSION);
                } else {
                    startImageChooserActivity();
                }
                break;
            case R.id.activity_entry_location:
                perm = Manifest.permission.ACCESS_COARSE_LOCATION;
                if (ContextCompat.checkSelfPermission(this, perm) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, new String[]{perm}, REQUEST_LOCATION_PERMISSION);
                } else {
                    startMapActivity();
                }
                break;
            case R.id.activity_entry_weather:
                break;
            case R.id.activity_entry_hashtag:
                break;
        }
    }

    private void startMapActivity() {
        PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();

        try {
            startActivityForResult(builder.build(this), REQUEST_PLACE_PICKER);
        } catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException e) {
            Logg.log(e);
        }
    }

    private void startImageChooserActivity() {
        startActivityForResult(new Intent(this, ImageChooserActivity.class), REQUEST_IMAGE);
    }
}
