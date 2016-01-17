package miles.forum.ui.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;
import android.widget.TextView;

import miles.forum.util.Logg;
import miles.forum.util.TextUtils;

/**
 * Created by mbpeele on 1/14/16.
 */
public class TypefaceTextView extends AppCompatTextView {

    public TypefaceTextView(Context context) {
        super(context);
        init();
    }

    public TypefaceTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public TypefaceTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        if (isInEditMode()) {
            return;
        }

        setTypeface(TextUtils.getDefaultFont(getContext()));
    }
}
