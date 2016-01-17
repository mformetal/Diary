package miles.forum.ui.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Build;
import android.support.v7.widget.AppCompatButton;
import android.util.AttributeSet;
import android.widget.Button;

import miles.forum.R;
import miles.forum.util.TextUtils;

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
