package com.vladstoick.DialogFragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import com.actionbarsherlock.app.SherlockDialogFragment;
import com.vladstoick.stiridinromania.R;

/**
 * Created by Vlad on 8/2/13.
 */
public class NoConnectionDialogFragment extends SherlockDialogFragment {
    public static String TAG = "NoConnectionDialogFragment";

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getSherlockActivity()).
                setTitle(getString(R.string.nointernet))
                .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        getDialog().dismiss();
                    }
                });
        return builder.show();
    }
}
