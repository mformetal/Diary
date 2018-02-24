package mformetal.diary.ui.widget;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;

import java.util.ArrayList;
import java.util.List;

import mformetal.diary.R;
import mformetal.diary.ui.CancelDetector;
import mformetal.diary.util.SimpleTextWatcher;
import mformetal.diary.util.AnimUtils;

/**
 * Created by mbpeele on 3/11/16.
 */
public class SearchWidget extends TypefaceEditText {

    private List<SearchListener> listeners;

    public SearchWidget(Context context) {
        super(context);
        init();
    }

    public SearchWidget(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SearchWidget(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        Drawable back = ContextCompat.getDrawable(getContext(), R.drawable.ic_arrow_back_24dp);
        Drawable reset = ContextCompat.getDrawable(getContext(), R.drawable.ic_close_24dp);

        setCompoundDrawablesRelativeWithIntrinsicBounds(back, null, reset, null);

        new CancelDetector(this);

        addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                callTextChangedListener(s);
            }
        });

        listeners = new ArrayList<>();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Drawable back = getCompoundDrawables()[0];
        if (getVisibility() == View.VISIBLE &&
                back != null && event.getX() < back.getIntrinsicWidth() + getPaddingLeft()) {
            toggle(new int[] { getWidth(), 0 });
        }
        return super.onTouchEvent(event);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        setPivotX(w);
    }

    public boolean interceptBackButton() {
        if (getVisibility() == View.VISIBLE) {
            if (!getTextAsString().isEmpty()) {
                setText("");
            }

            toggle(new int[] { getWidth(), 0 });
            return true;
        }

        return false;
    }

    public void addSearchListener(SearchListener searchListener) {
        listeners.add(searchListener);
    }

    private void callVisibilityListener(int[] position, boolean visible) {
        for (SearchListener listener : listeners) {
            if (visible) {
                listener.onSearchShow(position);
            } else {
                listener.onSearchDismiss(position);
            }
        }
    }

    private void callTextChangedListener(CharSequence charSequence) {
        for (SearchListener listener: listeners) {
            listener.onSearchTextChanged(charSequence.toString());
        }
    }

    public void toggle(int[] clickPosition) {
        if (getVisibility() == View.VISIBLE) {
            ObjectAnimator alpha = AnimUtils.gone(this);
            ObjectAnimator scale = ObjectAnimator.ofFloat(this, SCALE_X, 1f, 0f);

            AnimatorSet animatorSet = new AnimatorSet();
            animatorSet.play(alpha).with(scale);
            animatorSet.setInterpolator(new AccelerateDecelerateInterpolator());
            animatorSet.start();

            callVisibilityListener(clickPosition, false);
        } else {
            ObjectAnimator alpha = AnimUtils.visible(this);
            ObjectAnimator scale = ObjectAnimator.ofFloat(this, SCALE_X, 0f, 1f);

            AnimatorSet animatorSet = new AnimatorSet();
            animatorSet.play(alpha).with(scale);
            animatorSet.setInterpolator(new AccelerateDecelerateInterpolator());
            animatorSet.start();

            callVisibilityListener(clickPosition, true);
        }
    }

    public interface SearchListener {

        void onSearchShow(int[] position);

        void onSearchTextChanged(String text);

        void onSearchDismiss(int[] position);
    }
}
