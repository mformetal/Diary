package miles.diary.dagger.components;

import javax.inject.Singleton;

import dagger.Component;
import miles.diary.DiaryApplication;
import miles.diary.dagger.modules.ApplicationModule;
import miles.diary.data.api.Google;
import miles.diary.data.api.Repository;
import miles.diary.data.api.Weather;
import miles.diary.ui.activity.BaseActivity;
import miles.diary.util.Storage;

@Singleton
@Component(modules = ApplicationModule.class)
public interface ApplicationComponent {

    DiaryApplication diaryApplication();

    Repository repository();

    Weather weather();

    Google google();

    Storage storage();

    void inject(BaseActivity activity);

}