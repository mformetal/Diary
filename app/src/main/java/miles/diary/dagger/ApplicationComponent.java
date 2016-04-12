package miles.diary.dagger;

import javax.inject.Singleton;

import dagger.Component;
import miles.diary.ui.activity.BaseActivity;
import miles.diary.ui.fragment.BaseFragment;

@Singleton
@Component(modules = {ApplicationModule.class})
public interface ApplicationComponent {

    void inject(BaseActivity baseActivity);

    void inject(BaseFragment baseFragment);

}