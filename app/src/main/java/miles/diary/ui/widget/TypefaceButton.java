package miles.diary.ui.widget;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;

import me.grantland.widget.AutofitHelper;
import miles.diary.DiaryApplication;
import miles.diary.R;
import miles.diary.ui.PreDrawer;
import miles.diary.util.Logg;
import miles.diary.util.TextUtils;

/**
 * Created by mbpeele on 1/14/16.
 */
public class TypefaceButton extends Button {

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

            if (array.getBoolean(R.styleable.TypefaceButton_buttonAutofit, false)) {
                AutofitHelper helper = AutofitHelper.create(this, attrs);
                helper.setTextSize(getTextSize());
            }

            int color = array.getColor(R.styleable.TypefaceButton_tintDrawable, Color.TRANSPARENT);
            if (color != Color.TRANSPARENT) {
                tintDrawables(color);
            }

            array.recycle();
        }

        setTypeface(TextUtils.getDefaultFont(getContext()));
    }

    private void tintDrawables(final int color) {
        new PreDrawer<Button>(this) {
            @Override
            public void notifyPreDraw(Button view) {
                Drawable[] drawables = getCompoundDrawables();
                for (Drawable drawable: drawables) {
                    if (drawable != null) {
                        drawable.mutate().setColorFilter(color, PorterDuff.Mode.SRC_IN);
                    }
                }
            }
        };
    }
}
