package miles.diary.ui.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Paint;
import android.support.v7.widget.AppCompatButton;
import android.util.AttributeSet;
import android.widget.Button;

import me.grantland.widget.AutofitHelper;
import miles.diary.R;
import miles.diary.util.TextUtils;

/**
 * Created by mbpeele on 1/14/16.
 */
public class TypefaceButton extends Button {

    private Paint mBorderPaint;

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

            boolean autofit = array.getBoolean(R.styleable.TypefaceButton_buttonAutofit, false);
            if (autofit) {
                AutofitHelper helper = AutofitHelper.create(this, attrs);
                helper.setTextSize(getTextSize());
            }
            array.recycle();
        }

        setTypeface(TextUtils.getDefaultFont(getContext()));
    }

    public String getTextAsString() {
        return getText().toString();
    }
}
