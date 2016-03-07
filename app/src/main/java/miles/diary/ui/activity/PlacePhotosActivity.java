package miles.diary.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.Toolbar;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.PlacePhotoMetadata;
import com.google.android.gms.location.places.PlacePhotoMetadataResult;

import java.util.List;

import butterknife.Bind;
import miles.diary.R;
import miles.diary.data.adapter.PlacePhotosAdapter;
import miles.diary.data.api.google.GoogleService;
import miles.diary.data.model.google.PlaceInfo;
import miles.diary.data.rx.ActivitySubscriber;
import miles.diary.ui.widget.TypefaceTextView;
import miles.diary.util.Logg;
import rx.Observable;
import rx.functions.Func1;

/**
 * Created by mbpeele on 3/6/16.
 */
public class PlacePhotosActivity extends BaseActivity implements GoogleService.GoogleServiceCallback {

    public static final String ID = "placeId";
    public static final String NAME = "placeName";

    @Bind(R.id.activity_place_name_view)
    TypefaceTextView nameView;
    @Bind(R.id.activity_place_photos_pager)
    ViewPager pager;

    private PlacePhotosAdapter placePhotosAdapter;
    private GoogleService googleService;
    private String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_photos);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            String name = bundle.getString(NAME);
            id = bundle.getString(ID);
            nameView.setText(name);
        }

        googleService = new GoogleService(this, googleApiClientBuilder, this);
    }

    @Override
    public void onConnected(Bundle bundle, GoogleApiClient client, final BaseActivity activity) {
        googleService.getPlacePhotos(id)
                .subscribe(new ActivitySubscriber<PlacePhotoMetadataResult>(this) {
                    @Override
                    public void onNext(PlacePhotoMetadataResult result) {
                        if (result.getPhotoMetadata().getCount() > 0) {
                            placePhotosAdapter = new PlacePhotosAdapter(googleService, activity, result);
                            pager.setOffscreenPageLimit(2);
                            pager.setAdapter(placePhotosAdapter);
                        } else {
                            Snackbar.make(root,
                                    getString(R.string.activity_place_photos_empty),
                                    Snackbar.LENGTH_INDEFINITE)
                                    .setAction(android.R.string.ok, new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            finish();
                                        }
                                    })
                                    .show();
                        }
                    }
                });
    }
}
