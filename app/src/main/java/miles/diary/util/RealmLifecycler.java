package miles.diary.util;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

import miles.diary.DiaryApplication;
import miles.diary.ui.activity.BaseActivity;

/**
 * Created by mbpeele on 5/5/16.
 */
public class RealmLifecycler {

    public RealmLifecycler(DiaryApplication diaryApplication) {
        diaryApplication.registerActivityLifecycleCallbacks(new Application.ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

            }

            @Override
            public void onActivityStarted(Activity activity) {

            }

            @Override
            public void onActivityResumed(Activity activity) {

            }

            @Override
            public void onActivityPaused(Activity activity) {

            }

            @Override
            public void onActivityStopped(Activity activity) {

            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

            }

            @Override
            public void onActivityDestroyed(Activity activity) {

            }
        });
    }
}
