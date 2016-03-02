package miles.diary.ui.widget;

import android.content.Context;
import android.util.AttributeSet;

import com.joanzapata.iconify.widget.IconTextView;

import miles.diary.util.TextUtils;

/**
 * Created by mbpeele on 2/15/16.
 */
public class TypefaceIconTextView extends IconTextView {

    public TypefaceIconTextView(Context context) {
        super(context);
        init();
    }

    public TypefaceIconTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public TypefaceIconTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        if (isInEditMode()) {
            return;
        }

        setTypeface(TextUtils.getDefaultFont(getContext()));
    }

    public String getStringText() {
        return getText().toString();
    }
}
