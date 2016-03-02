package miles.diary.ui.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;

import miles.diary.R;
import miles.diary.util.Logg;

/**
 * Created by mbpeele on 1/28/16.
 */
public class NotebookEditText extends TypefaceEditText {

    private Paint mPaint;
    private Rect mRect;

    private int mFocusedColor, mUnfocusedColor;
    private float defaultWidth;

    public NotebookEditText(Context context) {
        super(context);
        init();
    }

    public NotebookEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public NotebookEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        if (isInEditMode()) {
            return;
        }

        mRect = new Rect();

        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        defaultWidth = mPaint.getStrokeWidth();

        mFocusedColor = ContextCompat.getColor(getContext(), R.color.accent);
        mUnfocusedColor = Color.WHITE;

        setBackground(null);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (hasFocus()) {
            mPaint.setColor(mFocusedColor);
            mPaint.setStrokeWidth(5f);
        } else {
            mPaint.setColor(mUnfocusedColor);
            mPaint.setStrokeWidth(defaultWidth);
        }

        int height = getHeight();
        int count = height / getLineHeight();

        if (getLineCount() > count) {
            count = getLineCount();
        }

        int baseline = getLineBounds(0, mRect) + Math.round(height * .01f);

        for (int i = 0; i < count; i++) {
            int bottom = baseline + 1;
            canvas.drawLine(mRect.left, bottom, mRect.right, bottom, mPaint);
            baseline += getLineHeight();
        }

        super.onDraw(canvas);
    }
}
