package miles.diary.dagger;

import android.app.Application;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import miles.diary.data.DataStore;

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
    public Application provideAppContext() {
        return mApplication;
    }

    @Provides
    @Singleton
    public DataStore getDatastore() {
        return new DataStore(mApplication);
    }
}
