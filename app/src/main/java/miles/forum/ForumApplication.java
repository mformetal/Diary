package miles.forum;

import android.app.Application;

import com.crashlytics.android.Crashlytics;
import com.digits.sdk.android.Digits;
import com.parse.Parse;
import com.parse.ParseACL;
import com.parse.ParseObject;

import com.parse.ParseUser;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterCore;
import io.fabric.sdk.android.Fabric;
import miles.forum.dagger.ApplicationComponent;
import miles.forum.dagger.ApplicationModule;
import miles.forum.dagger.DaggerApplicationComponent;
import miles.forum.data.model.Post;

/**
 * Created by mbpeele on 1/14/16.
 */
public class ForumApplication extends Application {

    private ApplicationComponent component;

    @Override
    public void onCreate() {
        super.onCreate();
        TwitterAuthConfig authConfig = new TwitterAuthConfig(
                getResources().getString(R.string.twitter_key),
                getResources().getString(R.string.twitter_secret));
        Fabric.with(this, new Crashlytics(), new TwitterCore(authConfig), new Digits());

        ParseACL defaultACL = new ParseACL();
        defaultACL.setPublicReadAccess(true);
        defaultACL.setPublicWriteAccess(true);
        ParseACL.setDefaultACL(defaultACL, true);

        Parse.enableLocalDatastore(this);
        ParseUser.enableAutomaticUser();
        Parse.initialize(this);
        ParseObject.registerSubclass(Post.class);

        component = DaggerApplicationComponent.builder()
                .applicationModule(new ApplicationModule(this))
                .build();
    }

    public ApplicationComponent getComponent() { return component; }
}
