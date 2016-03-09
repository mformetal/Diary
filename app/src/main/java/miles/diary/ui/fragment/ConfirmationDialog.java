package miles.diary.ui.fragment;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;

/**
 * Created by mbpeele on 3/3/16.
 */
public class ConfirmationDialog extends BaseDialogFragment {

    public static String MESSAGE = "message";

    public static ConfirmationDialog newInstance(String message) {
        ConfirmationDialog confirmationDialog = new ConfirmationDialog();
        Bundle args = new Bundle();
        args.putString(MESSAGE, message);
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
}
