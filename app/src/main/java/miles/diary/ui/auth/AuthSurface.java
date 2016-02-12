package miles.diary.ui.auth;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Build;
import android.support.design.widget.Snackbar;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import miles.diary.R;
import miles.diary.util.Logg;

/**
 * Created by mbpeele on 2/11/16.
 */
public class AuthSurface extends SurfaceView implements SurfaceHolder.Callback {

    private DrawingCurve mDrawingCurve;
    private DrawingThread mDrawingThread;

    public AuthSurface(Context context) {
        super(context);
        init();
    }

    public AuthSurface(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public AuthSurface(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mDrawingCurve = new DrawingCurve(getContext());

        setLayerType(LAYER_TYPE_NONE, null);

        setWillNotDraw(false);
        setSaveEnabled(true);

        getHolder().addCallback(this);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        holder.setFixedSize(getWidth(), getHeight());

        mDrawingThread = new DrawingThread(holder);
        mDrawingThread.setRunning(true);
        mDrawingThread.start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        mDrawingThread.onDestroy();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return mDrawingCurve.onTouchEvent(event);
    }

    public void undo() {
        if (!mDrawingCurve.undo()) {
//            Snackbar.make(this, R.string.snackbar_no_more_undo, Snackbar.LENGTH_SHORT).show();
        }
    }

    public void redo() {
        if (!mDrawingCurve.redo()) {
//            Snackbar.make(this, R.string.snackbar_no_more_undo, Snackbar.LENGTH_SHORT).show();
        }
    }

    public Bitmap getBitmap() {
        return mDrawingCurve.getBitmap();
    }

    private class DrawingThread extends Thread {

        private boolean mRun = false;

        private final SurfaceHolder mSurfaceHolder;
        private final Object mRunLock = new Object();

        public DrawingThread(SurfaceHolder holder) {
            super("drawingThread");
            mSurfaceHolder = holder;
        }

        public void setRunning(boolean b) {
            synchronized (mRunLock) {
                mRun = b;
            }
        }

        @Override
        public void run() {
            while (mRun) {
                Canvas c = null;
                try {
                    if (mSurfaceHolder.getSurface().isValid()) {
                        c = mSurfaceHolder.lockCanvas();
                    }

                    synchronized (mSurfaceHolder) {
                        synchronized (mRunLock) {
                            if (mRun)  {
                                mDrawingCurve.drawToSurfaceView(c);
                            }
                        }
                    }
                } catch (IllegalArgumentException e) {
                    Logg.log(e);
                } finally {
                    if (c != null) {
                        mSurfaceHolder.unlockCanvasAndPost(c);
                    }
                }
            }
        }

        public void onDestroy() {
            boolean retry = true;
            setRunning(false);
            while (retry) {
                try {
                    join();
                    retry = false;
                } catch (InterruptedException e) {
                    Logg.log(e);
                }
            }
        }
    }
}
