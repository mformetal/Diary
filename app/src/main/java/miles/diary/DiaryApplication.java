package miles.diary;

import android.app.Application;

import com.joanzapata.iconify.Iconify;
import com.joanzapata.iconify.fonts.WeathericonsModule;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import miles.diary.dagger.ApiModule;
import miles.diary.dagger.ContextComponent;
import miles.diary.dagger.DaggerContextComponent;
import miles.diary.dagger.PersistenceModule;

/**
 * Created by mbpeele on 1/14/16.
 */
public class DiaryApplication extends Application {

    private ContextComponent component;

    @Override
    public void onCreate() {
        super.onCreate();
        Iconify.with(new WeathericonsModule());

        RealmConfiguration realmConfiguration = new RealmConfiguration.Builder(this)
                .name(getString(R.string.realm_name))
                .deleteRealmIfMigrationNeeded()
                .build();
        Realm.setDefaultConfiguration(realmConfiguration);

        component = DaggerContextComponent.builder()
                .apiModule(new ApiModule(this))
                .persistenceModule(new PersistenceModule(this))
                .build();
    }

    public ContextComponent getContextComponent() { return component; }
}
