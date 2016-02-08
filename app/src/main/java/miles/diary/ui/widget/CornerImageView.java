package miles.diary.ui.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.widget.ImageView;

import miles.diary.util.AnimUtils;
import miles.diary.util.Logg;

/**
 * Created by mbpeele on 2/5/16.
 */
public class CornerImageView extends ImageView {

    private Path path;
    private RectF bounds;

    private float corner;

    public CornerImageView(Context context) {
        super(context);
        init();
    }

    public CornerImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CornerImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public CornerImageView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        path = new Path();
        bounds = new RectF();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        bounds.set(0, 0, w, h);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (corner > 0) {
            canvas.clipPath(path);
        }
        super.onDraw(canvas);
    }

    public void setCornerRadius(float value) {
        corner = value;
        path.rewind();
        path.addRoundRect(bounds, corner, corner, Path.Direction.CW);
        invalidate();
    }

    public float getCornerRadius() {
        return corner;
    }

    public final static AnimUtils.FloatProperty<CornerImageView> CORNERS =
            new AnimUtils.FloatProperty<CornerImageView>("corners") {
        @Override
        public void setValue(CornerImageView object, float value) {
            object.setCornerRadius(value);
        }

        @Override
        public Float get(CornerImageView object) {
            return object.getCornerRadius();
        }
    };
}
