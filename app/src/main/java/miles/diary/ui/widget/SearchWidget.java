package miles.diary.ui.widget;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.AutoCompleteTextView;

import java.util.ArrayList;
import java.util.List;

import miles.diary.R;
import miles.diary.ui.CancelDetector;
import miles.diary.util.AnimUtils;
import miles.diary.util.Logg;

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

    public boolean onBack() {
        if (getVisibility() == View.VISIBLE) {
            toggle(new int[] { getWidth(), 0 });
            return true;
        }

        return false;
    }

    public void addSearchListener(SearchListener searchListener) {
        listeners.add(searchListener);
    }

    public void callListeners(int[] position, boolean visible) {
        for (SearchListener listener : listeners) {
            if (visible) {
                listener.onSearchShow(position);
            } else {
                listener.onSearchDismiss(position);
            }
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

            callListeners(clickPosition, false);
        } else {
            ObjectAnimator alpha = AnimUtils.visible(this);
            ObjectAnimator scale = ObjectAnimator.ofFloat(this, SCALE_X, 0f, 1f);

            AnimatorSet animatorSet = new AnimatorSet();
            animatorSet.play(alpha).with(scale);
            animatorSet.setInterpolator(new AccelerateDecelerateInterpolator());
            animatorSet.start();

            callListeners(clickPosition, true);
        }
    }

    public interface SearchListener {

        void onSearchShow(int[] position);

        void onSearchDismiss(int[] position);
    }
}
