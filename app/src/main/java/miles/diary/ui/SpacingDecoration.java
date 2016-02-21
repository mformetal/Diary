package miles.diary.ui;

import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by mbpeele on 1/28/16.
 */
public class SpacingDecoration extends RecyclerView.ItemDecoration {

    private Drawable mDrawable;

    public SpacingDecoration(Drawable drawable) {
        mDrawable = drawable;
    }

    @Override
    public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
        final float diff = parent.getWidth() * .15f;
        int left = Math.round(parent.getPaddingLeft() + diff);
        int right = Math.round(parent.getWidth() - parent.getPaddingRight() - diff);
        final int childCount = parent.getChildCount();

        for (int i = 0; i < childCount; i++) {
            final View child = parent.getChildAt(i);

            RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();

            int top = child.getBottom() + params.bottomMargin;
            int bottom = top + mDrawable.getIntrinsicHeight();

            mDrawable.setBounds(left, top, right, bottom);
            mDrawable.draw(c);
        }
    }
}
