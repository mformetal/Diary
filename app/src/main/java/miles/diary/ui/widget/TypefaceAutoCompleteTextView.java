package miles.diary.ui.widget;

import android.content.Context;
import android.support.v7.widget.AppCompatAutoCompleteTextView;
import android.util.AttributeSet;
import android.widget.AutoCompleteTextView;

import miles.diary.ui.CancelListener;
import miles.diary.util.TextUtils;

/**
 * Created by mbpeele on 2/6/16.
 */
public class TypefaceAutoCompleteTextView extends AppCompatAutoCompleteTextView {

    public TypefaceAutoCompleteTextView(Context context) {
        super(context);
        init();
    }

    public TypefaceAutoCompleteTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public TypefaceAutoCompleteTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        if (isInEditMode()) {
            return;
        }

        setTypeface(TextUtils.getDefaultFont(getContext()));
        new CancelListener(this);
    }
}
