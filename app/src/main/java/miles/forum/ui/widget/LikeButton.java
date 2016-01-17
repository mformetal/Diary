package miles.forum.ui.widget;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatImageButton;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;

import miles.forum.R;
import miles.forum.util.Logg;

/**
 * Created by mbpeele on 1/15/16.
 */
public class LikeButton extends TypefaceButton {

    private final static Interpolator DECELERATE = new DecelerateInterpolator();
    private Drawable mEmptyStar, mFilledStar;

    private boolean hasLiked = false;

    public LikeButton(Context context) {
        super(context);
        init();
    }

    public LikeButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public LikeButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mEmptyStar = ContextCompat.getDrawable(getContext(), R.drawable.ic_star_24dp_empty);
        mFilledStar = ContextCompat.getDrawable(getContext(), R.drawable.ic_star_24dp_filled);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                animate().scaleX(0.7f).scaleY(0.7f).setDuration(150).setInterpolator(DECELERATE);
                break;

            case MotionEvent.ACTION_MOVE:
                float x = event.getX();
                float y = event.getY();
                boolean isInside = (x > 0 && x < getWidth() && y > 0 && y < getHeight());
                if (isPressed() != isInside) {
                    setPressed(isInside);
                }
                break;

            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                animate().scaleX(1).scaleY(1).setInterpolator(DECELERATE);
                if (isPressed()) {
                    performClick();
                    setPressed(false);
                }

                setLiked(!hasLiked, true);
                break;
        }

        return super.onTouchEvent(event);
    }

    public boolean hasLiked() {
        return hasLiked;
    }

    public void setLiked(boolean liked, boolean updateText) {
        hasLiked = liked;
        if (hasLiked) {
            setCompoundDrawablesWithIntrinsicBounds(mFilledStar,
                    null, null, null);
        } else {
            setCompoundDrawablesWithIntrinsicBounds(mEmptyStar,
                    null, null, null);
        }

        if (updateText) {
            int oldValue = Integer.valueOf(getTextAsString());
            int newValue = (!hasLiked) ? oldValue - 1 : oldValue + 1;
            setText(String.valueOf(newValue));
        }
    }
}
