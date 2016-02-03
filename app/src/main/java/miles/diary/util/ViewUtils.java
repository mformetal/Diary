package miles.diary.util;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.Property;
import android.util.TypedValue;
import android.view.View;

/**
 * Created by mbpeele on 1/14/16.
 */
public class ViewUtils {

    private static final Rect RECT = new Rect();

    private static int actionBarSize = -1;

    private ViewUtils() {}

    public static int getActionBarSize(Context context) {
        if (actionBarSize < 0) {
            TypedValue value = new TypedValue();
            context.getTheme().resolveAttribute(android.R.attr.actionBarSize, value, true);
            actionBarSize = TypedValue.complexToDimensionPixelSize(value.data, context
                    .getResources().getDisplayMetrics());
        }
        return actionBarSize;
    }

    public static float displayHeight(Context context) {
        Point point = new Point();
        ((Activity) context).getWindow().getWindowManager().getDefaultDisplay().getRealSize(point);
        return point.y;
    }

    public static int dominantMeasurement(View view) {
        return (view.getHeight() > view.getWidth()) ? view.getHeight() : view.getWidth();
    }

    public static int centerX(View view) {
        return (view.getLeft() + view.getRight()) / 2;
    }

    public static int centerY(View view) {
        return (view.getTop() + view.getBottom()) / 2;
    }

    public static Rect boundingRect(float cx, float cy, float radius) {
        RECT.set((int) (cx - radius), (int) (cy - radius), (int) (cx + radius), (int) (cy + radius));
        return RECT;
    }

    public static abstract class FloatProperty<T> extends Property<T, Float> {
        public FloatProperty(String name) {
            super(Float.class, name);
        }

        public abstract void setValue(T object, float value);

        @Override
        final public void set(T object, Float value) {
            setValue(object, value);
        }
    }

    public static abstract class IntProperty<T> extends Property<T, Integer> {

        public IntProperty(String name) {
            super(Integer.class, name);
        }

        public abstract void setValue(T object, int value);

        @Override
        final public void set(T object, Integer value) {
            setValue(object, value);
        }
    }

}
