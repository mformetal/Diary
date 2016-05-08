package miles.diary.dagger.modules;

import android.app.Activity;

import dagger.Module;
import dagger.Provides;
import miles.diary.dagger.ActivityScope;

/**
 * Created by mbpeele on 5/8/16.
 */
@Module
public class ActivityModule {

    private final Activity activity;

    public ActivityModule(Activity activity) {
        this.activity = activity;
    }

    @Provides
    @ActivityScope
    Activity activity() {
        return activity;
    }

}
