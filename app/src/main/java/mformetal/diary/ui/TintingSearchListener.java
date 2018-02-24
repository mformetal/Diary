package mformetal.diary.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Interpolator;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import mformetal.diary.ui.widget.SearchWidget;
import mformetal.diary.util.AnimUtils;
import mformetal.diary.util.ViewUtils;

/**
 * Created by mbpeele on 3/11/16.
 */
public abstract class TintingSearchListener implements SearchWidget.SearchListener {

    private ViewGroup root;
    private View tintView;
    private ViewGroup.MarginLayoutParams layoutParams;
    private Interpolator interpolator;

    public TintingSearchListener(ViewGroup viewGroup, final int color) {
        root = viewGroup;

        if (root.getWidth() == 0 || root.getHeight() == 0) {
            PreDrawerKt.addPreDrawer(root, new Function1<View, Unit>() {
                @Override
                public Unit invoke(View view) {
                    createTintView(color);
                    return null;
                }
            });
        } else {
            createTintView(color);
        }

        interpolator = new AccelerateDecelerateInterpolator();
    }

    private void createTintView(int color) {
        tintView = new View(root.getContext());
        tintView.setBackgroundColor(color);

        Context context = root.getContext();
        layoutParams = new ViewGroup.MarginLayoutParams(root.getLayoutParams());
        layoutParams.topMargin += ViewUtils.getActionBarSize(context);
        layoutParams.topMargin += ViewUtils.getStatusBarHeight(context);
    }

    @Override
    public void onSearchShow(int[] position) {
        root.addView(tintView, layoutParams);

        float radius = (float) Math.sqrt(Math.pow(root.getHeight(), 2) + Math.pow(root.getWidth(), 2));
        Animator revealAnimator =
                ViewAnimationUtils.createCircularReveal(tintView, position[0], position[1], 0.0f, radius);
        revealAnimator.setDuration(AnimUtils.longAnim(root.getContext()));

        ObjectAnimator alpha = ObjectAnimator.ofFloat(tintView, View.ALPHA, 0f, 1f);
        alpha.setDuration(revealAnimator.getDuration());

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.play(revealAnimator).with(alpha);
        animatorSet.play(alpha);
        animatorSet.setInterpolator(interpolator);
        animatorSet.start();
    }

    @Override
    public void onSearchDismiss(int[] position) {
        if (tintView.isAttachedToWindow() && tintView.isInLayout()) {
            root.bringChildToFront(tintView);

            float radius = (float) Math.sqrt(Math.pow(root.getHeight(), 2) + Math.pow(root.getWidth(), 2));
            Animator revealAnimator =
                    ViewAnimationUtils.createCircularReveal(tintView, position[0], position[1], radius, 0f);
            revealAnimator.setDuration(AnimUtils.longAnim(root.getContext()));
            revealAnimator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    root.removeView(tintView);
                }
            });
            revealAnimator.setDuration(AnimUtils.longAnim(root.getContext()));

            ObjectAnimator alpha = ObjectAnimator.ofFloat(tintView, View.ALPHA, 1f, 0f);
            alpha.setDuration(revealAnimator.getDuration());

            AnimatorSet animatorSet = new AnimatorSet();
            animatorSet.play(revealAnimator).with(alpha);
            animatorSet.setInterpolator(interpolator);
            animatorSet.start();
        } else {
            ObjectAnimator alpha = ObjectAnimator.ofFloat(tintView, View.ALPHA, 1f, 0f);
            alpha.setDuration(AnimUtils.longAnim(root.getContext()));
            alpha.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    root.removeView(tintView);
                }
            });
            alpha.start();
        }
    }

    @Override
    public void onSearchTextChanged(String text) {
        // remove the tintView, since it's currently on top of the searched items
        root.removeView(tintView);
    }
}
