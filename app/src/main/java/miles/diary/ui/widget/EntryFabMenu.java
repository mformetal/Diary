package miles.diary.ui.widget;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.content.Context;
import android.support.design.widget.FloatingActionButton;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnticipateOvershootInterpolator;
import android.view.animation.OvershootInterpolator;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import miles.diary.R;
import miles.diary.util.Logg;
import miles.diary.util.ViewUtils;

/**
 * Created by mbpeele on 1/28/16.
 */
public class EntryFabMenu extends ViewGroup {

    @Bind(R.id.activity_entry_toggle_menu) FloatingActionButton toggle;

    private final static int INITIAL_DELAY = 0;
    private final static int DURATION = 650;
    private final static int DELAY_INCREMENT = 15;
    private final static int HIDE_DIFF = 50;
    private boolean isAnimating = false;
    private boolean isMenuShowing = true;

    public EntryFabMenu(Context context) {
        super(context);
    }

    public EntryFabMenu(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public EntryFabMenu(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public EntryFabMenu(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        ButterKnife.bind(this);
        toggle.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleMenu();
            }
        });
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        measureChildWithMargins(toggle, widthMeasureSpec, 0, heightMeasureSpec, 0);

        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            if (child == toggle) continue;
            measureChildWithMargins(child, widthMeasureSpec, 0, heightMeasureSpec, 0);
        }

        int width = getDefaultSize(getSuggestedMinimumWidth(), widthMeasureSpec);
        int height = getDefaultSize(getSuggestedMinimumHeight(), heightMeasureSpec);

        setMeasuredDimension(width, height);
    }

    @Override
    protected LayoutParams generateDefaultLayoutParams() {
        return new MarginLayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
    }

    @Override
    protected LayoutParams generateLayoutParams(LayoutParams p) {
        return new MarginLayoutParams(p);
    }

    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new MarginLayoutParams(getContext(), attrs);
    }


    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if (!changed) {
            return;
        }

        MarginLayoutParams lps = (MarginLayoutParams) toggle.getLayoutParams();

        toggle.layout(r - toggle.getMeasuredWidth() - lps.rightMargin,
                getMeasuredHeight() - toggle.getMeasuredHeight() - lps.bottomMargin,
                r - lps.rightMargin,
                getMeasuredHeight() - lps.bottomMargin);

        int cy = Math.round((toggle.getTop() + toggle.getBottom()) / 2f);

        float remainingWidth = toggle.getLeft();

        for (int i = 0; i < getChildCount(); i++) {
            final View child = getChildAt(i);
            if (child.getId() == R.id.activity_entry_toggle_menu) {
                continue;
            }

            float cx = remainingWidth * ((float) (i + 1) / getChildCount());

            child.layout(Math.round(cx - child.getMeasuredWidth() / 2),
                    cy - child.getMeasuredHeight() / 2,
                    Math.round(cx + child.getMeasuredWidth() / 2),
                    cy + child.getMeasuredHeight() / 2);
        }
    }

    private void rotateToggleOpen() {
        ObjectAnimator.ofFloat(toggle, View.ROTATION,
                toggle.getRotation(), toggle.getRotation() - 135f).start();
    }

    private void rotateToggleClosed() {
        ObjectAnimator.ofFloat(toggle, View.ROTATION,
                toggle.getRotation(), toggle.getRotation() - 135f)
                .setDuration(HIDE_DIFF + DURATION + DELAY_INCREMENT * getChildCount() - 1)
                .start();
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

    public void showMenu() {
        if (!isMenuShowing && !isAnimating) {
            rotateToggleOpen();

            ArrayList<Animator> animators = new ArrayList<>();
            int delay = INITIAL_DELAY;
            for (int i = 0; i < getChildCount(); i++) {
                View view = getChildAt(i);
                if (view.getId() == R.id.activity_entry_toggle_menu) {
                    continue;
                }

                ObjectAnimator visible = ObjectAnimator.ofPropertyValuesHolder(view,
                        PropertyValuesHolder.ofFloat(View.ALPHA, 1f),
                        PropertyValuesHolder.ofFloat(View.TRANSLATION_X, 0f));
                visible.setDuration(DURATION);
                visible.setInterpolator(new OvershootInterpolator());
                visible.setStartDelay(delay);

                delay += DELAY_INCREMENT;

                animators.add(visible);
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

    public void hideMenu() {
        if (isMenuShowing && !isAnimating) {
            rotateToggleClosed();

            ArrayList<Animator> animators = new ArrayList<>();
            int delay = INITIAL_DELAY;
            for (int i = 0; i < getChildCount(); i++) {
                View view = getChildAt(i);
                if (view.getId() == R.id.activity_entry_toggle_menu) {
                    continue;
                }

                ObjectAnimator gone = ObjectAnimator.ofPropertyValuesHolder(view,
                        PropertyValuesHolder.ofFloat(View.ALPHA, 0f),
                        PropertyValuesHolder.ofFloat(View.TRANSLATION_X,
                                ViewUtils.centerX(toggle) - ViewUtils.centerX(view)));
                gone.setInterpolator(new AnticipateOvershootInterpolator());
                gone.setDuration(DURATION);
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
