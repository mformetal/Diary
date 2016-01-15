package miles.forum.ui.activity;

import android.support.v7.app.AppCompatActivity;

import butterknife.ButterKnife;

/**
 * Created by mbpeele on 1/14/16.
 */
public class BaseActivity extends AppCompatActivity {

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        ButterKnife.bind(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }
}
