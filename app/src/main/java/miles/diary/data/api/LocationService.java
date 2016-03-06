package miles.diary.data.api;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;

import miles.diary.util.Logg;
import rx.Observable;

/**
 * Created by mbpeele on 3/5/16.
 */
public class LocationService {

    public static LocationManager getLocationManager(Context context) {
        return (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
    }

    @SuppressWarnings({"ResourceType"})
    public static Location getLocation(Context context) {
        LocationManager locationManager =
                (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

        String gpsProvider = LocationManager.GPS_PROVIDER;
        String networkProvider = LocationManager.NETWORK_PROVIDER;
        boolean isGpsEnabled = locationManager.isProviderEnabled(gpsProvider);
        boolean isNetworkEnabled = locationManager.isProviderEnabled(networkProvider);

        if (isGpsEnabled) {
            return locationManager.getLastKnownLocation(gpsProvider);
        } else if (isNetworkEnabled) {
            return locationManager.getLastKnownLocation(networkProvider);
        }

        return null;
    }

    @SuppressWarnings({"ResourceType"})
    public static void getLocationUpdates(Context context, LocationListener locationListener) {
        LocationManager locationManager =
                (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

        String gpsProvider = LocationManager.GPS_PROVIDER;
        String networkProvider = LocationManager.NETWORK_PROVIDER;
        boolean isGpsEnabled = locationManager.isProviderEnabled(gpsProvider);
        boolean isNetworkEnabled = locationManager.isProviderEnabled(networkProvider);

        if (isGpsEnabled) {
            locationManager.requestLocationUpdates(gpsProvider, 0, 0, locationListener);
        } else if (isNetworkEnabled) {
            locationManager.requestLocationUpdates(networkProvider, 0, 0, locationListener);
        }
    }

    public static boolean isLocationEnabled(Context context) {
        int locationMode = 0;

        try {
            locationMode = Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.LOCATION_MODE);
        } catch (Settings.SettingNotFoundException e) {
            Logg.log(e, "LOCATION SETTING NOT FOUND");
        }

        return locationMode != Settings.Secure.LOCATION_MODE_OFF;
    }
}
