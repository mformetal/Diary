package miles.diary.ui.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.Property;
import android.view.View;
import android.widget.LinearLayout;

import miles.diary.R;
import miles.diary.ui.drawable.FabDialogDrawable;
import miles.diary.util.Logg;
import miles.diary.util.ViewUtils;

/**
 * Created by mbpeele on 1/19/16.
 */
public class CircledLinearLayout extends LinearLayout {

    private float mRadius;
    private Paint mPaint;
    
    public CircledLinearLayout(Context context) {
        super(context);
        init();
    }

    public CircledLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CircledLinearLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public CircledLinearLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(ContextCompat.getColor(getContext(), R.color.accent));
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        canvas.drawCircle(canvas.getWidth() / 2f, canvas.getHeight() / 2f, mRadius, mPaint);
        super.dispatchDraw(canvas);
    }

    public float getRadius() {
        return mRadius;
    }

    public void setRadius(float cornerRadius) {
        this.mRadius = cornerRadius;
        invalidate();
    }

    public int getColor() {
        return mPaint.getColor();
    }

    public void setColor(int color) {
        mPaint.setColor(color);
        invalidate();
    }

    public static final Property<CircledLinearLayout, Integer> COLOR
            = new ViewUtils.IntProperty<CircledLinearLayout>("color") {
        @Override
        public Integer get(CircledLinearLayout object) {
            return object.getColor();
        }

        @Override
        public void setValue(CircledLinearLayout object, int value) {
            object.setColor(value);
        }
    };

    public static final Property<CircledLinearLayout, Float> RADIUS
            = new ViewUtils.FloatProperty<CircledLinearLayout>("mRadius") {

        @Override
        public void setValue(CircledLinearLayout morphDrawable, float value) {
            morphDrawable.setRadius(value);
        }

        @Override
        public Float get(CircledLinearLayout morphDrawable) {
            return morphDrawable.getRadius();
        }
    };
}
