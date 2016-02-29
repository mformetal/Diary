package miles.diary.util;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.NonNull;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by mbpeele on 1/14/16.
 */
public class ViewUtils {

    private static final Rect RECT = new Rect();

    private static int actionBarSize = -1;

    private ViewUtils() {}

    public static void setLightStatusBar(@NonNull View view) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int flags = view.getSystemUiVisibility();
            flags |= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
            view.setSystemUiVisibility(flags);
        }
    }

    public static Drawable mutate(Drawable drawable, int color) {
        drawable.mutate().setColorFilter(color, PorterDuff.Mode.SRC_IN);
        return drawable;
    }

    public static Drawable mutate(ImageView imageView, int color) {
        Drawable drawable = imageView.getDrawable();
        drawable.mutate().setColorFilter(color, PorterDuff.Mode.SRC_IN);
        return drawable;
    }

    public static void mutate(Drawable[] drawables, int color) {
        for (Drawable drawable: drawables) {
            if (drawable != null) {
                mutate(drawable, color);
            }
        }
    }

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

    public static float pxToDp(final float px) {
        return px / Resources.getSystem().getDisplayMetrics().density;
    }

    public static float dpToPx(final float dp) {
        return dp * Resources.getSystem().getDisplayMetrics().density;
    }
}
