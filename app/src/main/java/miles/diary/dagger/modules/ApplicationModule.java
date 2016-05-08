package miles.diary.dagger.modules;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Places;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import icepick.Icepick;
import miles.diary.DiaryApplication;
import miles.diary.data.api.Google;
import miles.diary.data.api.Repository;
import miles.diary.data.api.RepositoryImpl;
import miles.diary.data.api.Weather;
import miles.diary.util.DataStore;
import miles.diary.util.SimpleLifecycleCallbacks;

/**
 * Created by mbpeele on 5/8/16.
 */
@Module
public class ApplicationModule {

    private final DiaryApplication application;
    private Repository repository;

    public ApplicationModule(DiaryApplication application) {
        this.application = application;
        application.registerActivityLifecycleCallbacks(new SimpleLifecycleCallbacks() {
            @Override
            public void onActivityResumed(Activity activity) {
                super.onActivityResumed(activity);
                repository.open();
            }

            @Override
            public void onActivityPaused(Activity activity) {
                super.onActivityPaused(activity);
                repository.close();
            }

            @Override
            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
                super.onActivityCreated(activity, savedInstanceState);
                Icepick.restoreInstanceState(activity, savedInstanceState);
            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
                super.onActivitySaveInstanceState(activity, outState);
                Icepick.saveInstanceState(activity, outState);
            }
        });
    }

    @Provides
    @Singleton
    DiaryApplication provideApplication() {
        return application;
    }

    @Provides
    @Singleton
    Repository provideRepository() {
        repository = new RepositoryImpl();
        repository.open();
        return repository;
    }

    @Provides
    @Singleton
    Weather provideWeather() {
        return new Weather(provideApplication());
    }

    @Provides
    @Singleton
    DataStore provideDataStore() {
        return new DataStore(provideApplication());
    }

    @Provides
    @Singleton
    Google provideGoogle() {
        return new Google(getGoogleBuilder(provideApplication()));
    }

    private GoogleApiClient.Builder getGoogleBuilder(Context context) {
        return new GoogleApiClient.Builder(context)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .addApi(LocationServices.API);
    }
}
