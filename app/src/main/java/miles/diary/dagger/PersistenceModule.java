package miles.diary.dagger;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import miles.diary.DiaryApplication;
import miles.diary.util.DataStore;

/**
 * Created by mbpeele on 5/5/16.
 */
@Module
@Singleton
public class PersistenceModule {

    private DiaryApplication diaryApplication;

    public PersistenceModule(DiaryApplication diaryApplication) {
        this.diaryApplication = diaryApplication;
    }

    @Provides
    @Singleton
    public DataStore provideDataStore() {
        return new DataStore(diaryApplication);
    }
}
