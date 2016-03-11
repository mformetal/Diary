package miles.diary.data.rx;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;

import java.util.List;

import rx.Observable;
import rx.Subscriber;

/**
 * Created by mbpeele on 3/11/16.
 */
public class GeocodeObservable {

    public static Observable<List<Address>> geocode(Context context, Location location, int count) {
        return Observable.create(new Observable.OnSubscribe<List<Address>>() {
            @Override
            public void call(Subscriber<? super List<Address>> subscriber) {
                subscriber.onStart();

                Geocoder geocoder = new Geocoder(context);

                try {
                    subscriber.onNext(geocoder.getFromLocation(
                            location.getLatitude(), location.getLongitude(), count));
                } catch (Exception e) {
                    subscriber.onError(e);
                } finally {
                    subscriber.onCompleted();
                }
            }
        });
    }
}
