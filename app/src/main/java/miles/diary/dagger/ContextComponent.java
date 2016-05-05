package miles.diary.dagger;

import javax.inject.Singleton;

import dagger.Component;
import miles.diary.ui.activity.BaseActivity;
import miles.diary.ui.activity.EntryActivity;
import miles.diary.ui.activity.HomeActivity;
import miles.diary.ui.activity.LocationActivity;
import miles.diary.ui.activity.MapActivity;
import miles.diary.ui.activity.NewEntryActivity;
import miles.diary.ui.activity.PlacePhotosActivity;
import miles.diary.ui.fragment.BaseFragment;

@Singleton
@Component(modules = {ApiModule.class, PersistenceModule.class})
public interface ContextComponent {

    void inject(HomeActivity activity);

    void inject(MapActivity activity);

    void inject(EntryActivity activity);

    void inject(LocationActivity locationActivity);

    void inject(NewEntryActivity newEntryActivity);

    void inject(PlacePhotosActivity activity);

}