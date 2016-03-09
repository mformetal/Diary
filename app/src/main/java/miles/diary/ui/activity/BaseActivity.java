package miles.diary.ui.activity;

import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Pair;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Toolbar;

import com.google.android.gms.common.api.GoogleApiClient;

import javax.inject.Inject;

import butterknife.ButterKnife;
import miles.diary.DiaryApplication;
import miles.diary.R;
import miles.diary.data.api.WeatherService;
import miles.diary.data.api.db.DataManager;
import miles.diary.util.DataStore;
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

    protected final static String CONFIRMATION_DIALOG = "confirmation";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((DiaryApplication) getApplication()).getComponent().inject(this);

        dataManager.init();

        compositeSubscription = new CompositeSubscription();
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
        Snackbar.make(root, getString(R.string.error_no_internet), Snackbar.LENGTH_SHORT).show();
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

    public Pair<View, String> getNavigationBarSharedElement() {
        return new Pair<>(getNavigationBarView(), Window.NAVIGATION_BAR_BACKGROUND_TRANSITION_NAME);
    }

    public Pair<View, String> getStatusBarSharedElement() {
        return new Pair<>(getStatusBarView(), Window.STATUS_BAR_BACKGROUND_TRANSITION_NAME);
    }

    public View getNavigationBarView() {
        View decor = getWindow().getDecorView();
        return decor.findViewById(android.R.id.navigationBarBackground);
    }

    public View getStatusBarView() {
        View decor = getWindow().getDecorView();
        return decor.findViewById(android.R.id.statusBarBackground);
    }

    public MenuItem getMenuItem(Toolbar toolbar, int id) {
        try {
            return toolbar.getMenu().getItem(id);
        } catch (IndexOutOfBoundsException e) {
            return null;
        }
    }
}
