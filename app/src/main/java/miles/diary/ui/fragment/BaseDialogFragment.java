package miles.diary.ui.fragment;

import android.app.DialogFragment;
import android.content.DialogInterface;

/**
 * Created by mbpeele on 3/8/16.
 */
public abstract class BaseDialogFragment extends DialogFragment {

    private OnDismissListener listener;

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);

        if (listener != null) {
            listener.onDismiss(this);
        }
    }

    public void setDismissListener(OnDismissListener listener) {
        this.listener = listener;
    }

    public interface OnDismissListener {
        void onDismiss(BaseDialogFragment fragment);
    }
}
