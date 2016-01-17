package miles.forum.ui;

import android.content.Context;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Interpolator;
import android.widget.FrameLayout;

import miles.forum.util.Logg;

/**
 * Created by mbpeele on 1/16/16.
 */
public class ScalingFabBehavior extends CoordinatorLayout.Behavior<FrameLayout> {

    private static final Interpolator INTERPOLATOR = new FastOutSlowInInterpolator();

    private boolean isAnimating = false;

    public ScalingFabBehavior(Context context, AttributeSet attributeSet) {
        super();
    }

    @Override
    public boolean layoutDependsOn(CoordinatorLayout parent, FrameLayout child, View dependency) {
        return dependency instanceof Snackbar.SnackbarLayout;
    }

    @Override
    public boolean onDependentViewChanged(CoordinatorLayout parent, FrameLayout child, View dependency) {
        float translationY = Math.max(0, dependency.getHeight() - dependency.getTranslationY());
        child.setTranslationY(-translationY);
        return true;
    }

    @Override
    public boolean onStartNestedScroll(final CoordinatorLayout coordinatorLayout, final FrameLayout child,
                                       final View directTargetChild, final View target, final int nestedScrollAxes) {
        return nestedScrollAxes == ViewCompat.SCROLL_AXIS_VERTICAL
                || super.onStartNestedScroll(coordinatorLayout, child, directTargetChild, target, nestedScrollAxes);
    }

    @Override
    public void onNestedScroll(final CoordinatorLayout coordinatorLayout, final FrameLayout child,
                               final View target, final int dxConsumed, final int dyConsumed,
                               final int dxUnconsumed, final int dyUnconsumed) {
        super.onNestedScroll(coordinatorLayout, child, target, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed);
        if (dyConsumed > 0 && !isAnimating && child.getVisibility() == View.VISIBLE) {
            child.animate()
                    .scaleX(0f)
                    .scaleY(0f)
                    .alpha(0f)
                    .setDuration(350)
                    .setInterpolator(INTERPOLATOR)
                    .withStartAction(new Runnable() {
                        @Override
                        public void run() {
                            isAnimating = true;
                        }
                    })
                    .withEndAction(new Runnable() {
                        @Override
                        public void run() {
                            isAnimating = false;
                            child.setVisibility(View.GONE);
                        }
                    })
                    .start();
        } else if (dyConsumed < 0 && child.getVisibility() != View.VISIBLE) {
            child.animate()
                    .scaleX(1f)
                    .scaleY(1f)
                    .alpha(1.0F)
                    .setDuration(350)
                    .setInterpolator(INTERPOLATOR)
                    .withStartAction(new Runnable() {
                        @Override
                        public void run() {
                            isAnimating = true;
                            child.setVisibility(View.VISIBLE);
                        }
                    })
                    .withEndAction(new Runnable() {
                        @Override
                        public void run() {
                            isAnimating = false;
                        }
                    })
                    .start();
        }
    }
}
