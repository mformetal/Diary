package miles.diary.util;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.PlaceFilter;
import com.google.android.gms.location.places.PlaceLikelihoodBuffer;
import com.google.android.gms.location.places.PlacePhotoMetadata;
import com.google.android.gms.location.places.PlacePhotoMetadataBuffer;
import com.google.android.gms.location.places.PlacePhotoMetadataResult;
import com.google.android.gms.location.places.PlacePhotoResult;
import com.google.android.gms.location.places.Places;

import java.io.IOException;
import java.util.List;

import pl.charmas.android.reactivelocation.observables.PendingResultObservable;
import pl.charmas.android.reactivelocation.observables.location.LastKnownLocationObservable;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.functions.Func2;
import rx.schedulers.Schedulers;

/**
 * Created by mbpeele on 2/21/16.
 */
public class GoogleUtils {

    private GoogleUtils() {}

    public static Observable<PlacePhotoResult> getPlacePhoto(final String placeId,
                                                             final GoogleApiClient apiClient) {
        return Observable.create(new PendingResultObservable<>(
                Places.GeoDataApi.getPlacePhotos(apiClient, placeId)))
                .filter(new Func1<PlacePhotoMetadataResult, Boolean>() {
                    @Override
                    public Boolean call(PlacePhotoMetadataResult placePhotoMetadataResult) {
                        return placePhotoMetadataResult.getPhotoMetadata().getCount() > 0;
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .flatMap(new Func1<PlacePhotoMetadataResult, Observable<PlacePhotoResult>>() {
                    @Override
                    public Observable<PlacePhotoResult> call(PlacePhotoMetadataResult placePhotoMetadataResult) {
                        PlacePhotoMetadataBuffer buffer = placePhotoMetadataResult.getPhotoMetadata();
                        PlacePhotoMetadata photoMetadata = buffer.get(0);
                        Observable<PlacePhotoResult> observable =
                                Observable.create(new PendingResultObservable<>(
                                        photoMetadata.getPhoto(apiClient)));
                        buffer.release();
                        return observable;
                    }
                });
    }

    public static Observable<PlaceLikelihoodBuffer> getCurrentPlace(GoogleApiClient apiClient, PlaceFilter placeFilter) {
        return Observable.create(
                new PendingResultObservable<>(
                        Places.PlaceDetectionApi.getCurrentPlace(apiClient, placeFilter))
        ).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }

    public static Observable<PlaceBuffer> getPlaceById(GoogleApiClient apiClient, String placeId) {
        return Observable.create(
                new PendingResultObservable<>(
                        Places.GeoDataApi.getPlaceById(apiClient, placeId)
        )).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }

    public static Observable<Location> getLocation(Context context) {
        return LastKnownLocationObservable.createObservable(context);
    }

    public static Observable<List<Address>> getAddress(Context context, Location location) {
        try {
            return Observable.just(new Geocoder(context).getFromLocation(location.getLatitude(),
                    location.getLongitude(), 1))
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread());
        } catch (IOException e) {
            Logg.log(e);
            return Observable.error(e);
        }
    }
}
