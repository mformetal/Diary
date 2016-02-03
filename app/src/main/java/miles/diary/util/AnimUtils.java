package miles.diary.util;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.view.View;

/**
 * Created by mbpeele on 2/2/16.
 */
public class AnimUtils {

    private static int SHORT_ANIM = -1, MEDIUM_ANIM = -1, LONG_ANIM = -1;

    public static int shortAnim(Context context) {
        if (SHORT_ANIM == -1) {
            SHORT_ANIM = context.getResources().getInteger(android.R.integer.config_shortAnimTime);
        }

        return SHORT_ANIM;
    }

    public static int mediumAnim(Context context) {
        if (MEDIUM_ANIM == -1) {
            MEDIUM_ANIM = context.getResources().getInteger(android.R.integer.config_mediumAnimTime);
        }

        return MEDIUM_ANIM;
    }

    public static int longAnim(Context context) {
        if (LONG_ANIM == -1) {
            LONG_ANIM = context.getResources().getInteger(android.R.integer.config_longAnimTime);
        }

        return LONG_ANIM;
    }

    public static ObjectAnimator gone(final View view, int duration) {
        ObjectAnimator alpha = ObjectAnimator.ofFloat(view, View.ALPHA, 1f, 0f);
        alpha.setDuration(duration);
        alpha.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                view.setVisibility(View.GONE);
            }
        });
        return alpha;
    }

    public static ObjectAnimator visible(final View view, int duration) {
        ObjectAnimator alpha = ObjectAnimator.ofFloat(view, View.ALPHA, 0f, 1f);
        alpha.setDuration(350);
        alpha.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                view.setVisibility(View.VISIBLE);
            }
        });
        return alpha;
    }

    public static ObjectAnimator invisible(final View view, int duration) {
        ObjectAnimator alpha = ObjectAnimator.ofFloat(view, View.ALPHA, 1f, 0f);
        alpha.setDuration(duration);
        alpha.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                view.setVisibility(View.INVISIBLE);
            }
        });
        return alpha;
    }
}
