package miles.diary.dagger;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Places;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import miles.diary.DiaryApplication;
import miles.diary.data.api.RealmImpl;
import miles.diary.data.api.Weather;
import miles.diary.util.DataStore;
import miles.diary.util.Logg;
import miles.diary.util.SimpleLifecycleCallbacks;

/**
 * Created by mbpeele on 5/5/16.
 */
@Module
@Singleton
public class ApiModule {

    private DiaryApplication mApplication;
    private RealmImpl realm;

    public ApiModule(DiaryApplication application) {
        mApplication = application;

        mApplication.registerActivityLifecycleCallbacks(new SimpleLifecycleCallbacks() {
            @Override
            public void onActivityResumed(Activity activity) {
                realm.init();
            }

            @Override
            public void onActivityStopped(Activity activity) {
                realm.close();
            }
        });
    }

    @Provides
    @Singleton
    public GoogleApiClient.Builder provideGoogle() {
        return new GoogleApiClient.Builder(mApplication)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .addApi(LocationServices.API);
    }

    @Provides
    @Singleton
    public RealmImpl provideRealmImpl() {
        realm = new RealmImpl(mApplication);
        realm.init();
        return realm;
    }

    @Provides
    @Singleton
    public Weather provideWeather() {
        return new Weather(mApplication);
    }

}
