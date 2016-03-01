package miles.diary.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.ViewGroup;

import icepick.State;
import miles.diary.ui.PreDrawer;

/**
 * Created by mbpeele on 2/2/16.
 */
public abstract class TransitionActivity extends BaseActivity {

    @State boolean hasSavedInstanceState;
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        hasSavedInstanceState = savedInstanceState != null;
        intent = getIntent();
    }

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        PreDrawer.addPreDrawer(root, new PreDrawer.OnPreDrawListener<ViewGroup>() {
            @Override
            public boolean onPreDraw(ViewGroup view) {
                onEnter(root, intent, hasSavedInstanceState);
                return true;
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (shouldRunCustomExitAnimation()) {
            onExit(root);
        } else {
            super.onBackPressed();
        }
    }

    abstract boolean shouldRunCustomExitAnimation();

    abstract void onEnter(ViewGroup root, Intent calledIntent, boolean hasSavedInstanceState);

    abstract void onExit(ViewGroup root);
}
