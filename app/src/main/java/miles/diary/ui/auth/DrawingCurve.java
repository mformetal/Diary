package miles.diary.ui.auth;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Handler;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.view.MotionEvent;

import java.util.ArrayList;
import java.util.Stack;

import miles.diary.util.FileUtils;
import miles.diary.util.Logg;
import rx.Observable;
import rx.Subscriber;
import rx.schedulers.Schedulers;

/**
 * Created by mbpeele on 9/25/15.
 */
class DrawingCurve {

    public enum State {
        DRAW,
        ERASE,
    }

    private Bitmap mBitmap, mCachedBitmap;
    private Canvas mCanvas;
    private Stroke mStroke;
    private final Stack<DrawHistory> mRedoneHistory;
    private final Stack<DrawHistory> mAllHistory;
    private Paint mPaint;
    private State mState = State.DRAW;

    private static final int INVALID_POINTER = -1;
    private int mActivePointer = INVALID_POINTER;
    private boolean isSafeToDraw = true;

    public DrawingCurve(Context context) {
        Point size = new Point();
        ((Activity) context).getWindowManager().getDefaultDisplay().getRealSize(size);
        int w = size.x;
        int h = size.y;

        mCachedBitmap = FileUtils.getAuthBitmap(context);
        if (mCachedBitmap == null) {

        }

        mBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        mCanvas = new Canvas(mBitmap);

        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(Color.WHITE);

        mAllHistory = new Stack<>();
        mRedoneHistory = new Stack<>();
        mStroke = new Stroke(mPaint);
    }

    public void drawToSurfaceView(Canvas canvas) {
        if (canvas != null && isSafeToDraw) {
            canvas.drawBitmap(mBitmap, 0, 0, null);
        }
    }

    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                onTouchDown(event);
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
        }

        return true;
    }

    private void onTouchDown(MotionEvent event) {
        float x = event.getX(), y = event.getY();

        switch (mState) {
            case ERASE:
            case DRAW:
                mStroke.addPoint(x, y, mCanvas, mPaint);
                break;
        }

        mActivePointer = event.getPointerId(0);
    }

    private void onTouchMove(MotionEvent event) {
        final int pointerIndex = event.findPointerIndex(mActivePointer);
        float x = event.getX(pointerIndex), y = event.getY(pointerIndex);

        switch (mState) {
            case ERASE:
            case DRAW:
                for (int i = 0; i < event.getHistorySize(); i++) {
                    mStroke.addPoint(event.getHistoricalX(pointerIndex, i),
                            event.getHistoricalY(pointerIndex, i), mCanvas, mPaint);
                }
                mStroke.addPoint(x, y, mCanvas, mPaint);
                break;
        }
    }

    private void onPointerUp(MotionEvent event) {
        int pointerIndex = (event.getAction() & MotionEvent.ACTION_POINTER_INDEX_MASK)
                >> MotionEvent.ACTION_POINTER_INDEX_SHIFT;
        int pointerId = event.getPointerId(pointerIndex);

        if (pointerId == mActivePointer) {
            final int newPointerIndex = pointerIndex == 0 ? 1 : 0;
            mActivePointer = event.getPointerId(newPointerIndex);
        }
    }

    private void onTouchUp(MotionEvent event) {
        mActivePointer = INVALID_POINTER;

        switch (mState) {
            case ERASE:
            case DRAW:
                mAllHistory.push(new DrawHistory(mStroke, mStroke.paint));
                mStroke.clear();
                break;
        }
    }

    public boolean redo() {
        if (!mRedoneHistory.isEmpty()) {
            mAllHistory.push(mRedoneHistory.pop());

            redraw();

            return true;
        }
        return false;
    }

    public boolean undo() {
        if (!mAllHistory.isEmpty()) {
            mRedoneHistory.push(mAllHistory.pop());

            redraw();

            return true;
        }
        return false;
    }

    private void redraw() {
        synchronized (mAllHistory) {
            Observable.from(mAllHistory)
                    .doOnError(Logg::log)
                    .subscribeOn(Schedulers.io())
                    .subscribe(new Subscriber<DrawHistory>() {
                        @Override
                        public void onCompleted() {
                            isSafeToDraw = true;
                        }

                        @Override
                        public void onError(Throwable e) {
                            Logg.log(e);
                        }

                        @Override
                        public void onNext(DrawHistory object) {
                            object.draw(mCanvas);
                        }

                        @Override
                        public void onStart() {
                            super.onStart();
                            isSafeToDraw = false;
                            mBitmap.eraseColor(Color.BLACK);
                        }
                    });
        }
    }

    public Bitmap getBitmap() {
        return mBitmap;
    }

    static class DrawHistory {

        public float[] lines;
        public Paint paint;

        public DrawHistory(ArrayList<CanvasPoint> points, Paint paint) {
            this.paint = new Paint(paint);
            lines = storePoints(points);
        }

        public void draw(Canvas canvas) {
            if (lines != null) {
                canvas.drawLines(lines, paint);
            }
        }

        private float[] storePoints(ArrayList<CanvasPoint> points) {
            int length = points.size();

            int n = length * 2;
            int arraySize = n + (n - 4);

            if (arraySize <= 0) {
                return null;
            }

            float[] pts = new float[arraySize];
            int counter = 1;

            for (int ndx = 0; ndx < length; ndx++) {
                float x = points.get(ndx).x, y = points.get(ndx).y;

                if (ndx == 0) {
                    pts[ndx] = x;
                    pts[ndx + 1] = y;
                    continue;
                }

                if (ndx == length - 1) {
                    pts[pts.length - 2] = points.get(ndx).x;
                    pts[pts.length - 1] = points.get(ndx).y;
                    break;
                }

                int newNdx = ndx + (counter);
                counter += 3;

                pts[newNdx] = x;
                pts[newNdx + 1] = y;
                pts[newNdx + 2] = x;
                pts[newNdx + 3] = y;
            }
            return pts;
        }
    }
}