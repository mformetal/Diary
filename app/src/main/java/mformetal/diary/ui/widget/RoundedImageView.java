package mformetal.diary.ui.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.widget.ImageView;

import mformetal.diary.util.AnimUtils;

/**
 * Created by mbpeele on 2/5/16.
 */
public class RoundedImageView extends ImageView {

    private Path path;
    private RectF bounds;

    private float corner;

    public RoundedImageView(Context context) {
        super(context);
        init();
    }

    public RoundedImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public RoundedImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public RoundedImageView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        if (isInEditMode()) {
            return;
        }

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

    public final static AnimUtils.FloatProperty<RoundedImageView> CORNERS =
            new AnimUtils.FloatProperty<RoundedImageView>("corners") {
        @Override
        public void setValue(RoundedImageView object, float value) {
            object.setCornerRadius(value);
        }

        @Override
        public Float get(RoundedImageView object) {
            return object.getCornerRadius();
        }
    };
}
