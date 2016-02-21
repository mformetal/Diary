package miles.diary.ui.widget;

import android.content.Context;
import android.util.AttributeSet;

/**
 * Created by mbpeele on 2/8/16.
 */
public class SquareCornerImageView extends CornerImageView {

    public SquareCornerImageView(Context context) {
        super(context);
    }

    public SquareCornerImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SquareCornerImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int max = Math.max(getMeasuredHeight(), getMeasuredWidth());
        setMeasuredDimension(max, max);
    }
}
