package miles.diary.ui;

import android.support.v7.widget.RecyclerView;

/**
 * Created by mbpeele on 1/25/16.
 */
public class StackLayoutManager extends RecyclerView.LayoutManager {

    @Override
    public RecyclerView.LayoutParams generateDefaultLayoutParams() {
        return new RecyclerView.LayoutParams(
                RecyclerView.LayoutParams.WRAP_CONTENT,
                RecyclerView.LayoutParams.WRAP_CONTENT);
    }
}
