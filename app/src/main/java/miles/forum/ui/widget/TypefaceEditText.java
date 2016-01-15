package miles.forum.ui.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.EditText;

import miles.forum.util.TextUtils;

/**
 * Created by mbpeele on 1/14/16.
 */
public class TypefaceEditText extends EditText {

    public TypefaceEditText(Context context) {
        super(context);
        init();
    }

    public TypefaceEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public TypefaceEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public TypefaceEditText(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        if (isInEditMode()) {
            return;
        }

        setTypeface(TextUtils.getDefaultFont(getContext()));
    }
}
