package miles.diary.data.api;

import android.content.IntentSender;
import android.location.Address;
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
import com.google.android.gms.location.places.PlaceLikelihood;
import com.google.android.gms.location.places.PlaceLikelihoodBuffer;
import com.google.android.gms.location.places.PlacePhotoMetadata;
import com.google.android.gms.location.places.PlacePhotoMetadataResult;
import com.google.android.gms.location.places.PlacePhotoResult;
import com.google.android.gms.location.places.Places;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import miles.diary.R;
import miles.diary.data.adapter.AutoCompleteAdapter;
import miles.diary.data.model.google.PlaceResponse;
import miles.diary.data.rx.GoogleObservable;
import miles.diary.data.rx.OkHttpObservable;
import miles.diary.ui.activity.BaseActivity;
import miles.diary.util.LocationUtils;
import miles.diary.util.Logg;
import miles.diary.util.SimpleLocationListener;
import miles.diary.util.URLFormatter;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by mbpeele on 2/21/16.
 */
public class Google implements GoogleApiClient.ConnectionCallbacks,
    GoogleApiClient.OnConnectionFailedListener {

    private final GoogleApiClient client;
    private final BaseActivity activity;
    private GoogleServiceCallback callback;
    private OkHttpClient okHttpClient;
    private Gson gson;

    private static int FAILED_CODE = 5;

    public Google(final BaseActivity activity1, GoogleApiClient.Builder builder,
                  GoogleServiceCallback googleServiceCallback) {
        activity = activity1;

        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        httpClient.addInterceptor(logging);
        okHttpClient = httpClient.build();

        gson = new Gson();

        client = builder.addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        client.connect();

        callback = googleServiceCallback;
    }

    public void cleanup() {
        client.disconnect();
    }

    public AutoCompleteAdapter getAutoCompleteAdapter() {
        return new AutoCompleteAdapter(activity, R.layout.autocomplete_adapter, client, null);
    }

    public Observable<PlaceResponse> searchNearby(Location location, float radius) {
        String url = URLFormatter.nearbySearch(activity, location, radius);

        OkHttpObservable<PlaceResponse> okHttpObservable = OkHttpObservable.<PlaceResponse>builder()
                .target(PlaceResponse.class)
                .url(url)
                .gson(gson)
                .build();

        return okHttpObservable.execute(okHttpClient)
                .retry(1)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @SuppressWarnings({"ResourceType"})
    public Observable<List<PlaceLikelihood>> getCurrentPlace(PlaceFilter placeFilter) {
        return GoogleObservable.execute(Places.PlaceDetectionApi.getCurrentPlace(client, placeFilter))
                .map(new Func1<PlaceLikelihoodBuffer, List<PlaceLikelihood>>() {
                    @Override
                    public List<PlaceLikelihood> call(PlaceLikelihoodBuffer placeLikelihoods) {
                        List<PlaceLikelihood> list = new ArrayList<PlaceLikelihood>();
                        for (PlaceLikelihood placeLikelihood : placeLikelihoods) {
                            list.add(placeLikelihood);
                        }
                        placeLikelihoods.release();
                        return list;
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
                    final LocationManager locationManager = LocationUtils.getLocationManager(activity);

                    SimpleLocationListener simpleLocationListener = new SimpleLocationListener() {
                        @Override
                        public void onLocationChanged(Location location) {
                            locationManager.removeUpdates(this);
                            subscriber.onNext(location);
                        }
                    };

                    LocationUtils.getLocationUpdates(activity, simpleLocationListener);
                }

                subscriber.onCompleted();
            }
        });
    }

    public Observable<List<Address>> getAddressFromLocation(Location location, int results) {
        return LocationUtils.geocode(activity, location, results)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<PlacePhotoMetadataResult> getPlacePhotos(final String placeId) {
        return GoogleObservable.execute(Places.GeoDataApi.getPlacePhotos(client, placeId))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<PlacePhotoResult> getPlacePhoto(PlacePhotoMetadata metadata) {
        return GoogleObservable.execute(metadata.getPhoto(client))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<PlacePhotoResult> getScaledPhoto(PlacePhotoMetadata metadata, int w, int h) {
        return GoogleObservable.execute(metadata.getScaledPhoto(client, w, h))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<Place> getPlaceById(final String placeId) {
        return GoogleObservable.execute(Places.GeoDataApi.getPlaceById(client, placeId))
                .map(new Func1<PlaceBuffer, Place>() {
                    @Override
                    public Place call(PlaceBuffer placeBuffer) {
                        Place place = placeBuffer.get(0);
                        place.freeze();
                        placeBuffer.release();
                        return place;
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
                callback.onConnected(bundle);
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
                    connectionResult.startResolutionForResult(activity, FAILED_CODE);
                } catch (IntentSender.SendIntentException e) {
                    e.printStackTrace();
                }
            } else {
                Logg.log("CONNECTION FAILED WITH CODE: " + connectionResult.getErrorCode());
            }
        }
    }

    public interface GoogleServiceCallback {

        void onConnected(Bundle bundle);
    }
}
