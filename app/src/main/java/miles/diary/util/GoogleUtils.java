package miles.diary.util;

import android.content.Context;
import android.location.Location;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.PlaceFilter;
import com.google.android.gms.location.places.PlaceLikelihoodBuffer;
import com.google.android.gms.location.places.PlacePhotoMetadata;
import com.google.android.gms.location.places.PlacePhotoMetadataBuffer;
import com.google.android.gms.location.places.PlacePhotoResult;
import com.google.android.gms.location.places.Places;

import pl.charmas.android.reactivelocation.observables.PendingResultObservable;
import pl.charmas.android.reactivelocation.observables.location.LastKnownLocationObservable;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func2;
import rx.schedulers.Schedulers;

/**
 * Created by mbpeele on 2/21/16.
 */
public class GoogleUtils {

    public static Observable<PlacePhotoResult> getPlacePhoto(String placeId, GoogleApiClient apiClient) {
        return Observable.create(new PendingResultObservable<>(
                Places.GeoDataApi.getPlacePhotos(apiClient, placeId)))
                .retry()
                .filter(placePhotoMetadataResult -> placePhotoMetadataResult.getPhotoMetadata().getCount() > 0)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .flatMap(placePhotoMetadataResult -> {
                    PlacePhotoMetadataBuffer buffer = placePhotoMetadataResult.getPhotoMetadata();
                    PlacePhotoMetadata photoMetadata = buffer.get(0);
                    Observable<PlacePhotoResult> observable =
                            Observable.create(new PendingResultObservable<>(
                                            photoMetadata.getPhoto(apiClient)));
                    buffer.release();
                    return observable;
                })
                .filter(placePhotoResult -> placePhotoResult.getStatus().isSuccess());
    }

    public static Observable<PlaceLikelihoodBuffer> getCurrentPlace(GoogleApiClient apiClient, PlaceFilter placeFilter) {
        return Observable.create(
                new PendingResultObservable<>(
                        Places.PlaceDetectionApi.getCurrentPlace(apiClient, placeFilter))
        ).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }

    public static Observable<Location> getLocation(Context context) {
        return LastKnownLocationObservable.createObservable(context);
    }
}
