package miles.diary.ui.activity;

import android.Manifest;
import android.app.ActivityOptions;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.VectorDrawable;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.transition.Transition;
import android.transition.TransitionSet;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceLikelihoodBuffer;
import com.google.android.gms.location.places.ui.PlacePicker;

import butterknife.Bind;
import butterknife.OnClick;
import icepick.State;
import miles.diary.R;
import miles.diary.data.ActivitySubscriber;
import miles.diary.data.adapter.AutoCompleteAdapter;
import miles.diary.ui.PreDrawer;
import miles.diary.ui.transition.ColorTransition;
import miles.diary.ui.widget.TypefaceAutoCompleteTextView;
import miles.diary.ui.widget.TypefaceButton;
import miles.diary.util.AnimUtils;
import miles.diary.util.GoogleUtils;
import miles.diary.util.IntentUtils;
import miles.diary.util.Logg;

/**
 * Created by mbpeele on 2/6/16.
 */
public class LocationActivity extends BaseActivity
        implements View.OnClickListener,
        GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks {

    private final static int REQUEST_LOCATION_PERMISSION = 1;
    private final static int REQUEST_PLACE_PICKER = 2;

    @Bind(R.id.activity_location_pos_button) TypefaceButton posButton;
    @Bind(R.id.activity_location_autocomplete) TypefaceAutoCompleteTextView autoCompleteTextView;
    @Bind(R.id.activity_location_image) ImageView locationImage;

    private GoogleApiClient googleApiClient;
    @State String locationName;
    @State String locationId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);

        if (savedInstanceState == null) {
            transitionSetup();
        }

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            locationName = bundle.getString(NewEntryActivity.PLACE_NAME);
            locationId = bundle.getString(NewEntryActivity.PLACE_ID);

            if (locationName != null) {
                autoCompleteTextView.setText(locationName, false);
            }
        }

        googleApiClient = googleApiClientBuilder
                .enableAutoManage(this, 0, this)
                .addConnectionCallbacks(this)
                .build();

        final AutoCompleteAdapter autoCompleteAdapter =
                new AutoCompleteAdapter(this, R.layout.autocomplete_adapter,
                        googleApiClient, null);
        autoCompleteTextView.setAdapter(autoCompleteAdapter);

        autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final AutoCompleteAdapter.AutoCompleteAdapterItem item =
                        autoCompleteAdapter.getItem(position);
                locationId = String.valueOf(item.placeId);
                locationName = String.valueOf(item.description);
            }
        });
    }

    @Override
    public void onBackPressed() {
        setReturnData();
        super.onBackPressed();
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
                if (permissionsGranted(grantResults)) {
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
    @OnClick({R.id.activity_location_pos_button, R.id.activity_location_image})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.activity_location_pos_button:
                if (locationName != null && locationId != null) {
                    setReturnData();
                    onBackPressed();
                } else {
                    Snackbar.make(root, R.string.activity_location_no_input,
                            Snackbar.LENGTH_SHORT).show();
                }
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
        String[] permissions = new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION};
        if (hasPermissions(permissions)) {
            if (hasConnection()) {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }

                Location loc = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
                if (loc != null) {
                    getPlace();
                }
            } else {
                noInternet();
            }
        } else {
            ActivityCompat.requestPermissions(this, permissions, REQUEST_LOCATION_PERMISSION);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {}

    private void getPlace() {
        if (locationName == null && locationId == null) {
            GoogleUtils.getCurrentPlace(googleApiClient, null)
                    .subscribe(new ActivitySubscriber<PlaceLikelihoodBuffer>(this) {
                        @Override
                        public void onNext(PlaceLikelihoodBuffer placeLikelihoods) {
                            Place mostLikely = placeLikelihoods.get(0).getPlace();

                            locationId = mostLikely.getId();
                            locationName = mostLikely.getName().toString();

                            autoCompleteTextView.setText(locationName, false);

                            placeLikelihoods.release();
                        }
                    });
        }
    }

    private void setReturnData() {
        Intent intent = new Intent();
        intent.putExtra(NewEntryActivity.PLACE_NAME, locationName);
        intent.putExtra(NewEntryActivity.PLACE_ID, locationId);
        if (!(locationImage.getDrawable() instanceof VectorDrawable) &&
                getWindow().getSharedElementReturnTransition() == null) {
            ActivityOptions transitionActivityOptions =
                    ActivityOptions.makeSceneTransitionAnimation(this, locationImage,
                            getString(R.string.transition_location_image));
            intent.putExtras(transitionActivityOptions.toBundle());
        }
        setResult(RESULT_OK, intent);
    }

    private void transitionSetup() {
        new PreDrawer<View>(root) {
            @Override
            public void notifyPreDraw(View view) {
                float offset = root.getHeight() / 4;
                for (int i = 0; i < root.getChildCount(); i++) {
                    View v = root.getChildAt(i);
                    v.setTranslationY(offset);
                    v.setAlpha(0f);
                    v.animate()
                            .alpha(1f)
                            .translationY(0f)
                            .setDuration(AnimUtils.mediumAnim(LocationActivity.this))
                            .setStartDelay(150)
                            .setInterpolator(new FastOutSlowInInterpolator());

                    offset *= 1.8f;
                }
            }
        };

        Transition returnTransition = getWindow().getSharedElementReturnTransition();

        if (locationName != null && locationId != null) {
            TransitionSet set = new TransitionSet();
            set.addTransition(returnTransition);
            ColorTransition colorTransition = new ColorTransition(
                    ContextCompat.getColor(this, R.color.accent), Color.WHITE);
            colorTransition.addTarget(locationImage);
            set.addTransition(colorTransition);

            getWindow().setSharedElementReturnTransition(set);
        }
    }
}
