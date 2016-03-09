package miles.diary.ui.transition;

import android.transition.ChangeBounds;
import android.transition.ChangeClipBounds;
import android.transition.ChangeImageTransform;
import android.transition.ChangeTransform;
import android.transition.TransitionSet;

/**
 * Created by mbpeele on 3/8/16.
 */
public class ScalingImageTransition extends TransitionSet {

    public ScalingImageTransition() {
        addTransition(new ChangeBounds());
        addTransition(new ChangeImageTransform());
        addTransition(new ChangeTransform());
        addTransition(new ChangeClipBounds());
    }
}
