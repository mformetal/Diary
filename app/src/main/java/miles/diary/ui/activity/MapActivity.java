package miles.diary.ui.activity;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toolbar;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;

import java.util.List;

import butterknife.Bind;
import miles.diary.R;
import miles.diary.data.model.realm.Entry;
import miles.diary.data.rx.ActivitySubscriber;
import miles.diary.ui.fragment.ConfirmationDialog;
import miles.diary.ui.fragment.DismissingDialogFragment;

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

        for (Entry entry: entries) {
            if (entry.getLatitude() != null &&
                    entry.getLongitude() != null) {
                hasLocation = true;

                // add stuff
            }
        }

        if (!hasLocation) {
            onLoadEmpty();
        }
    }
}
