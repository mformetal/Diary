package miles.forum.ui.drawable;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorInt;
import android.util.Property;

import miles.forum.util.ViewUtils;

/**
 * Created by mbpeele on 1/16/16.
 */
public class FabDialogDrawable extends Drawable {

    private float mRadius;
    private Paint mPaint;

    public FabDialogDrawable(@ColorInt int color, float cornerRadius) {
        this.mRadius = cornerRadius;
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(color);
    }

    public float getRadius() {
        return mRadius;
    }

    public void setRadius(float cornerRadius) {
        this.mRadius = cornerRadius;
        invalidateSelf();
    }

    public int getColor() {
        return mPaint.getColor();
    }

    public void setColor(int color) {
        mPaint.setColor(color);
        invalidateSelf();
    }

    @Override
    public void draw(Canvas canvas) {
        canvas.drawCircle(getBounds().centerX(), getBounds().centerY(), mRadius, mPaint);
    }

    @Override
    public void setAlpha(int alpha) {
        mPaint.setAlpha(alpha);
        invalidateSelf();
    }

    @Override
    public void setColorFilter(ColorFilter cf) {
        mPaint.setColorFilter(cf);
        invalidateSelf();
    }

    @Override
    public int getOpacity() {
        return mPaint.getAlpha();
    }

    public static final Property<FabDialogDrawable, Float> RADIUS
            = new ViewUtils.FloatProperty<FabDialogDrawable>("mRadius") {

        @Override
        public void setValue(FabDialogDrawable morphDrawable, float value) {
            morphDrawable.setRadius(value);
        }

        @Override
        public Float get(FabDialogDrawable morphDrawable) {
            return morphDrawable.getRadius();
        }
    };
}