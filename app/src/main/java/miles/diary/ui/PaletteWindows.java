package miles.diary.ui;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.support.v7.graphics.Palette;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;

import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.List;

import miles.diary.R;
import miles.diary.util.AnimUtils;
import miles.diary.util.ColorsUtils;
import miles.diary.util.Logg;
import miles.diary.util.ViewUtils;

/**
 * Created by mbpeele on 3/1/16.
 */
public class PaletteWindows implements Palette.PaletteAsyncListener {

    private final SoftReference<Activity> softReference;
    private final Bitmap resource;
    private List<View> overlappingViews;
    private List<Drawable> overlappingDrawables;

    public PaletteWindows(Activity activity, Bitmap bitmap) {
        softReference = new SoftReference<Activity>(activity);
        resource = bitmap;
    }

    public PaletteWindows(Activity activity, Bitmap bitmap, List<View> views, List<Drawable> drawables) {
        softReference = new SoftReference<Activity>(activity);
        resource = bitmap;
        overlappingViews = views;
        overlappingDrawables = drawables;
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
                if (overlappingViews != null) {
                    for (View view: overlappingViews) {
                        if (view instanceof ImageView) {
                            ((ImageView) view).setColorFilter(darkColor);
                        } else {
                            view.setBackgroundColor(darkColor);
                        }
                    }
                }

                if (overlappingDrawables != null) {
                    for (Drawable drawable: overlappingDrawables) {
                        drawable.setColorFilter(darkColor, PorterDuff.Mode.SRC_IN);
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
                ObjectAnimator statusBarColorAnim = ObjectAnimator.ofArgb(window,
                        AnimUtils.STATUS_BAR, window.getStatusBarColor(), statusBarColor);
                statusBarColorAnim.setDuration(1000);
                statusBarColorAnim.setInterpolator(new FastOutSlowInInterpolator());
                statusBarColorAnim.start();
            }
        }
    }
}
