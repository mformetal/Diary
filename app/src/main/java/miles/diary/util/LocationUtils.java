package miles.diary.util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Looper;
import android.provider.Settings;

import java.util.List;

import rx.Observable;
import rx.Subscriber;

/**
 * Created by mbpeele on 3/5/16.
 */
public class LocationUtils {

    public final static String GPS = LocationManager.GPS_PROVIDER;
    public final static String NETWORK = LocationManager.NETWORK_PROVIDER;

    public static LocationManager getLocationManager(Context context) {
        return (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
    }

    @SuppressWarnings({"ResourceType"})
    public static Location getLastKnownLocation(Context context) {
        LocationManager locationManager = getLocationManager(context);
        boolean isGpsEnabled = locationManager.isProviderEnabled(GPS);
        boolean isNetworkEnabled = locationManager.isProviderEnabled(NETWORK);

        if (isGpsEnabled) {
            return locationManager.getLastKnownLocation(GPS);
        } else if (isNetworkEnabled) {
            return locationManager.getLastKnownLocation(NETWORK);
        }

        return null;
    }

    @SuppressWarnings({"ResourceType"})
    public static void getLocationUpdates(Context context, LocationListener locationListener) {
        LocationManager locationManager = getLocationManager(context);

        boolean isGpsEnabled = locationManager.isProviderEnabled(GPS);
        boolean isNetworkEnabled = locationManager.isProviderEnabled(NETWORK);

        if (isGpsEnabled) {
            locationManager.requestLocationUpdates(GPS, 0, 0, locationListener);
        } else if (isNetworkEnabled) {
            locationManager.requestLocationUpdates(NETWORK, 0, 0, locationListener);
        }
    }

    public static boolean isLocationEnabled(Context context) {
        int locationMode = 0;

        try {
            locationMode = Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.LOCATION_MODE);
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }

        return locationMode != Settings.Secure.LOCATION_MODE_OFF;
    }

    public static void getLocationAvailabilityUpdate(Context context, final AvailabilityCallback callback) {
        BroadcastReceiver receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (LocationUtils.isLocationEnabled(context)) {
                    callback.onLocationEnabled(context, this, intent);
                }
            }
        };

        context.registerReceiver(receiver, new IntentFilter(LocationManager.PROVIDERS_CHANGED_ACTION));
    }

    public static Observable<List<Address>> geocode(final Context context, final Location location, final int count) {
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

    public interface AvailabilityCallback {
        void onLocationEnabled(Context context, BroadcastReceiver receiver, Intent intent);
    }
}
