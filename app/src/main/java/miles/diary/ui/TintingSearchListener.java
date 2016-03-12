package miles.diary.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.support.v4.content.ContextCompat;
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

/**
 * Created by mbpeele on 3/11/16.
 */
public class TintingSearchListener implements SearchWidget.SearchListener {

    private ViewGroup root;
    private View tintView;
    private Interpolator interpolator;

    public TintingSearchListener(ViewGroup viewGroup, int color) {
        root = viewGroup;

        if (root.getWidth() == 0 || root.getHeight() == 0) {
            PreDrawer.addPreDrawer(root, new PreDrawer.OnPreDrawListener<ViewGroup>() {
                @Override
                public boolean onPreDraw(ViewGroup view) {
                    createTintView(view, color);
                    return true;
                }
            });
        } else {
            createTintView(root, color);
        }

        interpolator = new AccelerateDecelerateInterpolator();
    }

    private void createTintView(ViewGroup view, int color) {
        tintView = new View(view.getContext());
        tintView.setBottom(view.getHeight());
        tintView.setRight(view.getWidth());
        tintView.setBackgroundColor(color);
    }

    @Override
    public void onSearchShow(int[] position) {
        root.addView(tintView);

        float radius = (float) Math.sqrt(Math.pow(root.getHeight(), 2) + Math.pow(root.getWidth(), 2));
        Animator revealAnimator =
                ViewAnimationUtils.createCircularReveal(tintView, position[0], position[1], 0.0f, radius);
        revealAnimator.setDuration(AnimUtils.longAnim(root.getContext()));
        revealAnimator.setInterpolator(interpolator);

        ObjectAnimator alpha = ObjectAnimator.ofFloat(tintView, View.ALPHA, 0f, 1f);
        alpha.setDuration(revealAnimator.getDuration());
        alpha.setInterpolator(interpolator);

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.play(revealAnimator).with(alpha);
        animatorSet.start();
    }

    @Override
    public void onSearchDismiss(int[] position) {
        float radius = (float) Math.sqrt(Math.pow(root.getHeight(), 2) + Math.pow(root.getWidth(), 2));
        Animator revealAnimator =
                ViewAnimationUtils.createCircularReveal(tintView, position[0], position[1], radius, 0f);
        revealAnimator.setDuration(AnimUtils.longAnim(root.getContext()));
        revealAnimator.setInterpolator(interpolator);
        revealAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                root.removeView(tintView);
            }
        });

        ObjectAnimator alpha = ObjectAnimator.ofFloat(tintView, View.ALPHA, 1f, 0f);
        alpha.setDuration(revealAnimator.getDuration());
        alpha.setInterpolator(interpolator);

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.play(revealAnimator).with(alpha);
        animatorSet.start();
    }
}
