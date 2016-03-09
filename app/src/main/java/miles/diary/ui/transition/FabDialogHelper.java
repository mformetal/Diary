package miles.diary.ui.transition;

import android.app.Activity;
import android.graphics.Color;
import android.transition.ArcMotion;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;

import miles.diary.util.AnimUtils;

/**
 * Created by mbpeele on 3/6/16.
 */
public class FabDialogHelper {

    public final static String START_COLOR = "startColor";

    public static void makeFabDialogTransition(Activity activity, View target, int radius, int dur) {
        ArcMotion arcMotion = new ArcMotion();
        arcMotion.setMinimumHorizontalAngle(50f);
        arcMotion.setMinimumVerticalAngle(50f);

        int color = activity.getIntent().getIntExtra(START_COLOR, Color.TRANSPARENT);
        Interpolator easeInOut =
                AnimationUtils.loadInterpolator(activity, android.R.interpolator.fast_out_slow_in);

        FabContainerTransition sharedEnter = new FabContainerTransition(color, radius);
        sharedEnter.setPathMotion(arcMotion);
        sharedEnter.setInterpolator(easeInOut);
        sharedEnter.addTarget(target);
        sharedEnter.setDuration(dur);

        ContainerFabTransition sharedReturn = new ContainerFabTransition(color);
        sharedReturn.setPathMotion(arcMotion);
        sharedReturn.setInterpolator(easeInOut);
        sharedReturn.addTarget(target);
        sharedReturn.setDuration(dur);

        activity.getWindow().setSharedElementEnterTransition(sharedEnter);
        activity.getWindow().setSharedElementReturnTransition(sharedReturn);
    }
}
