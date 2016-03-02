package miles.diary.data.api;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.AutocompletePredictionBuffer;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.PlaceFilter;
import com.google.android.gms.location.places.PlaceLikelihoodBuffer;
import com.google.android.gms.location.places.PlacePhotoMetadata;
import com.google.android.gms.location.places.PlacePhotoMetadataBuffer;
import com.google.android.gms.location.places.PlacePhotoMetadataResult;
import com.google.android.gms.location.places.PlacePhotoResult;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLngBounds;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import miles.diary.data.model.AutoCompleteItem;
import miles.diary.data.rx.GoogleResultObservable;
import miles.diary.util.Logg;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by mbpeele on 2/21/16.
 */
public class GoogleService {

    private GoogleService() {}

    public static Observable<List<AutoCompleteItem>> autocomplete(GoogleApiClient client, CharSequence query,
                                                        LatLngBounds bounds, AutocompleteFilter filter) {
        return Observable.create(new GoogleResultObservable<>(
                Places.GeoDataApi.getAutocompletePredictions(client, query.toString(), bounds, filter)))
                .filter(new Func1<AutocompletePredictionBuffer, Boolean>() {
                    @Override
                    public Boolean call(AutocompletePredictionBuffer autocompletePredictions) {
                        return autocompletePredictions.getStatus().isSuccess();
                    }
                })
                .flatMap(new Func1<AutocompletePredictionBuffer, Observable<List<AutoCompleteItem>>>() {
                    @Override
                    public Observable<List<AutoCompleteItem>> call(AutocompletePredictionBuffer autocompletePredictions) {
                        Iterator<AutocompletePrediction> predictionIterator = autocompletePredictions.iterator();
                        List<AutoCompleteItem> list = new ArrayList<AutoCompleteItem>(autocompletePredictions.getCount());
                        while (predictionIterator.hasNext()) {
                            AutocompletePrediction prediction = predictionIterator.next();
                            list.add(new AutoCompleteItem(prediction.getPlaceId(), prediction.getFullText(null)));
                        }

                        autocompletePredictions.release();
                        return Observable.just(list);
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public static Observable<PlacePhotoResult> getPlacePhoto(final String placeId,
                                                             final GoogleApiClient apiClient) {
        return Observable.create(new GoogleResultObservable<>(
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
                                Observable.create(new GoogleResultObservable<PlacePhotoResult>(
                                        photoMetadata.getPhoto(apiClient)));
                        buffer.release();
                        return observable;
                    }
                });
    }

    public static Observable<PlaceLikelihoodBuffer> getCurrentPlace(GoogleApiClient apiClient, PlaceFilter placeFilter) {
        return Observable.create(
                new GoogleResultObservable<PlaceLikelihoodBuffer>(
                        Places.PlaceDetectionApi.getCurrentPlace(apiClient, placeFilter))
        ).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }

    public static Observable<PlaceBuffer> getPlaceById(GoogleApiClient apiClient, String placeId) {
        return Observable.create(
                new GoogleResultObservable<>(
                        Places.GeoDataApi.getPlaceById(apiClient, placeId)
        )).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }

    public static Observable<Location> getLocation(final GoogleApiClient apiClient) {
        return Observable.create(new Observable.OnSubscribe<Location>() {
            @Override
            public void call(Subscriber<? super Location> subscriber) {
                subscriber.onStart();

                Location location = LocationServices.FusedLocationApi.getLastLocation(apiClient);
                subscriber.onNext(location);

                subscriber.onCompleted();
            }
        });
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
