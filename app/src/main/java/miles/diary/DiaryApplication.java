package miles.diary;

import android.app.Application;

import com.joanzapata.iconify.Iconify;
import com.joanzapata.iconify.fonts.WeathericonsModule;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import miles.diary.dagger.components.ApplicationComponent;
import miles.diary.dagger.components.DaggerApplicationComponent;
import miles.diary.dagger.modules.ApplicationModule;

/**
 * Created by mbpeele on 1/14/16.
 */
public class DiaryApplication extends Application {

    private ApplicationComponent applicationComponent;

    @Override
    public void onCreate() {
        super.onCreate();
        Iconify.with(new WeathericonsModule());

        RealmConfiguration realmConfiguration = new RealmConfiguration.Builder(this)
                .name(getString(R.string.realm_name))
                .deleteRealmIfMigrationNeeded()
                .build();
        Realm.setDefaultConfiguration(realmConfiguration);

        applicationComponent = DaggerApplicationComponent.builder()
                .applicationModule(new ApplicationModule(this))
                .build();
    }

    public ApplicationComponent getApplicationComponent() { return applicationComponent; }

}
