package miles.diary.ui;

import android.app.Activity;
import android.transition.Transition;
import android.view.View;
import android.widget.ImageView;

import miles.diary.ui.transition.SimpleTransitionListener;
import miles.diary.util.Logg;
import uk.co.senab.photoview.PhotoViewAttacher;

/**
 * Created by mbpeele on 5/7/16.
 */
public class ZoomController implements View.OnAttachStateChangeListener {

    private PhotoViewAttacher photoViewAttacher;

    public ZoomController(ImageView imageView) {
        if (imageView.isAttachedToWindow()) {
            photoViewAttacher = new PhotoViewAttacher(imageView);
        }

        imageView.addOnAttachStateChangeListener(this);
    }

    @Override
    public void onViewAttachedToWindow(View v) {
        if (photoViewAttacher == null) {
            photoViewAttacher = new PhotoViewAttacher((ImageView) v);
        }
    }

    @Override
    public void onViewDetachedFromWindow(View v) {
        photoViewAttacher = null;
    }
}
