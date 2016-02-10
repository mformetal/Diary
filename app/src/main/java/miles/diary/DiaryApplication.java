package miles.diary;

import android.app.Application;
import android.os.Build;

import com.crashlytics.android.Crashlytics;
import com.joanzapata.iconify.Iconify;
import com.joanzapata.iconify.fonts.WeathericonsModule;

import io.fabric.sdk.android.Fabric;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import miles.diary.dagger.ApplicationComponent;
import miles.diary.dagger.ApplicationModule;
import miles.diary.dagger.DaggerApplicationComponent;

/**
 * Created by mbpeele on 1/14/16.
 */
public class DiaryApplication extends Application {

    private ApplicationComponent component;

    @Override
    public void onCreate() {
        super.onCreate();
        Fabric.with(this, new Crashlytics());

        Iconify.with(new WeathericonsModule());

        RealmConfiguration realmConfiguration = new RealmConfiguration.Builder(this)
                .name(getString(R.string.realm_name))
                .deleteRealmIfMigrationNeeded()
                .build();

        Realm.setDefaultConfiguration(realmConfiguration);

        component = DaggerApplicationComponent.builder()
                .applicationModule(new ApplicationModule(this))
                .build();
    }

    public ApplicationComponent getComponent() { return component; }

    public static boolean isMarshmallow() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M;
    }
}
