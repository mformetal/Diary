package miles.diary.ui;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by mbpeele on 1/28/16.
 */
public class SpacingDecoration extends RecyclerView.ItemDecoration {

    private int mSpace;
    private Paint paint;

    public SpacingDecoration(int space, int color) {
        mSpace = space;
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(color);
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
       outRect.set(mSpace, mSpace, mSpace, mSpace);
    }

    @Override
    public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
        drawVertical(c, parent);
        drawHorizontal(c, parent);
    }

    public void drawVertical(Canvas c, RecyclerView parent) {
        final int childCount = parent.getChildCount();

        for (int i = 0; i < childCount; i++) {
            final View child = parent.getChildAt(i);
            final RecyclerView.LayoutParams params =
                    (RecyclerView.LayoutParams) child.getLayoutParams();

            final int left = child.getLeft() - params.leftMargin - mSpace;
            final int right = child.getRight() + params.rightMargin + mSpace;
            final int top = child.getBottom() + params.bottomMargin + mSpace;
            final int bottom = top + (int) paint.getStrokeWidth();
            c.drawLine(left, top, right, bottom, paint);
        }
    }

    public void drawHorizontal(Canvas c, RecyclerView parent) {
        final int childCount = parent.getChildCount();

        for (int i = 0; i < childCount; i++) {
            final View child = parent.getChildAt(i);
            final RecyclerView.LayoutParams params =
                    (RecyclerView.LayoutParams) child.getLayoutParams();

            final int left = child.getRight() + params.rightMargin + mSpace;
            final int right = left + (int) paint.getStrokeWidth();
            final int top = child.getTop() - params.topMargin - mSpace;
            final int bottom = child.getBottom() + params.bottomMargin + mSpace;
            c.drawLine(left, top, right, bottom, paint);
        }
    }
}
