package miles.diary.ui.widget;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AnticipateOvershootInterpolator;
import android.view.animation.BounceInterpolator;
import android.view.animation.DecelerateInterpolator;

import miles.diary.R;
import miles.diary.util.ViewUtils;

/**
 * Created by mbpeele on 1/25/16.
 */
public class TakePictureButton extends View {

    private Paint paint;
    private Drawable drawable;

    private float scale;

    public TakePictureButton(Context context) {
        super(context);
        init();
    }

    public TakePictureButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public TakePictureButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        drawable = ContextCompat.getDrawable(getContext(), R.drawable.ic_text_format_24dp);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        setPivotX(w / 2);
        setPivotY(h / 2);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                animate().scaleY(.7f)
                        .scaleX(.7f)
                        .setDuration(150)
                        .setInterpolator(new BounceInterpolator());
                break;
            case MotionEvent.ACTION_UP:
                animate().scaleX(1f)
                        .scaleY(1f)
                        .setInterpolator(new DecelerateInterpolator())
                        .withEndAction(new Runnable() {
                            @Override
                            public void run() {
                                callOnClick();
                            }
                        });
                break;
        }

        return true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        paint.setColor(ContextCompat.getColor(getContext(), R.color.half_opacity_gray));
        paint.setStrokeWidth(20f);
        canvas.drawCircle(getWidth() / 2f, getHeight() / 2f, getWidth() / 4f, paint);

        paint.setColor(ContextCompat.getColor(getContext(), R.color.primary));
        paint.setStyle(Paint.Style.STROKE);
        canvas.drawCircle(getWidth() / 2f, getHeight() / 2f, (getWidth() / 4f) + 10f, paint);


        if (drawable.getBounds().right == 0) {
            int middle = canvas.getWidth() / 2;
            float rad = (getWidth() / 4f);
            drawable.setBounds((int) (middle - rad), (int) (middle - rad),
                    (int) (middle + rad), (int) (middle + rad));
        }

        if (scale > 0) {
            canvas.save();
            canvas.scale(scale, scale, getPivotX(), getPivotY());
            drawable.draw(canvas);
            canvas.restore();
        }
    }

    public ObjectAnimator showTextDrawable() {
        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(this, SCALER, .7f);
        objectAnimator.setDuration(350);
        objectAnimator.setInterpolator(new AnticipateOvershootInterpolator());
        return objectAnimator;
    }

    public ObjectAnimator hideTextDrawable() {
        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(this, SCALER, 0f);
        objectAnimator.setDuration(350);
        objectAnimator.setInterpolator(new AnticipateOvershootInterpolator());
        return objectAnimator;
    }

    public float getScaler() {
        return scale;
    }

    public void setScaler(float scaler) {
        scale = scaler;
        invalidate();
    }

    private ViewUtils.FloatProperty<TakePictureButton> SCALER
            = new ViewUtils.FloatProperty<TakePictureButton>("scaler") {
        @Override
        public void setValue(TakePictureButton object, float value) {
            object.setScaler(value);
        }

        @Override
        public Float get(TakePictureButton object) {
            return object.getScaler();
        }
    };
}
