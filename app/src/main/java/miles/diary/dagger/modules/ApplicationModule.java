package miles.diary.dagger.modules;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import miles.diary.DiaryApplication;
import miles.diary.data.api.Google;
import miles.diary.data.api.Repository;
import miles.diary.data.api.RepositoryImpl;
import miles.diary.data.api.Weather;
import miles.diary.util.StorageImpl;
import miles.diary.util.Storage;

/**
 * Created by mbpeele on 5/8/16.
 */
@Module
public class ApplicationModule {

    private final DiaryApplication application;

    public ApplicationModule(DiaryApplication application) {
        this.application = application;
    }

    @Provides
    @Singleton
    DiaryApplication provideApplication() {
        return application;
    }

    @Provides
    @Singleton
    Repository provideRepository() {
        return new RepositoryImpl();
    }

    @Provides
    @Singleton
    Weather provideWeather() {
        return new Weather(provideApplication());
    }

    @Provides
    @Singleton
    Storage provideStorage() {
        return new StorageImpl(provideApplication());
    }

    @Provides
    @Singleton
    Google provideGoogle() {
        return new Google(provideApplication());
    }
}
