package mformetal.diary.ui.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;

import mformetal.diary.R;
import mformetal.diary.util.TextUtils;

/**
 * Created by mbpeele on 1/14/16.
 */
public class TypefaceTextView extends AppCompatTextView {

    public TypefaceTextView(Context context) {
        super(context);
        init(null);
    }

    public TypefaceTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public TypefaceTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        if (isInEditMode()) {
            return;
        }

        if (attrs != null) {
            TypedArray array = getContext().obtainStyledAttributes(attrs, R.styleable.TypefaceTextView);
            String font = array.getString(R.styleable.TypefaceTextView_textViewFont);
            if (font != null) {
                setTypeface(TextUtils.getFont(getContext(), font));
            } else {
                setTypeface(TextUtils.getDefaultFont(getContext()));
            }
            array.recycle();
        } else {
            setTypeface(TextUtils.getDefaultFont(getContext()));
        }
    }

    public String getStringText() {
        return getText().toString();
    }
}
