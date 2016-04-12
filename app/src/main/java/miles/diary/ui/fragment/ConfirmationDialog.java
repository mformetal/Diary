package miles.diary.ui.fragment;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import miles.diary.R;
import miles.diary.util.Logg;

/**
 * Created by mbpeele on 3/3/16.
 */
public class ConfirmationDialog extends DismissingDialogFragment {

    public static String MESSAGE = "message";
    public static String LAYOUT_ID = "layoutId";

    public static ConfirmationDialog newInstance(String message) {
        return newInstance(message, 0);
    }

    public static ConfirmationDialog newInstance(String message, int layoutId) {
        ConfirmationDialog confirmationDialog = new ConfirmationDialog();
        Bundle args = new Bundle();
        args.putString(MESSAGE, message);
        args.putInt(LAYOUT_ID, layoutId);
        confirmationDialog.setArguments(args);
        return confirmationDialog;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new AlertDialog.Builder(getActivity())
                .setMessage(getArguments().getString(MESSAGE))
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        int id = getArguments().getInt(LAYOUT_ID);
        if (id != 0) {
            return inflater.inflate(id, container, false);
        } else {
            return super.onCreateView(inflater, container, savedInstanceState);
        }
    }
}
