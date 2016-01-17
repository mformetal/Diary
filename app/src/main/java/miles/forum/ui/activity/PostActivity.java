package miles.forum.ui.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;

import butterknife.Bind;
import miles.forum.R;
import miles.forum.ui.drawable.FabDialogDrawable;
import miles.forum.ui.widget.TypefaceButton;
import miles.forum.ui.widget.TypefaceEditText;

/**
 * Created by mbpeele on 1/16/16.
 */
public class PostActivity extends BaseActivity {

    public static final String LEFT = "forum:fabLeft";
    public static final String TOP = "forum:fabTop";
    public static final String WIDTH = "forum:fabWidth";
    public static final String HEIGHT = "forum:fabHeight";
    public static final String START_COLOR = "forum:fabStartColor";
    public static final String RESULT_TEXT = "forum:result";

    @Bind(R.id.activity_post_input) TypefaceEditText typefaceEditText;
    @Bind(R.id.activity_post_confirm) TypefaceButton typefaceButton;
    @Bind(R.id.activity_post_root) ViewGroup root;

    private int fabLeft, fabTop, fabWidth, fabHeight, fabStartColor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        Bundle bundle = getIntent().getExtras();
        fabLeft = bundle.getInt(LEFT);
        fabTop = bundle.getInt(TOP);
        fabWidth = bundle.getInt(WIDTH);
        fabHeight = bundle.getInt(HEIGHT);
        fabStartColor = bundle.getInt(START_COLOR);

        if (savedInstanceState == null) {
            final ViewTreeObserver observer = root.getViewTreeObserver();
            observer.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                @Override
                public boolean onPreDraw() {
                    if (observer.isAlive()) {
                        observer.removeOnPreDrawListener(this);
                    } else {
                        root.getViewTreeObserver().removeOnPreDrawListener(this);
                    }

                    int[] location = new int[2];
                    root.getLocationOnScreen(location);

                    root.setBackgroundColor(Color.MAGENTA);

                    float dx = fabLeft - location[0];
                    float dy = fabTop - location[1];

                    float sx = (float) fabWidth / (float) root.getWidth();
                    float sy = (float) fabHeight / (float) root.getHeight();

                    runEnterAnimation(dx, dy, sx, sy);
                    return true;
                }
            });
        }

        typefaceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = typefaceEditText.getTextAsString();
                if (text.isEmpty()) {
                    typefaceEditText.setError("No thoughts?");
                } else {
                    Intent intent = new Intent();
                    intent.putExtra(RESULT_TEXT, text);
                    setResult(RESULT_OK, intent);
                    onBackPressed();
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        runExitAnimation(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                finish();
            }
        });
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0, 0);
    }

    private void runEnterAnimation(float dx, float dy, float sx, float sy) {
        root.setPivotX(0);
        root.setPivotY(0);
        root.setScaleX(sx);
        root.setScaleY(sy);
        root.setTranslationX(dx);
        root.setTranslationY(dy);

        final FabDialogDrawable drawable = new FabDialogDrawable(fabStartColor, fabWidth);
        root.setBackground(drawable);

        Animator position = ObjectAnimator.ofPropertyValuesHolder(root,
                PropertyValuesHolder.ofFloat(View.SCALE_X, root.getScaleX(), 1),
                PropertyValuesHolder.ofFloat(View.SCALE_Y, root.getScaleY(), 1),
                PropertyValuesHolder.ofFloat(View.TRANSLATION_X, 0),
                PropertyValuesHolder.ofFloat(View.TRANSLATION_Y, 0));
        position.setStartDelay(100);
        position.setDuration(400);
        position.setInterpolator(new DecelerateInterpolator());

        final ValueAnimator background = ValueAnimator.ofObject(new ArgbEvaluator(), fabStartColor, Color.WHITE);
        background.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                drawable.setColor((Integer) animation.getAnimatedValue());
            }
        });
        background.setDuration(400);

        Animator radius = ObjectAnimator.ofFloat(drawable, FabDialogDrawable.RADIUS, fabWidth,
                root.getWidth());
        radius.setDuration(400);

        ValueAnimator alpha = ValueAnimator.ofObject(new ArgbEvaluator(),
                Color.TRANSPARENT, ContextCompat.getColor(this, R.color.scrim));
        alpha.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                ((View) root.getParent()).setBackgroundColor((Integer) animation.getAnimatedValue());
            }
        });
        alpha.setDuration(400);

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(position, background, radius, alpha);
        animatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                float offset = root.getHeight() / 3;
                for (int i = 0; i < root.getChildCount(); i++) {
                    View v = root.getChildAt(i);
                    v.setTranslationY(offset);
                    v.setAlpha(0f);
                    v.animate()
                            .alpha(1f)
                            .translationY(0f)
                            .setDuration(150)
                            .setStartDelay(150)
                            .setInterpolator(new FastOutSlowInInterpolator());
                    offset *= 1.8f;
                }
            }
        });
        animatorSet.start();
    }

    private void runExitAnimation(final AnimatorListenerAdapter adapter) {
        int[] location = new int[2];
        root.getLocationOnScreen(location);

        root.setPivotX(0);
        root.setPivotY(0);

        final FabDialogDrawable drawable = (FabDialogDrawable) root.getBackground();

        Animator position = ObjectAnimator.ofPropertyValuesHolder(root,
                PropertyValuesHolder.ofFloat(View.SCALE_X, (float) fabWidth / (float) root.getWidth()),
                PropertyValuesHolder.ofFloat(View.SCALE_Y, (float) fabHeight / (float) root.getHeight()),
                PropertyValuesHolder.ofFloat(View.TRANSLATION_X, fabLeft - location[0]),
                PropertyValuesHolder.ofFloat(View.TRANSLATION_Y, fabTop - location[1]));
        position.setDuration(400);
        position.setInterpolator(new AccelerateInterpolator());

        final ValueAnimator background = ValueAnimator.ofObject(new ArgbEvaluator(),
                Color.WHITE, fabStartColor);
        background.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                drawable.setColor((Integer) animation.getAnimatedValue());
            }
        });
        background.setDuration(400);

        Animator radius = ObjectAnimator.ofFloat(drawable, FabDialogDrawable.RADIUS,
                root.getWidth(), fabWidth);
        radius.setDuration(400);

        ValueAnimator alpha = ValueAnimator.ofObject(new ArgbEvaluator(),
                ContextCompat.getColor(this, R.color.scrim), Color.TRANSPARENT);
        alpha.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                ((View) root.getParent()).setBackgroundColor((Integer) animation.getAnimatedValue());
            }
        });
        alpha.setDuration(400);

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(position, background, radius, alpha);
        animatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                for (int i = 0; i < root.getChildCount(); i++) {
                    View v = root.getChildAt(i);
                    v.animate()
                            .alpha(0f)
                            .translationY(v.getHeight() / 3)
                            .setDuration(50)
                            .setInterpolator(new FastOutSlowInInterpolator())
                            .start();
                }
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                adapter.onAnimationEnd(animation);
            }
        });
        animatorSet.start();
    }
}
