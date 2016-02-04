package miles.diary.ui.activity;

import android.animation.Animator;
import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
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
    private Intent intent;
    public ValueAnimator enterColor, exitColor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        hasSavedInstanceState = savedInstanceState != null;
        intent = getIntent();

        ValueAnimator.AnimatorUpdateListener updateListener = animation ->
                getWindow().getDecorView().setBackgroundColor((int) animation.getAnimatedValue());
        exitColor = ValueAnimator.ofObject(new ArgbEvaluator(),
                ContextCompat.getColor(this, R.color.scrim), Color.TRANSPARENT);
        exitColor.addUpdateListener(updateListener);
        enterColor = ValueAnimator.ofObject(new ArgbEvaluator(),
                Color.TRANSPARENT, ContextCompat.getColor(this, R.color.scrim));
        enterColor.addUpdateListener(updateListener);
    }

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        root = ((ViewGroup) findViewById(android.R.id.content)).getChildAt(0);
        new PreDrawer(root) {
            @Override
            public void notifyPreDraw() {
                onEnter(root, intent, hasSavedInstanceState);
            }
        };
    }

    @Override
    public void setContentView(View view) {
        super.setContentView(view);
        new PreDrawer(view) {
            @Override
            public void notifyPreDraw() {
                onEnter(root, intent, hasSavedInstanceState);
            }
        };
    }

    @Override
    public void setContentView(View view, ViewGroup.LayoutParams params) {
        super.setContentView(view, params);
        new PreDrawer(view) {
            @Override
            public void notifyPreDraw() {
                onEnter(root, intent, hasSavedInstanceState);
            }
        };
    }

    @Override
    public void onBackPressed() {
        onExit(root, intent, hasSavedInstanceState);
    }

    public void finishWithDefaultTransition() {
        finishWithoutDefaultTransition();
    }

    public void finishWithoutDefaultTransition() {
        finish();
        overridePendingTransition(0, 0);
    }


    public abstract void onEnter(View root, Intent calledIntent, boolean hasSavedInstanceState);

    public abstract void onExit(View root, Intent calledIntent, boolean hasSavedInstanceState);
}
