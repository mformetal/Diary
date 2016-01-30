package miles.diary.ui.widget;

import android.content.Context;
import android.util.AttributeSet;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by mbpeele on 1/28/16.
 */
public class SquaredCircleImageView extends CircleImageView {

    public SquaredCircleImageView(Context context) {
        super(context);
    }

    public SquaredCircleImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SquaredCircleImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int width = getMeasuredWidth();
        setMeasuredDimension(width, width);
    }
}
