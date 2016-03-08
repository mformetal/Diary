package miles.diary.ui;

import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Interpolator;
import android.view.animation.Transformation;
import android.widget.ImageView;

import java.util.Arrays;
import java.util.List;

import miles.diary.util.Logg;

/**
 * Created by mbpeele on 3/7/16.
 */
public class Zoomer implements MotionEventHandler {

    private final ImageView imageView;
    private final Matrix savedMatrix, matrix;
    private PointF startPoint, midPoint;

    private float lastX, lastY;
    private int activePointer;
    private double oldDistance = 1f;
    private float lastRotation;

    private Mode mode = Mode.NONE;

    public Zoomer(final ImageView view) {
        imageView = view;
        imageView.setScaleType(ImageView.ScaleType.MATRIX);
        imageView.setOnTouchListener(listener);

        savedMatrix = new Matrix();
        matrix = new Matrix();

        startPoint = new PointF();
        midPoint = new PointF();
    }

    @Override
    public void onTouchDown(MotionEvent event) {
        float x = event.getX(), y = event.getY();

        savedMatrix.set(matrix);
        setMode(Mode.DRAG);
        startPoint.set(x, y);

        activePointer = event.getPointerId(0);
        lastX = x;
        lastY = y;
    }

    @Override
    public void onPointerDown(MotionEvent event) {
        if (event.getPointerCount() <= 2) {
            oldDistance = distance(event);
            if (oldDistance > 10f) {
                savedMatrix.set(matrix);
                midPoint(midPoint, event);
                setMode(Mode.ZOOM);
                lastRotation = angle(event);
            }
        }
    }

    @Override
    public void onTouchMove(MotionEvent event) {
        final int pointerIndex = event.findPointerIndex(activePointer);
        float x = event.getX(pointerIndex), y = event.getY(pointerIndex);

        if (getMode() == Mode.DRAG) {
            matrix.set(savedMatrix);
            matrix.postTranslate(x - startPoint.x, y - startPoint.y);
        } else if (getMode() == Mode.ZOOM) {
            if (event.getPointerCount() == 2) {
                double newDist = distance(event);
                if (newDist > 10f) {
                    matrix.set(savedMatrix);
                    double scale = (newDist / oldDistance);
                    matrix.postScale((float) scale, (float) scale, midPoint.x, midPoint.y);
                }

                float mCurrentRotation = angle(event);
                matrix.postRotate(mCurrentRotation - lastRotation, midPoint.x, midPoint.y);
            }
        }

        updateImageView();

        lastX = x;
        lastY = y;
    }

    @Override
    public void onPointerUp(MotionEvent event) {
        int pointerIndex = (event.getAction() & MotionEvent.ACTION_POINTER_INDEX_MASK)
                >> MotionEvent.ACTION_POINTER_INDEX_SHIFT;
        int pointerId = event.getPointerId(pointerIndex);

        if (pointerId == activePointer) {
            final int newPointerIndex = pointerIndex == 0 ? 1 : 0;
            lastX = event.getX(newPointerIndex);
            lastY = event.getY(newPointerIndex);
            activePointer = event.getPointerId(newPointerIndex);
        }

        setMode(Mode.NONE);
    }

    @Override
    public void onTouchUp(MotionEvent event) {
        activePointer = INVALID_POINTER;

        lastX = event.getX();
        lastY = event.getY();
    }

    @Override
    public void onCancel(MotionEvent event) {
        activePointer = INVALID_POINTER;
    }

    @Override
    public void setMode(Mode value) {
        if (!excludeModes().contains(value)) {
            mode = value;
        }
    }

    @Override
    public Mode getMode() {
        return mode;
    }

    @Override
    public List<Mode> excludeModes() {
        return Arrays.asList();
    }

    @Override
    public double distance(MotionEvent event) {
        double dx = event.getX(0) - event.getX(1);
        double dy = event.getY(0) - event.getY(1);
        return Math.sqrt(dx * dx + dy * dy);
    }

    @Override
    public void midPoint(PointF pointF, MotionEvent event) {
        float x = event.getX(0) + event.getX(1);
        float y = event.getY(0) + event.getY(1);
        pointF.set(x / 2, y / 2);
    }

    @Override
    public float angle(MotionEvent event) {
        double dx = (event.getX(0) - event.getX(1));
        double dy = (event.getY(0) - event.getY(1));
        double radians = Math.atan2(dy, dx);
        return (float) Math.toDegrees(radians);
    }

    private void updateImageView() {
        imageView.setImageMatrix(matrix);
        imageView.invalidate();
    }

    private final View.OnTouchListener listener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction() & MotionEvent.ACTION_MASK) {
                case MotionEvent.ACTION_DOWN:
                    onTouchDown(event);
                    break;

                case MotionEvent.ACTION_POINTER_DOWN:
                    onPointerDown(event);
                    break;

                case MotionEvent.ACTION_MOVE:
                    onTouchMove(event);
                    break;

                case MotionEvent.ACTION_UP:
                    onTouchUp(event);
                    break;

                case MotionEvent.ACTION_POINTER_UP:
                    onPointerUp(event);
                    break;

                case MotionEvent.ACTION_CANCEL:
                    onCancel(event);
                    break;
            }

            return true;
        }
    };
}
