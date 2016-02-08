package miles.diary.ui.widget;

import android.content.Context;
import android.util.AttributeSet;

/**
 * Created by mbpeele on 2/8/16.
 */
public class MaxCornerImageView extends CornerImageView {

    public MaxCornerImageView(Context context) {
        super(context);
    }

    public MaxCornerImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MaxCornerImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public MaxCornerImageView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int max = Math.max(getMeasuredHeight(), getMeasuredWidth());
        setMeasuredDimension(max, max);
    }
}
