package mformetal.diary.ui.transition;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.transition.ChangeBounds;
import android.transition.TransitionValues;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Interpolator;

import java.util.ArrayList;

import mformetal.diary.ui.widget.RoundedImageView;
import mformetal.diary.util.AnimUtils;

/**
 * Created by mbpeele on 2/29/16.
 */
public class RoundedImageViewTransition extends ChangeBounds {

    private static final String PROPNAME_RADIUS = "miles:diary:ui:transition:cornerTransition:radius";
    private static final String[] PROPERTIES = new String[] {
            PROPNAME_RADIUS
    };

    private float start, end;

    public RoundedImageViewTransition(float startCorner, float endCorner) {
        super();
        start = startCorner;
        end = endCorner;
    }

    public RoundedImageViewTransition(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public Animator createAnimator(ViewGroup sceneRoot, TransitionValues startValues, TransitionValues endValues) {
        Animator changeBounds = super.createAnimator(sceneRoot, startValues, endValues);

        if (startValues == null || endValues == null || changeBounds == null) {
            return null;
        }

        AnimatorSet set = new AnimatorSet();

        ArrayList<Animator> arrayList = new ArrayList<>();
        arrayList.add(changeBounds);

        int duration = AnimUtils.mediumAnim(sceneRoot.getContext());
        Interpolator interpolator = new FastOutSlowInInterpolator();
        for (View view: getTargets()) {
            if (view instanceof RoundedImageView) {
                RoundedImageView roundedImageView = (RoundedImageView) view;

                ObjectAnimator corner = ObjectAnimator.ofFloat(roundedImageView,
                        RoundedImageView.CORNERS, start, end);
                corner.setDuration(duration);
                corner.setInterpolator(interpolator);

                arrayList.add(corner);
            }
        }

        set.playTogether(arrayList);
        return set;
    }
}
