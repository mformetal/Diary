package miles.diary.dagger.modules;

import android.app.Activity;

import dagger.Module;
import dagger.Provides;
import miles.diary.dagger.ActivityScope;
import miles.diary.ui.activity.BaseActivity;

/**
 * Created by mbpeele on 5/8/16.
 */
@Module
public class ActivityModule {

    private final BaseActivity activity;

    public ActivityModule(BaseActivity activity) {
        this.activity = activity;
    }

    @Provides
    @ActivityScope
    BaseActivity activity() {
        return activity;
    }

}
