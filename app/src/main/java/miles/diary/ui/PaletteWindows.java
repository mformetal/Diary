package miles.diary.ui;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.support.v7.graphics.Palette;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;

import java.lang.ref.SoftReference;

import miles.diary.R;
import miles.diary.util.ColorsUtils;
import miles.diary.util.ViewUtils;

/**
 * Created by mbpeele on 3/1/16.
 */
public class PaletteWindows implements Palette.PaletteAsyncListener {

    private final SoftReference<Activity> softReference;
    private final Bitmap resource;
    private View[] overlappingViews;

    public PaletteWindows(Activity activity, Bitmap bitmap, View... overlap) {
        softReference = new SoftReference<Activity>(activity);
        resource = bitmap;
        overlappingViews = overlap;
    }

    @Override
    public void onGenerated(Palette palette) {
        Activity activity = softReference.get();
        if (activity != null) {
            final Window window = activity.getWindow();
            boolean isDark;
            @ColorsUtils.Lightness int lightness = ColorsUtils.isDark(palette);
            if (lightness == ColorsUtils.LIGHTNESS_UNKNOWN) {
                isDark = ColorsUtils.isDark(resource,
                        resource.getWidth() / 2, 0);
            } else {
                isDark = lightness == ColorsUtils.IS_DARK;
            }

            if (!isDark) { // make back icon dark on light images
                if (overlappingViews != null) {
                    int dark = ContextCompat.getColor(
                            activity, R.color.dark_icons);
                    for (View view: overlappingViews) {
                        if (view instanceof ImageView) {
                            ((ImageView) view).setColorFilter(dark);
                        } else if (view.getBackground() instanceof ColorDrawable) {
                            ((ColorDrawable) view.getBackground()).setColor(dark);
                        } else {
                            view.setBackgroundColor(dark);
                        }
                    }
                }
            }

            int statusBarColor = window.getStatusBarColor();
            Palette.Swatch topColor = ColorsUtils.getMostPopulousSwatch(palette);
            if (topColor != null &&
                    (isDark || Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)) {
                statusBarColor = ColorsUtils.scrimify(topColor.getRgb(),
                        isDark, .075f);

                if (!isDark && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    ViewUtils.setLightStatusBar(window.getDecorView());
                }
            }

            if (statusBarColor != window.getStatusBarColor()) {
                ValueAnimator statusBarColorAnim = ValueAnimator.ofArgb(
                        window.getStatusBarColor(), statusBarColor);
                statusBarColorAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        window.setStatusBarColor((int) animation.getAnimatedValue());
                    }
                });
                statusBarColorAnim.setDuration(1000);
                statusBarColorAnim.setInterpolator(new FastOutSlowInInterpolator());
                statusBarColorAnim.start();
            }
        }
    }
}
