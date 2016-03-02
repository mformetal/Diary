package miles.diary.ui.widget;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.support.design.widget.FloatingActionButton;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import miles.diary.R;
import miles.diary.util.AnimUtils;
import miles.diary.util.Logg;
import miles.diary.util.ViewUtils;

/**
 * Created by mbpeele on 3/1/16.
 */
public class LinearFabMenu extends LinearLayout {

    private FloatingActionButton menuFab;

    private final static int INITIAL_DELAY = 0;
    private final static int DURATION = 400;
    private final static int DELAY_INCREMENT = 15;
    private final static int HIDE_DIFF = 50;
    private boolean isAnimating = false;
    private boolean isMenuShowing = false;

    public LinearFabMenu(Context context) {
        super(context);
    }

    public LinearFabMenu(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public LinearFabMenu(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public LinearFabMenu(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        menuFab = getFloatingActionButton(0);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);

        menuFab = getFloatingActionButton(0);
        menuFab.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleMenu();
            }
        });
    }

    private void rotateToggleOpen() {
        ObjectAnimator.ofFloat(menuFab, View.ROTATION,
                menuFab.getRotation(), menuFab.getRotation() - 135f).start();
    }

    private void rotateToggleClosed() {
        ObjectAnimator.ofFloat(menuFab, View.ROTATION,
                menuFab.getRotation(), menuFab.getRotation() - 135f)
                .setDuration(HIDE_DIFF + DURATION + DELAY_INCREMENT * getChildCount() - 1)
                .start();
    }

    private FloatingActionButton getFloatingActionButton(int i) {
        return (FloatingActionButton) getChildAt(i);
    }

    public void toggleMenu() {
        if (!isAnimating) {
            if (isMenuShowing) {
                hideMenu();
            } else {
                showMenu();
            }
        }
    }

    private void showMenu() {
        if (!isMenuShowing && !isAnimating) {
            rotateToggleOpen();

            ArrayList<Animator> animators = new ArrayList<>();
            int delay = INITIAL_DELAY;
            for (int i = 0; i < getChildCount(); i++) {
                View view = getChildAt(i);
                if (view.getId() == menuFab.getId()) {
                    continue;
                }

                ObjectAnimator gone = AnimUtils.visible(view);
                gone.setStartDelay(delay);

                delay += DELAY_INCREMENT;

                animators.add(gone);
            }

            AnimatorSet set = new AnimatorSet();
            set.playTogether(animators);
            set.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationStart(Animator animation) {
                    isAnimating = true;
                    isMenuShowing = true;
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    isAnimating = false;
                }
            });
            set.start();
        }
    }

    private void hideMenu() {
        if (isMenuShowing && !isAnimating) {
            rotateToggleClosed();

            ArrayList<Animator> animators = new ArrayList<>();
            int delay = INITIAL_DELAY;
            for (int i = 0; i < getChildCount(); i++) {
                View view = getChildAt(i);
                if (view.getId() == menuFab.getId()) {
                    continue;
                }

                ObjectAnimator gone = AnimUtils.gone(view);
                gone.setStartDelay(delay);

                delay += DELAY_INCREMENT;

                animators.add(gone);
            }

            AnimatorSet set = new AnimatorSet();
            set.playTogether(animators);
            set.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationStart(Animator animation) {
                    isAnimating = true;
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    isMenuShowing = false;
                    isAnimating = false;
                }
            });
            set.start();
        }
    }
}
