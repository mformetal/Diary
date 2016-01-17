package miles.forum.ui.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.view.View;
import android.view.ViewTreeObserver;

import miles.forum.R;

/**
 * Created by mbpeele on 1/17/16.
 */
public abstract class TransitionActivity extends BaseActivity {

    private View root;

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        root = findViewById(android.R.id.content);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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

                    runEnterTransition();

                    return true;
                }
            });
        }
    }

    @Override
    public void onBackPressed() {
        runExitTransition(new AnimatorListenerAdapter() {
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

    public void runEnterTransition() {

    }

    public void runExitTransition(AnimatorListenerAdapter adapter) {

    }
}
