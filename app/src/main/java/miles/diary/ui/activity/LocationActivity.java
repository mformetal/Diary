package miles.diary.ui.activity;

import android.Manifest;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceLikelihood;
import com.google.android.gms.location.places.ui.PlacePicker;

import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;
import miles.diary.R;
import miles.diary.data.adapter.AutoCompleteAdapter;
import miles.diary.data.api.GoogleService;
import miles.diary.data.model.google.AutoCompleteItem;
import miles.diary.data.rx.ActivitySubscriber;
import miles.diary.ui.widget.TypefaceAutoCompleteTextView;
import miles.diary.ui.widget.TypefaceButton;
import miles.diary.util.AnimUtils;
import miles.diary.util.Logg;

/**
 * Created by mbpeele on 2/6/16.
 */
public class LocationActivity extends TransitionActivity implements View.OnClickListener {

    private final static int REQUEST_LOCATION_PERMISSION = 1;
    private final static int REQUEST_PLACE_PICKER = 2;

    @Bind(R.id.activity_location_pos_button) TypefaceButton posButton;
    @Bind(R.id.activity_location_autocomplete) TypefaceAutoCompleteTextView autoCompleteTextView;
    @Bind(R.id.activity_location_image) ImageView mapIcon;

    private GoogleService googleService;
    private String placeName;
    private String placeId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);

        String[] permissions = new String[] {Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION};
        if (hasPermissions(permissions)) {
            if (hasConnection()) {
                Bundle bundle = getIntent().getExtras();
                if (bundle != null) {
                    placeName = bundle.getString(NewEntryActivity.PLACE_NAME);
                    placeId = bundle.getString(NewEntryActivity.PLACE_ID);

                    if (placeName != null) {
                        autoCompleteTextView.setText(placeName, false);
                    }
                }

                googleService = new GoogleService(this, googleApiClientBuilder, new GoogleService.GoogleServiceCallback() {
                    @Override
                    public void onConnected(Bundle bundle) {
                        final AutoCompleteAdapter autoCompleteAdapter = googleService.getAutoCompleteAdapter();
                        autoCompleteTextView.setAdapter(autoCompleteAdapter);

                        autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                final AutocompletePrediction item = autoCompleteAdapter.getItem(position);
                                placeId = item.getPlaceId();
                                placeName = item.getPrimaryText(null).toString();
                            }
                        });

                        getPlace();
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
    boolean overrideTransitions() {
        return false;
    }

    @Override
    void onEnter(ViewGroup root, Intent calledIntent, boolean hasSavedInstanceState) {
        if (getWindow().getSharedElementEnterTransition() != null) {
            AnimUtils.background(root, ContextCompat.getColor(this, R.color.accent), Color.WHITE)
                    .setDuration(500)
                    .start();
        }
    }

    @Override
    void onExit(ViewGroup root) {
        if (getWindow().getSharedElementEnterTransition() != null) {
            AnimUtils.background(root, Color.WHITE, ContextCompat.getColor(this, R.color.accent))
                    .setDuration(500)
                    .start();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_PLACE_PICKER:
                if (resultCode == RESULT_OK) {
                    Place place = PlacePicker.getPlace(this, data);
                    placeName = place.getName().toString();
                    placeId = place.getId();

                    autoCompleteTextView.setText(placeName, false);
                }
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
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
    @OnClick({R.id.activity_location_pos_button, R.id.activity_location_image})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.activity_location_pos_button:
                if (placeName != null && placeId != null) {
                    setReturnData();
                    finishAfterTransition();
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

    private void getPlace() {
        if (placeName == null && placeId == null) {
            googleService.getCurrentPlace(null)
                    .subscribe(new ActivitySubscriber<List<PlaceLikelihood>>(this) {
                        @Override
                        public void onNext(List<PlaceLikelihood> likelyPlaces) {
                            Place placeInfo = likelyPlaces.get(0).getPlace();
                            placeName = placeInfo.getName().toString();
                            placeId = placeInfo.getId();
                        }
                    });
        }
    }

    private void setReturnData() {
        Intent intent = new Intent();
        intent.putExtra(NewEntryActivity.PLACE_NAME, placeName);
        intent.putExtra(NewEntryActivity.PLACE_ID, placeId);
        setResult(RESULT_OK, intent);
    }
}
