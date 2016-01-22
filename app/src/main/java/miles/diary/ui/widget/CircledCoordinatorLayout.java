package miles.diary.ui.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.Property;
import android.view.View;

import miles.diary.R;
import miles.diary.util.ViewUtils;

/**
 * Created by mbpeele on 1/20/16.
 */
public class CircledCoordinatorLayout extends CoordinatorLayout {

    private Paint mPaint;
    private Path mPath;

    private float mRadius;

    public CircledCoordinatorLayout(Context context) {
        super(context);
        init();
    }

    public CircledCoordinatorLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CircledCoordinatorLayout(Context context, AttributeSet attrs, int defStyleAttr) {
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

    public static final Property<CircledCoordinatorLayout, Float> RADIUS
            = new ViewUtils.FloatProperty<CircledCoordinatorLayout>("mRadius") {

        @Override
        public void setValue(CircledCoordinatorLayout morphDrawable, float value) {
            morphDrawable.setRadius(value);
        }

        @Override
        public Float get(CircledCoordinatorLayout morphDrawable) {
            return morphDrawable.getRadius();
        }
    };
}
