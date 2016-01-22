package miles.diary.ui.widget;

import android.content.Context;
import android.graphics.Paint;
import android.support.v7.widget.AppCompatButton;
import android.util.AttributeSet;

import miles.diary.util.TextUtils;

/**
 * Created by mbpeele on 1/14/16.
 */
public class TypefaceButton extends AppCompatButton {

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

        setTypeface(TextUtils.getDefaultFont(getContext()));
    }

    public String getTextAsString() {
        return getText().toString();
    }
}
