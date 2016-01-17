package miles.forum.dagger;

import javax.inject.Singleton;

import dagger.Component;
import miles.forum.ui.activity.BaseActivity;

@Singleton
@Component(modules = {ApplicationModule.class})
public interface ApplicationComponent {

    void inject(BaseActivity baseActivity);

}