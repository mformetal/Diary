package miles.diary.dagger;

import javax.inject.Singleton;

import dagger.Component;
import miles.diary.ui.activity.BaseActivity;

@Singleton
@Component(modules = {ApplicationModule.class})
public interface ApplicationComponent {

    void inject(BaseActivity baseActivity);

}