package miles.diary.ui;

import android.graphics.PointF;
import android.view.MotionEvent;

import java.util.List;

/**
 * Created by mbpeele on 3/7/16.
 */
public interface MotionEventHandler {

    enum Mode {
        NONE,
        DRAG,
        ZOOM
    }

    int INVALID_POINTER = -1;

    void onTouchDown(MotionEvent event);

    void onPointerDown(MotionEvent event);

    void onTouchMove(MotionEvent event);

    void onPointerUp(MotionEvent event);

    void onTouchUp(MotionEvent event);

    void onCancel(MotionEvent event);

    void setMode(Mode value);

    Mode getMode();

    List<Mode> excludeModes();

    double distance(MotionEvent event);

    void midPoint(PointF pointF, MotionEvent event);

    float angle(MotionEvent event);
}
