package miles.diary.ui;

import android.view.View;
import android.view.ViewTreeObserver;

/**
 * Created by mbpeele on 2/2/16.
 */
public abstract class PreDrawer {

    public PreDrawer(final View view) {
        final ViewTreeObserver viewTreeObserver = view.getViewTreeObserver();
        viewTreeObserver.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                if (viewTreeObserver.isAlive()) {
                    viewTreeObserver.removeOnPreDrawListener(this);
                } else {
                    view.getViewTreeObserver().removeOnPreDrawListener(this);
                }

                notifyPreDraw(view);
                return true;
            }
        });
    }

    public abstract void notifyPreDraw(View view);
}
