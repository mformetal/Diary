package miles.diary.ui.widget;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v7.widget.AppCompatButton;
import android.util.AttributeSet;

import miles.diary.R;
import miles.diary.util.TextUtils;

/**
 * Created by mbpeele on 1/14/16.
 */
public class TypefaceButton extends AppCompatButton {

    public TypefaceButton(Context context) {
        super(context);
        init(context, null);
    }

    public TypefaceButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public TypefaceButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        if (isInEditMode()) {
            return;
        }

        if (attrs != null) {
            TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.TypefaceButton);

            int color = array.getColor(R.styleable.TypefaceButton_tintDrawable, Color.TRANSPARENT);
            if (color != Color.TRANSPARENT) {
                tintDrawables(color);
            }

            array.recycle();
        }

        setTypeface(TextUtils.getDefaultFont(getContext()));
    }

    private void tintDrawables(final int color) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            setCompoundDrawableTintList(ColorStateList.valueOf(color));
        } else {
            for (Drawable drawable: getCompoundDrawables()) {
                if (drawable != null) {
                    drawable.mutate().setColorFilter(color, PorterDuff.Mode.SRC_IN);
                }
            }
        }
    }
}
