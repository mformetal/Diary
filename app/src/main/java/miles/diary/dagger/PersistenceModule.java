package miles.diary.dagger;

import android.app.Activity;
import android.os.Bundle;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import icepick.Icepick;
import miles.diary.DiaryApplication;
import miles.diary.util.DataStore;
import miles.diary.util.SimpleLifecycleCallbacks;

/**
 * Created by mbpeele on 5/5/16.
 */
@Module
@Singleton
public class PersistenceModule {

    private DiaryApplication diaryApplication;

    public PersistenceModule(DiaryApplication diaryApplication) {
        this.diaryApplication = diaryApplication;

        diaryApplication.registerActivityLifecycleCallbacks(new SimpleLifecycleCallbacks() {
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
    public DataStore provideDataStore() {
        return new DataStore(diaryApplication);
    }
}
