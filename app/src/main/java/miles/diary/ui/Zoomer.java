package miles.diary.ui;

import android.graphics.Matrix;
import android.graphics.PointF;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.ImageView;

import java.lang.ref.WeakReference;
import java.util.Arrays;
import java.util.List;

import miles.diary.util.Logg;

/**
 * Created by mbpeele on 3/7/16.
 */
public class Zoomer implements MotionEventHandler, ZoomerInterface {

    private final static Interpolator ZOOM_INTERPOLATOR = new AccelerateInterpolator();
    private WeakReference<ImageView> imageViewWeakReference;
    private final Matrix savedMatrix, matrix;
    private PointF startPoint, midPoint, centerPoint;
    private float[] values = new float[9];
    private Mode mode = Mode.NONE;

    private float lastX, lastY;
    private boolean isZooming;
    private int activePointer;
    private double oldDistance = 1f;
    private float lastRotation;
    private float minScale = DEFAULT_MIN_SCALE,
            maxScale = DEFAULT_MAX_SCALE;

    public Zoomer(final ImageView view) {
        imageViewWeakReference = new WeakReference<ImageView>(view);

        view.setScaleType(ImageView.ScaleType.MATRIX);
        view.setOnTouchListener(listener);
        view.addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() {
            @Override
            public void onViewAttachedToWindow(View v) {

            }

            @Override
            public void onViewDetachedFromWindow(View v) {
                if (imageViewWeakReference == null) {
                    return;
                }

                final ImageView imageView = getImageView();

                if (imageView != null) {
                    imageView.setOnTouchListener(null);
                }

                imageViewWeakReference = null;
            }
        });

        savedMatrix = new Matrix();
        matrix = new Matrix();

        startPoint = new PointF();
        midPoint = new PointF();

        if (view.getWidth() == 0 || view.getHeight() == 0) {
            PreDrawer.addPreDrawer(view, new PreDrawer.OnPreDrawListener<ImageView>() {
                @Override
                public boolean onPreDraw(ImageView view) {
                    centerPoint = new PointF(view.getWidth() / 2f, view.getHeight() / 2f);
                    return true;
                }
            });
        } else {
            centerPoint = new PointF(view.getWidth() / 2f, view.getHeight() / 2f);
        }
    }

    @Override
    public void onTouchDown(MotionEvent event) {
        if (isZooming()) {
            setZooming(false);
        }

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
            }
            lastRotation = angle(event);
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
                    setScale((float) scale, centerPoint.x, centerPoint.y);
                }

                if (!excludeModes().contains(Mode.ROTATE)) {
                    float mCurrentRotation = angle(event);
                    setRotation(mCurrentRotation - lastRotation, midPoint.x, midPoint.y);
                }
            }
        }

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

        if (shouldResetOnFinish()) {
            reset();
        }
    }

    @Override
    public void onCancel(MotionEvent event) {
        activePointer = INVALID_POINTER;

        if (shouldResetOnFinish()) {
            reset();
        }
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
        return Arrays.asList(Mode.DRAG, Mode.ROTATE);
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

    @Override
    public float getZoomDuration() {
        return DEFAULT_ZOOM_DURATION;
    }

    @Override
    public Interpolator getZoomInterpolator() {
        return ZOOM_INTERPOLATOR;
    }

    @Override
    public void setMinimumScale(float scale) {
        minScale = scale;
    }

    @Override
    public void setMaximumScale(float scale) {
        maxScale = scale;
    }

    @Override
    public float getMinimumScale() {
        return minScale;
    }

    @Override
    public float getMaximumScale() {
        return maxScale;
    }

    @Override
    public void setZooming(boolean bool) {
        isZooming = bool;

        ImageView imageView = getImageView();
        if (imageView != null) {
            imageView.removeCallbacks(null);
        }
    }

    @Override
    public boolean isZooming() {
        return isZooming;
    }

    @Override
    public float getScale() {
        return (float) Math.sqrt((float) Math.pow(getMatrixValue(matrix, Matrix.MSCALE_X), 2) +
                (float) Math.pow(getMatrixValue(matrix, Matrix.MSKEW_Y), 2));
    }

    @Override
    public void setScale(float scale, float px, float py) {
        matrix.setScale(scale, scale, px, py);
        updateImageView();
    }

    @Override
    public void setRotation(float rotation, float px, float py) {
        matrix.postRotate(rotation, px, py);
        updateImageView();
    }

    @Override
    public float getMatrixValue(Matrix matrix, int index) {
        matrix.getValues(values);
        return values[index];
    }

    @Override
    public boolean shouldResetOnFinish() {
        return true;
    }

    @Override
    public void reset() {
        if (getScale() < getMinimumScale()) {
            ImageView imageView = getImageView();
            if (imageView != null) {
                setZooming(true);
                imageView.post(new ResetRunnable(getScale(), getMinimumScale()));
            }
        }
    }

    private void updateImageView() {
        ImageView imageView = getImageView();;
        if (imageView != null) {
            imageView.setImageMatrix(matrix);
            imageView.invalidate();
        }
    }

    private ImageView getImageView() {
        return imageViewWeakReference.get();
    }

    private class ResetRunnable implements Runnable {

        private final long mStartTime;
        private final float mZoomStart, mZoomEnd;

        public ResetRunnable(final float currentZoom, final float targetZoom) {
            mStartTime = System.currentTimeMillis();
            mZoomStart = currentZoom;
            mZoomEnd = targetZoom;
        }

        @Override
        public void run() {
            if (isZooming()) {
                ImageView imageView = getImageView();
                if (imageView == null) {
                    return;
                }

                float t = interpolate();
                float scale = mZoomStart + t * (mZoomEnd - mZoomStart);

                setScale(scale, centerPoint.x, centerPoint.y);

                if (t < 1f) {
                    imageView.postOnAnimation(this);
                } else {
                    setZooming(false);
                }
            }
        }

        private float interpolate() {
            float t = 1f * (System.currentTimeMillis() - mStartTime) / getZoomDuration();
            t = Math.min(1f, t);
            t = ZOOM_INTERPOLATOR.getInterpolation(t);
            return t;
        }
    }

    private final View.OnTouchListener listener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            ImageView imageView = getImageView();
            if (imageView != null) {
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
            }

            return true;
        }
    };
}
