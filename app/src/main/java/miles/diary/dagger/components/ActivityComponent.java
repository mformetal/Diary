package miles.diary.dagger.components;

import android.app.Activity;

import dagger.Component;
import miles.diary.dagger.ActivityScope;
import miles.diary.dagger.modules.ActivityModule;

/**
 * Created by mbpeele on 5/8/16.
 */
@ActivityScope
@Component(dependencies = ApplicationComponent.class, modules = ActivityModule.class)
public interface ActivityComponent {

    Activity activity();
}
