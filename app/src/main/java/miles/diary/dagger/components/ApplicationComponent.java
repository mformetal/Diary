package miles.diary.dagger.components;

import android.content.Context;

import javax.inject.Singleton;

import dagger.Component;
import miles.diary.DiaryApplication;
import miles.diary.dagger.modules.ApplicationModule;
import miles.diary.data.api.Google;
import miles.diary.data.api.Repository;
import miles.diary.data.api.Weather;
import miles.diary.ui.activity.BaseActivity;
import miles.diary.ui.activity.EntryActivity;
import miles.diary.ui.activity.HomeActivity;
import miles.diary.ui.activity.LocationActivity;
import miles.diary.ui.activity.MapActivity;
import miles.diary.ui.activity.NewEntryActivity;
import miles.diary.ui.activity.PlacePhotosActivity;
import miles.diary.ui.activity.UriActivity;

@Singleton
@Component(modules = ApplicationModule.class)
public interface ApplicationComponent {

    DiaryApplication diaryApplication();

    Repository repository();

    Weather weather();

    Google google();

    void inject(HomeActivity activity);

    void inject(MapActivity activity);

    void inject(EntryActivity activity);

    void inject(LocationActivity locationActivity);

    void inject(NewEntryActivity newEntryActivity);

    void inject(PlacePhotosActivity activity);

    void inject(UriActivity activity);

}