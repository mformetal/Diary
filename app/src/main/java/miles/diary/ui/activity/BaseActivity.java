package miles.diary.ui.activity;

import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.ViewGroup;

import com.google.android.gms.common.api.GoogleApiClient;

import javax.inject.Inject;

import butterknife.ButterKnife;
import icepick.Icepick;
import miles.diary.DiaryApplication;
import miles.diary.R;
import miles.diary.util.DataStore;
import miles.diary.data.api.WeatherService;
import miles.diary.data.api.DataManager;
import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by mbpeele on 1/14/16.
 */
public abstract class BaseActivity extends AppCompatActivity {

    @Inject WeatherService weatherService;
    @Inject DataStore datastore;
    @Inject GoogleApiClient.Builder googleApiClientBuilder;
    @Inject DataManager dataManager;

    private CompositeSubscription compositeSubscription;
    protected ViewGroup root;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((DiaryApplication) getApplication()).getComponent().inject(this);

        dataManager.init();

        compositeSubscription = new CompositeSubscription();

        Icepick.restoreInstanceState(this, savedInstanceState);
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
        dataManager.close();
        compositeSubscription.unsubscribe();
        ButterKnife.unbind(this);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Icepick.saveInstanceState(this, outState);
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

    public void noInternet() {
        Snackbar.make(root, getString(R.string.no_internet), Snackbar.LENGTH_SHORT).show();
    }

    public boolean hasPermissions(String[] permissions) {
        for (String permission: permissions) {
            if (ActivityCompat.checkSelfPermission(this, permission)
                    != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }

        return true;
    }

    public boolean permissionsGranted(int[] results) {
        if (results == null) {
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
