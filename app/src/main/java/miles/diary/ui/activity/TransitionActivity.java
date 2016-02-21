package miles.diary.ui.activity;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.ViewGroup;

import icepick.State;
import miles.diary.R;
import miles.diary.ui.PreDrawer;

/**
 * Created by mbpeele on 2/2/16.
 */
public abstract class TransitionActivity extends BaseActivity {

    private boolean hasSavedInstanceState;
    private Intent intent;
    @State boolean runCustomExitTransition = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        hasSavedInstanceState = savedInstanceState != null;
        intent = getIntent();
    }

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        root = (ViewGroup) ((ViewGroup) findViewById(android.R.id.content)).getChildAt(0);
        new PreDrawer<View>(root) {
            @Override
            public void notifyPreDraw(View view) {
                onEnter(root, intent, hasSavedInstanceState);
            }
        };
    }

    @Override
    public void onBackPressed() {
        if (runCustomExitTransition) {
            onExit(root);
        } else {
            super.onBackPressed();
        }
    }

    public void finishWithDefaultTransition() {
        runCustomExitTransition = false;
        onBackPressed();
    }

    abstract void onEnter(View root, Intent calledIntent, boolean hasSavedInstanceState);

    abstract void onExit(View root);
}
