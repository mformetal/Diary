package miles.diary.ui.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;
import android.widget.TextView;

import me.grantland.widget.AutofitHelper;
import miles.diary.R;
import miles.diary.util.TextUtils;

/**
 * Created by mbpeele on 1/14/16.
 */
public class TypefaceTextView extends TextView {

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

            boolean autofit = array.getBoolean(R.styleable.TypefaceTextView_textViewAutoFit, false);
            if (autofit) {
                AutofitHelper helper = AutofitHelper.create(this, attrs);
                helper.setTextSize(getTextSize());
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
