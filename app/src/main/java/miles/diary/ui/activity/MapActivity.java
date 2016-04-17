package miles.diary.ui.activity;

import android.os.Bundle;
import android.util.Pair;
import android.view.MenuItem;
import android.widget.Toolbar;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import miles.diary.R;
import miles.diary.data.model.realm.Entry;
import miles.diary.data.rx.ActivitySubscriber;
import miles.diary.ui.fragment.ConfirmationDialog;
import miles.diary.ui.fragment.DismissingDialogFragment;
import miles.diary.util.Logg;
import rx.Observable;
import rx.functions.Func1;

/**
 * Created by mbpeele on 3/13/16.
 */
public class MapActivity extends BaseActivity implements OnMapReadyCallback {

    @Bind(R.id.activity_map_toolbar)
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        setActionBar(toolbar);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setDisplayShowHomeEnabled(true);

        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.activity_map_fragment);
        mapFragment.getMapAsync(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        dataManager.getAll(Entry.class)
                .subscribe(new ActivitySubscriber<List<Entry>>(this) {
                    @Override
                    public void onNext(List<Entry> entries) {
                        if (entries.isEmpty()) {
                            onLoadEmpty();
                        } else {
                            createInfoWindows(googleMap, entries);
                        }
                    }
                });

    }

    public void onLoadEmpty() {
        ConfirmationDialog dialog =
                ConfirmationDialog.newInstance(getString(R.string.activity_map_empty));
        dialog.setDismissListener(new DismissingDialogFragment.OnDismissListener() {
            @Override
            public void onDismiss(DismissingDialogFragment fragment) {
                finish();
            }
        });
        dialog.show(getFragmentManager(), CONFIRMATION_DIALOG);
    }

    private void createInfoWindows(GoogleMap googleMap, List<Entry> entries) {
        boolean hasLocation = false;

        Entry mostRecent = null;
        MarkerOptions mostRecentMarkerOptions = null;

        for (Entry entry: entries) {
            if (entry.hasLocation()) {
                hasLocation = true;

                // Do something different with entries that have images?
                if (entry.hasImageUri()) {
                } else {
                }

                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(entry.getLatLng());
                markerOptions.title(entry.getBody());
                googleMap.addMarker(markerOptions);

                if (mostRecent == null) {
                    mostRecent = entry;
                    mostRecentMarkerOptions = markerOptions;
                } else {
                    int compare = entry.getDate().compareTo(mostRecent.getDate());
                    if (compare > 0) { // This Entry's Date is greater than mostRecent's date
                        mostRecent = entry;
                        mostRecentMarkerOptions = markerOptions;
                    }
                }
            }
        }

        if (!hasLocation) {
            onLoadEmpty();
        } else {
            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(mostRecentMarkerOptions.getPosition())
                    .zoom(17)
                    .bearing(45)
                    .tilt(45)
                    .build();
            googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        }
    }
}
