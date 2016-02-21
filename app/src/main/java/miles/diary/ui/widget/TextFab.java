package miles.diary.ui.widget;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AnticipateOvershootInterpolator;

import miles.diary.R;
import miles.diary.util.AnimUtils;
import miles.diary.util.TextUtils;

/**
 * Created by mbpeele on 2/4/16.
 */
public class TextFab extends FloatingActionButton {

    private Paint paint;
    private String text;

    private float textWidth, textHeight, textScale;

    public TextFab(Context context) {
        super(context);
        init();
    }

    public TextFab(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public TextFab(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        paint = new Paint();
        paint.setColor(Color.WHITE);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setTypeface(TextUtils.getDefaultFont(getContext()));
        paint.setStyle(Paint.Style.FILL_AND_STROKE);

        setWillNotDraw(false);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        paint.setTextSize(w / 4f);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (text != null) {
            Rect bounds = canvas.getClipBounds();

            canvas.save();
            canvas.scale(textScale, textScale, bounds.exactCenterX(), bounds.exactCenterY());
            canvas.drawText(text,
                    bounds.exactCenterX(),
                    bounds.exactCenterY() + textHeight / 2f,
                    paint);
            canvas.restore();
        }
    }

    public void setText(String string) {
        Rect textBounds = new Rect();
        paint.getTextBounds(string, 0, string.length(), textBounds);
        textWidth = paint.measureText(string);
        textHeight = textBounds.height();

        int toColor = getBackgroundTintList() == null ?
                Color.WHITE : getBackgroundTintList().getDefaultColor();
        ValueAnimator valueAnimator = ValueAnimator.ofArgb(
                ContextCompat.getColor(getContext(), R.color.accent),
                toColor);
        valueAnimator.setDuration(AnimUtils.mediumAnim(getContext()));
        valueAnimator.setInterpolator(new AccelerateInterpolator());
        valueAnimator.addUpdateListener(animation -> setColorFilter((int) animation.getAnimatedValue()));
        valueAnimator.start();

        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(this, SCALER, 1f);
        objectAnimator.setDuration(AnimUtils.mediumAnim(getContext()));
        objectAnimator.setInterpolator(new AnticipateOvershootInterpolator());
        objectAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                text = string;
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                setImageDrawable(null);
            }
        });
        objectAnimator.start();
    }

    public float getTextScale() {
        return textScale;
    }

    public void setTextScale(float scale) {
        textScale = scale;
        invalidate();
    }

    private final static AnimUtils.FloatProperty<TextFab> SCALER
            = new AnimUtils.FloatProperty<TextFab>("scaler") {
        @Override
        public void setValue(TextFab object, float value) {
            object.setTextScale(value);
        }

        @Override
        public Float get(TextFab object) {
            return object.getTextScale();
        }
    };
}