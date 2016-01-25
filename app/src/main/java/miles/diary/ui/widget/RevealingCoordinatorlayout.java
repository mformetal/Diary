package miles.diary.ui.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.Property;

import miles.diary.R;
import miles.diary.util.ViewUtils;

/**
 * Created by mbpeele on 1/20/16.
 */
public class RevealingCoordinatorLayout extends CoordinatorLayout {

    private Paint mPaint;
    private Path mPath;

    private float mRadius;

    public RevealingCoordinatorLayout(Context context) {
        super(context);
        init();
    }

    public RevealingCoordinatorLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public RevealingCoordinatorLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mPath = new Path();
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(ContextCompat.getColor(getContext(), R.color.accent));
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        mPath.rewind();
        mPath.addCircle(getPivotX(), getPivotY(), mRadius, Path.Direction.CCW);
        canvas.clipPath(mPath);
        super.dispatchDraw(canvas);
    }

    public float getRadius() {
        return mRadius;
    }

    public void setRadius(float cornerRadius) {
        this.mRadius = cornerRadius;
        invalidate(ViewUtils.boundingRect(getPivotX(), getPivotY(), mRadius));
    }

    public static final Property<RevealingCoordinatorLayout, Float> RADIUS
            = new ViewUtils.FloatProperty<RevealingCoordinatorLayout>("mRadius") {

        @Override
        public void setValue(RevealingCoordinatorLayout morphDrawable, float value) {
            morphDrawable.setRadius(value);
        }

        @Override
        public Float get(RevealingCoordinatorLayout morphDrawable) {
            return morphDrawable.getRadius();
        }
    };
}
