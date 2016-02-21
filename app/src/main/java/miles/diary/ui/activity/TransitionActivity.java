package miles.diary.ui.activity;

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

/**
 * Created by mbpeele on 2/2/16.
 */
public abstract class TransitionActivity extends BaseActivity {

    protected ViewGroup root;
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
        root = (ViewGroup) ((ViewGroup) findViewById(android.R.id.content)).getChildAt(0);
        new PreDrawer(root) {
            @Override
            public void notifyPreDraw(View view) {
                onEnter(root, intent, hasSavedInstanceState);
            }
        };
    }

    @Override
    public void onBackPressed() {
        onExit(root, intent, hasSavedInstanceState);
    }

    public void finishWithDefaultTransition() {
        finish();
    }

    public void finishWithoutDefaultTransition() {
        finish();
        overridePendingTransition(0, 0);
    }

    abstract void onEnter(View root, Intent calledIntent, boolean hasSavedInstanceState);

    abstract void onExit(View root, Intent calledIntent, boolean hasSavedInstanceState);
}
