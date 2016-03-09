package miles.diary.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.widget.ProgressBar;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.PlacePhotoMetadataBuffer;
import com.google.android.gms.location.places.PlacePhotoMetadataResult;

import butterknife.Bind;
import miles.diary.R;
import miles.diary.data.adapter.PlacePhotosAdapter;
import miles.diary.data.api.GoogleService;
import miles.diary.data.rx.ActivitySubscriber;
import miles.diary.ui.fragment.BaseDialogFragment;
import miles.diary.ui.fragment.ConfirmationDialog;
import miles.diary.ui.widget.TypefaceTextView;
import miles.diary.util.AnimUtils;
import rx.functions.Action1;

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
    @Bind(R.id.activity_place_photos_progressbar)
    ProgressBar progressBar;

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
                .doOnError(new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        onErrorOrEmpty();
                    }
                })
                .subscribe(new ActivitySubscriber<PlacePhotoMetadataResult>(this) {
                    @Override
                    public void onNext(PlacePhotoMetadataResult result) {
                        AnimUtils.gone(progressBar).start();

                        PlacePhotoMetadataBuffer buffer = result.getPhotoMetadata();
                        if (buffer != null && buffer.getCount() > 0) {
                            placePhotosAdapter = new PlacePhotosAdapter(googleService, activity, result);
                            pager.setOffscreenPageLimit(2);
                            pager.setAdapter(placePhotosAdapter);
                        } else {
                            onErrorOrEmpty();
                        }
                    }
                });
    }

    private void onErrorOrEmpty() {
        ConfirmationDialog dialog =
                ConfirmationDialog.newInstance(getString(R.string.activity_place_photos_empty));
        dialog.setDismissListener(new BaseDialogFragment.OnDismissListener() {
            @Override
            public void onDismiss(BaseDialogFragment fragment) {
                finish();
            }
        });
        dialog.show(getFragmentManager(), CONFIRMATION_DIALOG);
    }
}
