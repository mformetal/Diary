package miles.diary.ui;

import android.view.View;
import android.view.ViewTreeObserver;

import java.lang.ref.SoftReference;

/**
 * Created by mbpeele on 2/2/16.
 */
public class PreDrawer {

    public static <T extends View> void addPreDrawer(final T view, final OnPreDrawListener<T> listener) {
        final SoftReference<T> softReference = new SoftReference<>(view);

        final ViewTreeObserver viewTreeObserver = view.getViewTreeObserver();
        viewTreeObserver.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @SuppressWarnings("SimplifiableIfStatement")
            @Override
            public boolean onPreDraw() {
                if (viewTreeObserver.isAlive()) {
                    viewTreeObserver.removeOnPreDrawListener(this);
                } else {
                    view.getViewTreeObserver().removeOnPreDrawListener(this);
                }

                T reference = softReference.get();
                if (reference != null) {
                    return listener.onPreDraw(reference);
                }

                return false;
            }
        });
    }

    public interface OnPreDrawListener<T> {

        boolean onPreDraw(final T view);
    }
}
