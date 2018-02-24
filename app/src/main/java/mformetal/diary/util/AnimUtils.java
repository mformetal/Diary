package mformetal.diary.util;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.util.Property;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import java.lang.ref.SoftReference;

/**
 * Created by mbpeele on 2/2/16.
 */
public class AnimUtils {

    private static int SHORT_ANIM = -1, MEDIUM_ANIM = -1, LONG_ANIM = -1;
    public final static String COLOR = "color";
    public final static String BACKGROUND_COLOR = "backgroundColor";
    public final static String STATUS_BAR = "statusBarColor";

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
        scale.setInterpolator(new FastOutSlowInInterpolator());
        return scale;
    }

    public static ValueAnimator colorFilter(final ImageView imageView, int... colors) {
        final SoftReference<ImageView> softReference = new SoftReference<ImageView>(imageView);
        ValueAnimator color = ValueAnimator.ofArgb(colors);
        color.setInterpolator(new DecelerateInterpolator());
        color.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                ImageView imageView1 = softReference.get();
                if (imageView1 != null) {
                    imageView1.setColorFilter((int) animation.getAnimatedValue());
                }
            }
        });
        return color;
    }

    public static ValueAnimator colorFilter(final Drawable drawable, int... colors) {
        final SoftReference<Drawable> softReference = new SoftReference<Drawable>(drawable);
        drawable.mutate();
        ValueAnimator color = ValueAnimator.ofArgb(colors);
        color.setDuration(400);
        color.setInterpolator(new DecelerateInterpolator());
        color.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                Drawable drawable1 = softReference.get();
                if (drawable1 != null) {
                    drawable1.setColorFilter((int) animation.getAnimatedValue(), PorterDuff.Mode.SRC_IN);
                }
            }
        });
        return color;
    }

    public static ValueAnimator background(final View view, int... colors) {
        final SoftReference<View> softReference = new SoftReference<View>(view);
        ValueAnimator color = ValueAnimator.ofArgb(colors);
        color.setDuration(AnimUtils.mediumAnim(view.getContext()));
        color.setInterpolator(new FastOutSlowInInterpolator());
        color.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                View view = softReference.get();
                if (view != null) {
                    view.setBackgroundColor((int) animation.getAnimatedValue());
                }
            }
        });
        return color;
    }

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
