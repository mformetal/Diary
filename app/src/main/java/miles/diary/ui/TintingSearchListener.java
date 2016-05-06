package miles.diary.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.graphics.Canvas;
import android.os.Handler;
import android.support.design.widget.AppBarLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.view.ViewGroupOverlay;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Interpolator;

import java.lang.ref.WeakReference;

import miles.diary.R;
import miles.diary.ui.widget.SearchWidget;
import miles.diary.util.AnimUtils;
import miles.diary.util.Logg;
import miles.diary.util.ViewUtils;

/**
 * Created by mbpeele on 3/11/16.
 */
public abstract class TintingSearchListener implements SearchWidget.SearchListener {

    private ViewGroup root;
    private View tintView;
    private Interpolator interpolator;

    public TintingSearchListener(ViewGroup viewGroup, final int color) {
        root = viewGroup;

        if (root.getWidth() == 0 || root.getHeight() == 0) {
            PreDrawer.addPreDrawer(root, new PreDrawer.OnPreDrawListener<ViewGroup>() {
                @Override
                public boolean onPreDraw(ViewGroup view) {
                    createTintView(color);
                    return true;
                }
            });
        } else {
            createTintView(color);
        }

        interpolator = new AccelerateDecelerateInterpolator();
    }

    private void createTintView(int color) {
//        tintView = new View(viewGroup.getContext());
//        ViewGroup.LayoutParams layoutParams = root.getLayoutParams();
//        layoutParams.width = root.getWidth();
//        layoutParams.height = root.getHeight() - ViewUtils.getActionBarSize(root.getContext());
//        tintView.setBackgroundColor(color);
//        tintView.setLayoutParams(layoutParams);
//
//        root.addView(tintView, layoutParams);
    }

    @Override
    public void onSearchShow(int[] position) {
//        root.addView(tintView);
//
//        float radius = (float) Math.sqrt(Math.pow(root.getHeight(), 2) + Math.pow(root.getWidth(), 2));
//        Animator revealAnimator =
//                ViewAnimationUtils.createCircularReveal(tintView, position[0], position[1], 0.0f, radius);
//        revealAnimator.setDuration(AnimUtils.longAnim(root.getContext()));
//
//        ObjectAnimator alpha = ObjectAnimator.ofFloat(tintView, View.ALPHA, 0f, 1f);
//        alpha.setDuration(revealAnimator.getDuration());
//
//        AnimatorSet animatorSet = new AnimatorSet();
//        animatorSet.play(revealAnimator).with(alpha);
//        animatorSet.play(alpha);
//        animatorSet.setInterpolator(interpolator);
//        animatorSet.start();
    }

    @Override
    public void onSearchDismiss(int[] position) {
//        root.bringChildToFront(tintView);
//
//        float radius = (float) Math.sqrt(Math.pow(root.getHeight(), 2) + Math.pow(root.getWidth(), 2));
//        Animator revealAnimator =
//                ViewAnimationUtils.createCircularReveal(tintView, position[0], position[1], radius, 0f);
//        revealAnimator.setDuration(AnimUtils.longAnim(root.getContext()));
//        revealAnimator.addListener(new AnimatorListenerAdapter() {
//            @Override
//            public void onAnimationEnd(Animator animation) {
//                root.removeView(tintView);
//            }
//        });
//        revealAnimator.setDuration(AnimUtils.longAnim(root.getContext()));
//
//        ObjectAnimator alpha = ObjectAnimator.ofFloat(tintView, View.ALPHA, 1f, 0f);
//        alpha.setDuration(revealAnimator.getDuration());
//
//        AnimatorSet animatorSet = new AnimatorSet();
//        animatorSet.play(revealAnimator).with(alpha);
//        animatorSet.setInterpolator(interpolator);
//        animatorSet.start();
    }
}
