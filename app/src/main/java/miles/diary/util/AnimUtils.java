package miles.diary.util;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.util.Property;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewPropertyAnimator;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.request.animation.ViewPropertyAnimation;

import miles.diary.R;
import miles.diary.ui.PreDrawer;

/**
 * Created by mbpeele on 2/2/16.
 */
public class AnimUtils {

    private static int SHORT_ANIM = -1, MEDIUM_ANIM = -1, LONG_ANIM = -1;
    public final static String COLOR = "color";
    public final static String TEXT_SCALE_X = "textScaleX";

    public static int shortAnim(Context context) {
        if (SHORT_ANIM < 0) {
            SHORT_ANIM =
                    context.getResources().getInteger(android.R.integer.config_shortAnimTime);
        }

        return SHORT_ANIM;
    }

    public static int mediumAnim(Context context) {
        if (MEDIUM_ANIM < 0) {
            MEDIUM_ANIM =
                    context.getResources().getInteger(android.R.integer.config_mediumAnimTime);
        }

        return MEDIUM_ANIM;
    }

    public static int longAnim(Context context) {
        if (LONG_ANIM < 0) {
            LONG_ANIM =
                    context.getResources().getInteger(android.R.integer.config_longAnimTime);
        }

        return LONG_ANIM;
    }

    public static ObjectAnimator gone(final View view) {
        ObjectAnimator alpha = ObjectAnimator.ofFloat(view, View.ALPHA, 1f, 0f);
        alpha.setDuration(shortAnim(view.getContext()));
        alpha.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                view.setVisibility(View.GONE);
            }
        });
        return alpha;
    }

    public static ObjectAnimator visible(final View view) {
        ObjectAnimator alpha = ObjectAnimator.ofFloat(view, View.ALPHA, 0f, 1f);
        alpha.setDuration(shortAnim(view.getContext()));
        alpha.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                view.setVisibility(View.VISIBLE);
            }
        });
        return alpha;
    }

    public static ObjectAnimator invisible(final View view) {
        ObjectAnimator alpha = ObjectAnimator.ofFloat(view, View.ALPHA, 1f, 0f);
        alpha.setDuration(shortAnim(view.getContext()));
        alpha.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                view.setVisibility(View.INVISIBLE);
            }
        });
        return alpha;
    }

    public static ObjectAnimator alpha(final View view, float... alphas) {
        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(view, View.ALPHA, alphas);
        objectAnimator.setDuration(mediumAnim(view.getContext()));
        objectAnimator.setInterpolator(new DecelerateInterpolator());
        return objectAnimator;
    }

    public static ObjectAnimator textScale(final TextView textView, final String text, float... scales) {
        FloatProperty<TextView> floatProperty = new FloatProperty<TextView>("") {
            @Override
            public void setValue(TextView object, float value) {
                object.setTextScaleX(value);
            }

            @Override
            public Float get(TextView object) {
                Logg.log("GET: ", object.getTextScaleX());
                return object.getTextScaleX();
            }
        };

        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(textView, floatProperty, scales);
        objectAnimator.setDuration(mediumAnim(textView.getContext()));
        objectAnimator.setInterpolator(new DecelerateInterpolator());
        objectAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                if (text != null) {
                    textView.setText(text);
                }
            }
        });
        return objectAnimator;
    }

    public static ObjectAnimator pop(final View view, float... scales) {
        ObjectAnimator scale = ObjectAnimator.ofPropertyValuesHolder(view,
                PropertyValuesHolder.ofFloat(View.SCALE_X, scales),
                PropertyValuesHolder.ofFloat(View.SCALE_Y, scales));
        scale.setDuration(mediumAnim(view.getContext()));
        scale.setInterpolator(new FastOutSlowInInterpolator());
        return scale;
    }

    public static ValueAnimator colorfilter(final ImageView imageView, int... colors) {
        ValueAnimator color = ValueAnimator.ofArgb(colors);
        color.setDuration(AnimUtils.mediumAnim(imageView.getContext()));
        color.setInterpolator(new DecelerateInterpolator());
        color.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                imageView.setColorFilter((int) animation.getAnimatedValue());
            }
        });
        return color;
    }

    public final static ViewPropertyAnimation.Animator REVEAL = new ViewPropertyAnimation.Animator() {
        @Override
        public void animate(View view) {
            if (view.getWidth() == 0 || view.getHeight() == 0) {
                new PreDrawer<View>(view) {
                    @Override
                    public void notifyPreDraw(View view1) {
                        Animator reveal = ViewAnimationUtils.createCircularReveal(view1,
                                view1.getWidth() / 2, view1.getHeight() / 2, 0,
                                Math.max(view1.getWidth(), view1.getHeight()));
                        reveal.setDuration(longAnim(view1.getContext()));
                        reveal.setInterpolator(new DecelerateInterpolator());
                        reveal.start();
                    }
                };
            } else {
                Animator reveal = ViewAnimationUtils.createCircularReveal(view,
                        view.getWidth() / 2, view.getHeight() / 2, 0,
                        Math.max(view.getWidth(), view.getHeight()));
                reveal.setDuration(longAnim(view.getContext()));
                reveal.setInterpolator(new DecelerateInterpolator());
                reveal.start();
            }
        }
    };

    public static abstract class FloatProperty<T> extends Property<T, Float> {
        public FloatProperty(String name) {
            super(Float.class, name);
        }

        public abstract void setValue(T object, float value);

        @Override
        final public void set(T object, Float value) {
            setValue(object, value);
        }
    }

    public static abstract class IntProperty<T> extends Property<T, Integer> {

        public IntProperty(String name) {
            super(Integer.class, name);
        }

        public abstract void setValue(T object, int value);

        @Override
        final public void set(T object, Integer value) {
            setValue(object, value);
        }
    }
}
