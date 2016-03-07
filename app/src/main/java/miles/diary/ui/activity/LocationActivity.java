package miles.diary.ui.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.transition.Transition;
import android.transition.TransitionSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;

import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;
import miles.diary.R;
import miles.diary.data.adapter.AutoCompleteAdapter;
import miles.diary.data.model.google.LikelyPlace;
import miles.diary.data.model.google.PlaceInfo;
import miles.diary.data.rx.ActivitySubscriber;
import miles.diary.data.model.google.AutoCompleteItem;
import miles.diary.ui.PreDrawer;
import miles.diary.ui.transition.ColorTransition;
import miles.diary.ui.widget.TypefaceAutoCompleteTextView;
import miles.diary.ui.widget.TypefaceButton;
import miles.diary.util.AnimUtils;
import miles.diary.data.api.google.GoogleService;
import miles.diary.util.Logg;

/**
 * Created by mbpeele on 2/6/16.
 */
public class LocationActivity extends BaseActivity implements View.OnClickListener {

    private final static int REQUEST_LOCATION_PERMISSION = 1;
    private final static int REQUEST_PLACE_PICKER = 2;

    @Bind(R.id.activity_location_pos_button) TypefaceButton posButton;
    @Bind(R.id.activity_location_autocomplete) TypefaceAutoCompleteTextView autoCompleteTextView;
    @Bind(R.id.activity_location_image) ImageView mapIcon;

    private GoogleService googleService;
    private ArrayAdapter<AutoCompleteItem> autoCompleteAdapter;
    String placeName;
    String placeId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);
        transitionSetup();

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
                    public void onConnected(Bundle bundle, GoogleApiClient client, BaseActivity activity) {
                        autoCompleteAdapter = new AutoCompleteAdapter(activity,
                                R.layout.autocomplete_adapter, client, null, null);
                        autoCompleteTextView.setAdapter(autoCompleteAdapter);

                        autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                final AutoCompleteItem item = autoCompleteAdapter.getItem(position);
                                placeId = String.valueOf(item.placeId);
                                placeName = String.valueOf(item.description);
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
                    .subscribe(new ActivitySubscriber<List<LikelyPlace>>(this) {
                        @Override
                        public void onNext(List<LikelyPlace> likelyPlaces) {
                            PlaceInfo placeInfo = likelyPlaces.get(0).getPlaceInfo();
                            placeName = placeInfo.getName();
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

    private void transitionSetup() {
        PreDrawer.addPreDrawer(root, new PreDrawer.OnPreDrawListener<ViewGroup>() {
            @Override
            public boolean onPreDraw(ViewGroup view) {
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

                    Context context = view.getContext();
                    AnimUtils.colorFilter(mapIcon.getDrawable(),
                            Color.WHITE, ContextCompat.getColor(context, R.color.accent)).start();
                }
                return true;
            }
        });


        Transition returnTransition = getWindow().getSharedElementReturnTransition();

        TransitionSet set = new TransitionSet();
        set.setOrdering(TransitionSet.ORDERING_TOGETHER);
        set.addTransition(returnTransition);
        ColorTransition colorTransition = new ColorTransition(
                ContextCompat.getColor(this, R.color.accent), Color.WHITE);
        colorTransition.addTarget(mapIcon);
        set.addTransition(colorTransition);

        getWindow().setSharedElementReturnTransition(set);
    }
}
