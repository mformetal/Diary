package miles.diary.data.api.google;

import android.content.IntentSender;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.PlaceFilter;
import com.google.android.gms.location.places.PlaceLikelihoodBuffer;
import com.google.android.gms.location.places.PlacePhotoMetadata;
import com.google.android.gms.location.places.PlacePhotoMetadataResult;
import com.google.android.gms.location.places.PlacePhotoResult;
import com.google.android.gms.location.places.Places;

import java.util.List;

import miles.diary.R;
import miles.diary.data.api.LocationService;
import miles.diary.data.model.google.LikelyPlace;
import miles.diary.data.model.google.PlaceInfo;
import miles.diary.data.model.google.PlaceResponse;
import miles.diary.data.rx.GoogleResultObservable;
import miles.diary.ui.activity.BaseActivity;
import miles.diary.util.IntentUtils;
import miles.diary.util.Logg;
import miles.diary.util.SimpleLocationListener;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by mbpeele on 2/21/16.
 */
public class GoogleService implements GoogleApiClient.ConnectionCallbacks,
    GoogleApiClient.OnConnectionFailedListener {

    private final GoogleApiClient client;
    private final BaseActivity activity;
    private GoogleServiceCallback callback;
    private MapsAPI mapsService;

    public GoogleService(final BaseActivity activity1, GoogleApiClient.Builder builder,
                         GoogleServiceCallback googleServiceCallback) {
        activity = activity1;

        client = builder
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        client.connect();

        callback = googleServiceCallback;
    }

    public void cleanup() {
        client.disconnect();
    }

    public Observable<PlaceResponse> searchNearby(Location location, float radius) {
        if (mapsService == null) {
            Retrofit builder = new Retrofit.Builder()
                    .baseUrl(activity.getString(R.string.maps_url))
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                    .build();

            mapsService = builder.create(MapsAPI.class);
        }

        String string = location.getLatitude() + "," + location.getLongitude();
        return mapsService.searchNearby(string, radius,
                activity.getString(R.string.google_web_api_key))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @SuppressWarnings({"ResourceType"})
    public Observable<List<LikelyPlace>> getCurrentPlace(PlaceFilter placeFilter) {
        return Observable.create(new GoogleResultObservable<PlaceLikelihoodBuffer>(
                Places.PlaceDetectionApi.getCurrentPlace(client, placeFilter)))
                .map(new Func1<PlaceLikelihoodBuffer, List<LikelyPlace>>() {
                    @Override
                    public List<LikelyPlace> call(PlaceLikelihoodBuffer placeLikelihoods) {
                        return LikelyPlace.fromBuffer(placeLikelihoods);
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @SuppressWarnings({"ResourceType"})
    public Observable<Location> getLocation() {
        return Observable.create(new Observable.OnSubscribe<Location>() {
            @Override
            public void call(final Subscriber<? super Location> subscriber) {
                subscriber.onStart();

                Location location = LocationServices.FusedLocationApi.getLastLocation(client);
                if (location != null) {
                    subscriber.onNext(location);
                } else {
                    final LocationManager locationManager = LocationService.getLocationManager(activity);

                    SimpleLocationListener simpleLocationListener = new SimpleLocationListener() {
                        @Override
                        public void onLocationChanged(Location location) {
                            locationManager.removeUpdates(this);
                            subscriber.onNext(location);
                        }
                    };

                    LocationService.getLocationUpdates(activity, simpleLocationListener);
                }

                subscriber.onCompleted();
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<PlacePhotoMetadataResult> getPlacePhotos(final String placeId) {
        final String test = "ChIJrTLr-GyuEmsRBfy61i59si0";
        return Observable.create(new GoogleResultObservable<>(
                Places.GeoDataApi.getPlacePhotos(client, placeId)))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<PlacePhotoResult> getPlacePhoto(PlacePhotoMetadata metadata) {
        return Observable.create(new GoogleResultObservable<PlacePhotoResult>(metadata.getPhoto(client)))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<PlacePhotoResult> getScaledPhoto(PlacePhotoMetadata metadata, int w, int h) {
        return Observable.create(new GoogleResultObservable<PlacePhotoResult>(metadata.getScaledPhoto(client, w, h)))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<PlaceInfo> getPlaceById(final String placeId) {
        return Observable.create(new GoogleResultObservable<>(Places.GeoDataApi.getPlaceById(client, placeId)))
                .map(new Func1<PlaceBuffer, PlaceInfo>() {
                    @Override
                    public PlaceInfo call(PlaceBuffer places) {
                        Place place = places.get(0);
                        PlaceInfo info = new PlaceInfo(place);
                        places.release();
                        return info;
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    private boolean checkActivity() {
        return activity != null && !activity.isFinishing();
    }

    @Override
    public void onConnected(Bundle bundle) {
        if (checkActivity()) {
            if (callback != null) {
                callback.onConnected(bundle, client, activity);
            }
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        if (checkActivity()) {
            if (connectionResult.hasResolution()) {
                try {
                    connectionResult.startResolutionForResult(activity, IntentUtils.GOOGLE_API_CLIENT_FAILED_CODE);
                } catch (IntentSender.SendIntentException e) {
                    e.printStackTrace();
                }
            } else {
                Logg.log("CONNECTION FAILED WITH CODE: " + connectionResult.getErrorCode());
            }
        }
    }

    public interface GoogleServiceCallback {

        void onConnected(Bundle bundle, GoogleApiClient client, BaseActivity activity);
    }
}
