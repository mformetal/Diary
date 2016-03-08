package miles.diary.ui;

import android.graphics.Matrix;
import android.view.animation.Interpolator;

/**
 * Created by mbpeele on 3/8/16.
 */
public interface ZoomerInterface {

    float DEFAULT_MIN_SCALE = 1f;
    float DEFAULT_MAX_SCALE = 4f;
    int DEFAULT_ZOOM_DURATION = 200;

    float getZoomDuration();

    Interpolator getZoomInterpolator();

    void setMinimumScale(float scale);

    void setMaximumScale(float scale);

    float getMinimumScale();

    float getMaximumScale();

    void setZooming(boolean isZooming);

    boolean isZooming();

    float getScale();

    void setScale(float scale, float px, float py);

    void setRotation(float rotation, float px, float py);

    float getMatrixValue(Matrix matrix, int index);

    boolean shouldResetOnFinish();

    void reset();
}
