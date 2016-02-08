package miles.diary.ui.activity;

import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.ViewGroup;

import com.google.android.gms.common.api.GoogleApiClient;

import javax.inject.Inject;

import butterknife.ButterKnife;
import io.realm.Realm;
import miles.diary.DiaryApplication;
import miles.diary.data.DataStore;
import miles.diary.data.WeatherService;
import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by mbpeele on 1/14/16.
 */
public abstract class BaseActivity extends AppCompatActivity {

    @Inject WeatherService weatherService;
    @Inject DataStore datastore;
    @Inject GoogleApiClient.Builder googleApiClientBuilder;

    private CompositeSubscription compositeSubscription;
    protected Realm realm;
    public ViewGroup root;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((DiaryApplication) getApplication()).getComponent().inject(this);
        compositeSubscription = new CompositeSubscription();
        realm = Realm.getDefaultInstance();
    }

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        ButterKnife.bind(this);
        root = (ViewGroup) ((ViewGroup) findViewById(android.R.id.content)).getChildAt(0);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.close();
        ButterKnife.unbind(this);
        compositeSubscription.unsubscribe();
    }

    public void addSubscription(Subscription subscription) {
        compositeSubscription.add(subscription);
    }

    public void removeSubscription(Subscription subscription) {
        compositeSubscription.remove(subscription);
    }

    public boolean hasConnection() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        return connectivityManager.getActiveNetworkInfo() != null &&
                connectivityManager.getActiveNetworkInfo().isConnectedOrConnecting();
    }

    public boolean hasPermissions(String[] permissions) {
        for (String permission: permissions) {
            if (ActivityCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_DENIED) {
                return false;
            }
        }

        return true;
    }

    public boolean permissionsGranted(int[] results, int expectedLength) {
        if (results == null || results.length != expectedLength) {
            return false;
        }

        for (int result: results) {
            if (result != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }

        return true;
    }
}
