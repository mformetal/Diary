package miles.diary.ui.transition;

import android.animation.ValueAnimator;
import android.support.annotation.ColorInt;
import android.transition.Transition;

import android.animation.Animator;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.transition.TransitionValues;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import miles.diary.util.AnimUtils;

public class ColorTransition extends Transition {

    private static final String PROPNAME_BACKGROUND = "miles:diary:ui:transition:colortransition:background";
    private static final String PROPNAME_COLOR = "miles:diary:ui:transition:colortransition:textColor";

    private int startColor, endColor;

    public ColorTransition(@ColorInt int start, @ColorInt int end) {
        startColor = start;
        endColor = end;
    }

    public ColorTransition(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void captureStartValues(TransitionValues transitionValues) {
        transitionValues.values.put(PROPNAME_BACKGROUND, transitionValues.view.getBackground());
        transitionValues.values.put(PROPNAME_COLOR, startColor);
    }

    @Override
    public void captureEndValues(TransitionValues transitionValues) {
        transitionValues.values.put(PROPNAME_BACKGROUND, transitionValues.view.getBackground());
        transitionValues.values.put(PROPNAME_COLOR, endColor);
    }

    @Override
    public Animator createAnimator(ViewGroup sceneRoot, TransitionValues startValues,
                                   TransitionValues endValues) {
        if (startValues == null || endValues == null) {
            return null;
        }
        final View view = endValues.view;
        Drawable startBackground = (Drawable) startValues.values.get(PROPNAME_BACKGROUND);
        Drawable endBackground = (Drawable) endValues.values.get(PROPNAME_BACKGROUND);

        if (startBackground instanceof ColorDrawable && endBackground instanceof ColorDrawable) {
            ColorDrawable startDrawable = (ColorDrawable) startBackground;
            ColorDrawable endDrawable = (ColorDrawable) endBackground;
            if (startDrawable.getColor() != endDrawable.getColor()) {
                endDrawable.setColor(startDrawable.getColor());
                return ObjectAnimator.ofObject(endBackground, AnimUtils.COLOR,
                        new ArgbEvaluator(), startDrawable.getColor(), endDrawable.getColor());
            }
        }

        if (view instanceof ImageView) {
            final ImageView imageView = (ImageView) view;
            int start = (Integer) startValues.values.get(PROPNAME_COLOR);
            int end = (Integer) endValues.values.get(PROPNAME_COLOR);
            if (start != end) {
                ValueAnimator colorFilter = ValueAnimator.ofArgb(start, end);
                colorFilter.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        imageView.setColorFilter((int) animation.getAnimatedValue());
                    }
                });
                return colorFilter;
            }
        } else {
            int start = (Integer) startValues.values.get(PROPNAME_COLOR);
            int end = (Integer) endValues.values.get(PROPNAME_COLOR);
            if (start != end) {
                ValueAnimator color = ValueAnimator.ofArgb(start, end);
                color.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        view.setBackgroundColor((int) animation.getAnimatedValue());
                    }
                });
                return color;
            }
        }

        return null;
    }
}
