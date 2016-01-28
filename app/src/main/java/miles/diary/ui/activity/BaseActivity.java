package miles.diary.ui.activity;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.StringRes;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import javax.inject.Inject;

import butterknife.ButterKnife;
import io.realm.Realm;
import miles.diary.DiaryApplication;
import miles.diary.data.RealmUtils;
import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by mbpeele on 1/14/16.
 */
public class BaseActivity extends AppCompatActivity {

    private CompositeSubscription compositeSubscription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        compositeSubscription = new CompositeSubscription();

        ((DiaryApplication) getApplication()).getComponent().inject(this);
    }

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        ButterKnife.bind(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
        compositeSubscription.unsubscribe();
    }

    public void showSnackbar(@StringRes int res, View root, View.OnClickListener onClickListener, int dur) {
        Snackbar snackbar = Snackbar.make(root, res, dur);
        if (onClickListener != null) {
            snackbar.setAction("Aight", onClickListener);
        }
        snackbar.show();
    }

    public void addSubscription(Subscription subscription) {
        compositeSubscription.add(subscription);
    }

    public void removeSubscription(Subscription subscription) {
        compositeSubscription.remove(subscription);
    }

    public boolean hasPermissions(String[] permissions) {
        for (String permission: permissions) {
            if (ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_DENIED) {
                return false;
            }
        }

        return true;
    }
}
