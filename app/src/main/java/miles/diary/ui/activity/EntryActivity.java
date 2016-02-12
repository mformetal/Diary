package miles.diary.ui.activity;

import android.animation.ObjectAnimator;
import android.content.IntentSender;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.support.v7.graphics.Palette;
import android.transition.Transition;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.GeoDataApi;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.PlaceDetectionApi;
import com.google.android.gms.location.places.Places;

import butterknife.Bind;
import miles.diary.R;
import miles.diary.data.model.Entry;
import miles.diary.ui.SimpleTransitionListener;
import miles.diary.ui.widget.CornerImageView;
import miles.diary.ui.widget.TypefaceTextView;
import miles.diary.util.AnimUtils;
import miles.diary.util.IntentUtils;
import miles.diary.util.Logg;

/**
 * Created by mbpeele on 2/8/16.
 */
public class EntryActivity extends BaseActivity
        implements GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks {

    public final static String DATA = "data";

    @Bind(R.id.activity_entry_body) TypefaceTextView body;
    @Bind(R.id.activity_entry_image) CornerImageView imageView;
    @Bind(R.id.activity_entry_time) TypefaceTextView time;

    private Entry entry;
    private GoogleApiClient googleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entry);
        setupTransitions();

        entry = realm.where(Entry.class)
                .equalTo(Entry.KEY, getIntent().getStringExtra(DATA))
                .findFirst();

        googleApiClient = googleApiClientBuilder
                .enableAutoManage(this, 0, this)
                .addConnectionCallbacks(this)
                .build();

        body.setText(entry.getBody());
        time.setText(Entry.formatDateString(entry));
        String placeId = entry.getPlaceId();

        if (entry.getUri() != null) {
            Glide.with(EntryActivity.this)
                    .fromString()
                    .asBitmap()
                    .load(entry.getUri())
                    .dontAnimate()
                    .listener(new RequestListener<String, Bitmap>() {
                        @Override
                        public boolean onException(Exception e, String model, Target<Bitmap> target, boolean isFirstResource) {
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Bitmap resource, String model, Target<Bitmap> target,
                                                       boolean isFromMemoryCache, boolean isFirstResource) {
                            Palette.from(resource).generate(palette -> {
//                                Palette.Swatch swatch = palette.getLightVibrantSwatch();
//                                body.setTextColor(swatch.getTitleTextColor());
//                                body.setHighlightColor(swatch.getRgb());
                            });
                            return false;
                        }
                    })
                    .into(imageView);
        }
    }

    private void setupTransitions() {
        Transition enterTransition = getWindow().getSharedElementEnterTransition();
        Transition returnTransition = getWindow().getSharedElementReturnTransition();

        if (enterTransition != null && returnTransition != null) {
            enterTransition.addListener(new SimpleTransitionListener() {
                @Override
                public void onTransitionStart(Transition transition) {
                    super.onTransitionStart(transition);
                    ObjectAnimator corner = ObjectAnimator.ofFloat(imageView,
                            CornerImageView.CORNERS,
                            Math.max(imageView.getWidth(), imageView.getHeight()) / 2f, 0);
                    corner.setDuration(AnimUtils.longAnim(getApplicationContext()));
                    corner.setInterpolator(new FastOutSlowInInterpolator());
                    corner.start();
                }
            });

            returnTransition.addListener(new SimpleTransitionListener() {
                @Override
                public void onTransitionStart(Transition transition) {
                    super.onTransitionEnd(transition);

                    ObjectAnimator corner = ObjectAnimator.ofFloat(imageView,
                            CornerImageView.CORNERS,
                            0, Math.min(imageView.getWidth(), imageView.getHeight()) / 2f);
                    corner.setDuration(AnimUtils.longAnim(getApplicationContext()));
                    corner.setInterpolator(new FastOutSlowInInterpolator());
                    corner.start();
                }
            });
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
    public void onConnected(Bundle bundle) {
        if (hasConnection()) {
            String placeId = entry.getPlaceId();
            if (placeId != null) {
                Places.GeoDataApi.getPlaceById(googleApiClient, placeId)
                        .setResultCallback(new ResultCallback<PlaceBuffer>() {
                            @Override
                            public void onResult(@NonNull PlaceBuffer places) {

                            }
                        });
            }
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }
}
