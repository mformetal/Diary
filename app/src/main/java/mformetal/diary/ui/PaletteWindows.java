package mformetal.diary.ui;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.support.v7.graphics.Palette;
import android.view.Window;

import java.lang.ref.SoftReference;

import mformetal.diary.R;
import mformetal.diary.util.AnimUtils;
import mformetal.diary.util.ColorsUtils;
import mformetal.diary.util.ViewUtils;

/**
 * Created by mbpeele on 3/1/16.
 */
public class PaletteWindows implements Palette.PaletteAsyncListener {

    private final SoftReference<Activity> softReference;
    private final Bitmap resource;

    public PaletteWindows(Activity activity, Bitmap bitmap) {
        softReference = new SoftReference<Activity>(activity);
        resource = bitmap;
    }

    @Override
    public void onGenerated(Palette palette) {
        Activity activity = softReference.get();
        if (activity != null) {
            final Window window = activity.getWindow();
            boolean isDark;
            @ColorsUtils.Lightness int lightness = ColorsUtils.isDark(palette);
            if (lightness == ColorsUtils.LIGHTNESS_UNKNOWN) {
                isDark = ColorsUtils.isDark(resource, resource.getWidth() / 2, 0);
            } else {
                isDark = lightness == ColorsUtils.IS_DARK;
            }

            if (!isDark) {
                int darkColor = ContextCompat.getColor(activity, R.color.dark_icons);
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
                ObjectAnimator statusBarColorAnim = ObjectAnimator.ofArgb(window,
                        AnimUtils.STATUS_BAR, window.getStatusBarColor(), statusBarColor);
                statusBarColorAnim.setDuration(1000);
                statusBarColorAnim.setInterpolator(new FastOutSlowInInterpolator());
                statusBarColorAnim.start();
            }
        }
    }
}
