package miles.diary.ui.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import miles.diary.R;
import miles.diary.util.Logg;

/**
 * Created by mbpeele on 2/13/16.
 */
public class AuthView extends View {

    private Paint paint;
    private Rect textBounds;

    private int[] code;

    public AuthView(Context context) {
        super(context);
        init();
    }

    public AuthView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public AuthView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        code = new int[3];
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.WHITE);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int width = getMeasuredWidth();
        int height = Math.round(width * .25f);
        setMeasuredDimension(width, height);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        paint.setTextSize(w * .2f);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return super.onTouchEvent(event);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Rect bounds = canvas.getClipBounds();

        for (int i = 0; i < code.length; i++) {
            // Primary number
            String text = String.valueOf(code[i]);

            textBounds = new Rect();
            paint.getTextBounds(text, 0, text.length(), textBounds);
            float textWidth = paint.measureText(text);
            float textHeight = textBounds.height();

            float x = bounds.width() * (i + 1) / (code.length + 1);

            canvas.drawText(text,
                    x - textWidth / 2f,
                    bounds.exactCenterY() + textHeight / 4f,
                    paint);
        }
    }
}
