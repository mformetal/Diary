package miles.diary.ui.activity;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;
import miles.diary.DiaryApplication;
import miles.diary.R;
import miles.diary.data.api.Repository;
import miles.diary.data.model.realm.Entry;
import miles.diary.data.rx.ActivitySubscriber;
import miles.diary.ui.fragment.ConfirmationDialog;
import miles.diary.ui.fragment.DismissingDialogFragment;

/**
 * Created by mbpeele on 3/13/16.
 */
public class MapActivity extends BaseActivity implements OnMapReadyCallback {

    @Inject
    Repository repository;

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
    public void onMapReady(final GoogleMap googleMap) {
        repository.getAll(Entry.class)
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
                ConfirmationDialog.newInstance(getString(R.string.map_entries_empty));
        dialog.setDismissListener(new DismissingDialogFragment.OnDismissListener() {
            @Override
            public void onDismiss(DismissingDialogFragment fragment) {
                finish();
            }
        });
        dialog.show(getFragmentManager(), CONFIRMATION_DIALOG);
    }

    private void createInfoWindows(final GoogleMap googleMap, List<Entry> entries) {
        boolean hasLocation = false;

        LatLngBounds.Builder builder = new LatLngBounds.Builder();

        for (Entry entry: entries) {
            if (entry.hasLocation()) {
                hasLocation = true;

                builder.include(entry.getPosition());

                final MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.title(entry.getBody());
                markerOptions.position(entry.getPosition());
                if (entry.hasImageUri()) {
                    Glide.with(this)
                            .load(entry.getUri())
                            .asBitmap()
                            .into(new SimpleTarget<Bitmap>() {
                                @Override
                                public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                                    markerOptions.icon(BitmapDescriptorFactory.fromBitmap(resource));
                                    googleMap.addMarker(markerOptions);
                                }
                            });
                } else {
                    googleMap.addMarker(markerOptions);
                }
            }
        }

        if (!hasLocation) {
            onLoadEmpty();
        } else {
            googleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), 500));
        }
    }
}
