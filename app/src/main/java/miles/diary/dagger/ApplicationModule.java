package miles.diary.dagger;

import android.app.Application;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Places;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import miles.diary.data.api.DataManagerImpl;
import miles.diary.util.DataStore;

/**
 * Created by mbpeele on 1/16/16.
 */
@Module
@Singleton
public class ApplicationModule {

    private Application mApplication;

    public ApplicationModule(Application application) {
        mApplication = application;
    }

    @Provides
    @Singleton
    public GoogleApiClient.Builder getGoogleApiClientBuilder() {
        return new GoogleApiClient.Builder(mApplication)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .addApi(LocationServices.API);
    }

    @Provides
    @Singleton
    public DataStore getDataStore() {
        return new DataStore(mApplication);
    }

    @Provides
    @Singleton
    public DataManagerImpl getDataManager() {
        return new DataManagerImpl(mApplication);
    }
}
