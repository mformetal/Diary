package mformetal.diary.ui;

import android.content.Context;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by mbpeele on 2/2/16.
 */
public class SnackbarBehavior extends CoordinatorLayout.Behavior<View> {

    public SnackbarBehavior(Context context, AttributeSet attrs) {}

    @Override
    public boolean layoutDependsOn(CoordinatorLayout parent, View child, View dependency) {
        return dependency instanceof Snackbar.SnackbarLayout;
    }

    @Override
    public boolean onDependentViewChanged(CoordinatorLayout parent, View child, View dependency) {
        float translationY = Math.max(0, dependency.getHeight() - dependency.getTranslationY());
        child.setTranslationY(-translationY);
        return true;
    }
}