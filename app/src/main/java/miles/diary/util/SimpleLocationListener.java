package miles.diary.util;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;

/**
 * Created by mbpeele on 3/5/16.
 */
public abstract class SimpleLocationListener implements LocationListener {

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }
}
