package miles.diary.ui;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by mbpeele on 3/7/16.
 */
public class SpacingDecoration extends RecyclerView.ItemDecoration {

    final int spacing;

    public SpacingDecoration(int value) {
        spacing = value;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        outRect.left = spacing;
        outRect.right = spacing;
        outRect.bottom = spacing;

        if (parent.getChildAdapterPosition(view) == 0 || parent.getChildAdapterPosition(view) == 1) {
            outRect.top = spacing;
        }
    }
}
