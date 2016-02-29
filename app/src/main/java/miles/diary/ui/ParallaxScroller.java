package miles.diary.ui;

import android.content.Context;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.AttributeSet;
import android.view.View;

import miles.diary.data.model.weather.Main;
import miles.diary.util.Logg;

/**
 * Created by mbpeele on 2/28/16.
 */
public class ParallaxScroller<T extends View> extends CoordinatorLayout.Behavior<T> {

    public ParallaxScroller(Context context, AttributeSet attrs) {}

    @Override
    public boolean layoutDependsOn(CoordinatorLayout parent, T child, View dependency) {
        return dependency instanceof AppBarLayout;
    }

    @Override
    public boolean onDependentViewChanged(CoordinatorLayout parent, final T child, View dependency) {
        if (dependency instanceof AppBarLayout) {
            ((AppBarLayout) dependency).addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
                @Override
                public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                    float ratio = 1 - Math.abs((float) verticalOffset / (float) appBarLayout.getHeight());
                    float translation = verticalOffset * .75f;
                    child.setTranslationY(translation);
                    child.setAlpha(ratio);
                    child.setScaleX(ratio);
                    child.setScaleY(ratio);
                }
            });
        }
        return super.onDependentViewChanged(parent, child, dependency);
    }
}
