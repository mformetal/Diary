package miles.diary.ui;

import android.content.Context;
import android.support.design.widget.CoordinatorLayout;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by mbpeele on 3/7/16.
 */
public abstract class BaseBehavior<T extends View> extends CoordinatorLayout.Behavior<T> {

    public BaseBehavior() {}

    public BaseBehavior(Context context, AttributeSet attrs) {}
}
