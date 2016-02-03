package miles.diary.ui.activity;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

import miles.diary.R;
import miles.diary.ui.PreDrawer;
import miles.diary.util.Logg;

/**
 * Created by mbpeele on 2/2/16.
 */
public abstract class TransitionActivity extends BaseActivity {

    private View root;
    private boolean hasSavedInstanceState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        hasSavedInstanceState = savedInstanceState == null;
    }

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        root = ((ViewGroup) findViewById(android.R.id.content)).getChildAt(0);
        new PreDrawer(root) {
            @Override
            public void notifyPreDraw() {
                onEnter(root, hasSavedInstanceState);
            }
        };
    }

    @Override
    public void setContentView(View view) {
        super.setContentView(view);
        new PreDrawer(view) {
            @Override
            public void notifyPreDraw() {
                onEnter(root, hasSavedInstanceState);
            }
        };
    }

    @Override
    public void setContentView(View view, ViewGroup.LayoutParams params) {
        super.setContentView(view, params);
        new PreDrawer(view) {
            @Override
            public void notifyPreDraw() {
                onEnter(root, hasSavedInstanceState);
            }
        };
    }

    @Override
    public void onBackPressed() {
        onExit(root, hasSavedInstanceState);
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0, 0);
    }

    public abstract void onEnter(View root, boolean hasSavedInstanceState);

    public abstract void onExit(View root, boolean hasSavedInstanceState);
}
