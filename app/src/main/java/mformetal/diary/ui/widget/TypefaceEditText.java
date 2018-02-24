package mformetal.diary.ui.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v7.widget.AppCompatEditText;
import android.util.AttributeSet;
import android.view.inputmethod.InputMethodManager;

import mformetal.diary.R;
import mformetal.diary.util.TextUtils;

/**
 * Created by mbpeele on 1/14/16.
 */
public class TypefaceEditText extends AppCompatEditText {

    public TypefaceEditText(Context context) {
        super(context);
        init(null);
    }

    public TypefaceEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public TypefaceEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        if (isInEditMode()) {
            return;
        }

        if (attrs != null) {
            TypedArray array = getContext().obtainStyledAttributes(attrs, R.styleable.TypefaceEditText);
            String font = array.getString(R.styleable.TypefaceTextView_textViewFont);
            if (font != null) {
                setTypeface(TextUtils.getFont(getContext(), font));
            } else {
                setTypeface(TextUtils.getDefaultFont(getContext()));
            }
            array.recycle();
        }

        setTypeface(TextUtils.getDefaultFont(getContext()));
    }

    public String getTextAsString() {
        return getText().toString();
    }

    public void openKeyboard() {
        InputMethodManager imm
                = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(this, InputMethodManager.SHOW_FORCED);
    }

    public void closeKeyboard() {
        InputMethodManager imm
                = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getWindowToken(), 0);
    }
}
