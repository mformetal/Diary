package mformetal.diary.ui.drawable;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Outline;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorInt;
import android.util.Property;

import mformetal.diary.util.AnimUtils;

public class RoundRectDrawable extends Drawable {

    private float cornerRadius;
    private Paint paint;

    public RoundRectDrawable(@ColorInt int color, float cornerRadius) {
        this.cornerRadius = cornerRadius;
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(color);
    }

    public RoundRectDrawable(@ColorInt int color) {
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(color);
    }

    public float getCornerRadius() {
        return cornerRadius;
    }

    public void setCornerRadius(float cornerRadius) {
        this.cornerRadius = cornerRadius;
        invalidateSelf();
    }

    public int getColor() {
        return paint.getColor();
    }

    public void setColor(int color) {
        paint.setColor(color);
        invalidateSelf();
    }

    @Override
    public void draw(Canvas canvas) {
        canvas.drawRoundRect(getBounds().left, getBounds().top, getBounds().right, getBounds()
                .bottom, cornerRadius, cornerRadius, paint);
    }

    @Override
    public void getOutline(Outline outline) {
        outline.setRoundRect(getBounds(), cornerRadius);
    }

    @Override
    public void setAlpha(int alpha) {
        paint.setAlpha(alpha);
        invalidateSelf();
    }

    @Override
    public void setColorFilter(ColorFilter cf) {
        paint.setColorFilter(cf);
        invalidateSelf();
    }

    @Override
    public int getOpacity() {
        return paint.getAlpha();
    }

    public static final Property<RoundRectDrawable, Float> CORNER_RADIUS
            = new AnimUtils.FloatProperty<RoundRectDrawable>("cornerRadius") {

        @Override
        public void setValue(RoundRectDrawable roundRectDrawable, float value) {
            roundRectDrawable.setCornerRadius(value);
        }

        @Override
        public Float get(RoundRectDrawable roundRectDrawable) {
            return roundRectDrawable.getCornerRadius();
        }
    };

    public static final Property<RoundRectDrawable, Integer> COLOR =
            new AnimUtils.IntProperty<RoundRectDrawable>("color") {

        @Override
        public void setValue(RoundRectDrawable roundRectDrawable, int value) {
            roundRectDrawable.setColor(value);
        }

        @Override
        public Integer get(RoundRectDrawable roundRectDrawable) {
            return roundRectDrawable.getColor();
        }
    };
}
