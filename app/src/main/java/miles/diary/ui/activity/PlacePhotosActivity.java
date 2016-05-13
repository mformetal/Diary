package miles.diary.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;

import com.google.android.gms.location.places.PlacePhotoMetadataBuffer;
import com.google.android.gms.location.places.PlacePhotoMetadataResult;

import javax.inject.Inject;

import butterknife.Bind;
import icepick.State;
import miles.diary.DiaryApplication;
import miles.diary.R;
import miles.diary.data.adapter.PlacePhotosAdapter;
import miles.diary.data.api.Google;
import miles.diary.data.rx.ActivitySubscriber;
import miles.diary.ui.fragment.DismissingDialogFragment;
import miles.diary.ui.fragment.ConfirmationDialog;
import miles.diary.ui.widget.TypefaceTextView;
import rx.functions.Action1;

/**
 * Created by mbpeele on 3/6/16.
 */
public class PlacePhotosActivity extends BaseActivity implements Google.GoogleCallback {

    public static final String ID = "placeId";
    public static final String NAME = "placeName";

    @Bind(R.id.activity_place_name_view)
    TypefaceTextView nameView;
    @Bind(R.id.activity_place_photos_pager)
    ViewPager pager;

    private PlacePhotosAdapter placePhotosAdapter;
    @State String id;

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

        google.setActivity(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        google.connect(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        google.disconnect();
    }

    @Override
    protected void onDestroy() {
        if (placePhotosAdapter != null) {
            placePhotosAdapter.release();
        }
        super.onDestroy();
    }

    @Override
    public void onConnected(Bundle bundle) {
        placePhotosAdapter = new PlacePhotosAdapter(google, this);
        pager.setOffscreenPageLimit(2);
        pager.setAdapter(placePhotosAdapter);

        google.getPlacePhotos(id)
                .doOnError(new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        onErrorOrEmpty();
                    }
                })
                .subscribe(new ActivitySubscriber<PlacePhotoMetadataResult>(this) {
                    @Override
                    public void onNext(PlacePhotoMetadataResult result) {
                        PlacePhotoMetadataBuffer buffer = result.getPhotoMetadata();
                        if (buffer != null && buffer.getCount() > 0) {
                            pager.setAdapter(null);
                            placePhotosAdapter.setBuffer(buffer);
                            pager.setAdapter(placePhotosAdapter);
                        } else {
                            onErrorOrEmpty();
                        }
                    }
                });
    }

    private void onErrorOrEmpty() {
        ConfirmationDialog dialog =
                ConfirmationDialog.newInstance(getString(R.string.place_photos_empty));
        dialog.setDismissListener(new DismissingDialogFragment.OnDismissListener() {
            @Override
            public void onDismiss(DismissingDialogFragment fragment) {
                finish();
            }
        });
        dialog.show(getFragmentManager(), CONFIRMATION_DIALOG);
    }
}
