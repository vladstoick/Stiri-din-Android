package com.vladstoick.DialogFragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import com.actionbarsherlock.app.SherlockDialogFragment;
import com.vladstoick.OttoBus.BusProvider;
import com.vladstoick.OttoBus.DataModifiedEvent;
import com.vladstoick.stiridinromania.R;

/**
 * Created by vlad on 7/20/13.
 */
public class DeleteDialogFragment extends SherlockDialogFragment {
    public static String TAG = "DELETEDIALOGFRAMGNET";
    Context mContext;
    String type;
    int id;

    /**
     * Constructor for DeleteDialgoFragment
     *
     * @param context Context for Fragment
     * @param id      Id of item to be deleted
     * @param type    type on item
     */
    public DeleteDialogFragment(Context context, int id, String type) {
        this.mContext = context;
        this.type = type;
        this.id = id;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        BusProvider.getInstance().register(this);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mContext);
        alertDialogBuilder.setTitle(getString(R.string.delete_dialog_fragment_title));
        alertDialogBuilder.setMessage(getString(R.string.delete_dialog_fragment_message));
        //null should be your on click listener
        alertDialogBuilder.setPositiveButton(getString(R.string.ok),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        BusProvider.getInstance().post(new DataModifiedEvent(
                                DataModifiedEvent.TAG_DELETEGROUP, id));
                    }
                });
        alertDialogBuilder.setNegativeButton(getString(R.string.cancel),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });


        return alertDialogBuilder.create();
    }
}
