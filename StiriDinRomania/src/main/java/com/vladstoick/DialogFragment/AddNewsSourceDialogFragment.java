package com.vladstoick.DialogFragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import com.actionbarsherlock.app.SherlockDialogFragment;
import com.vladstoick.stiridinromania.R;

/**
 * Created by Vlad on 7/24/13.
 */
public class AddNewsSourceDialogFragment extends SherlockDialogFragment{
    private View mView;
    public static String TAG = "ADDNEWSSOURCE";
    public AddNewsSourceDialogFragment() {
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Context context = getSherlockActivity();
        LayoutInflater inflater = LayoutInflater.from(context);
        mView = inflater.inflate(R.layout.dialog_addnewsource,null);
        return new AlertDialog.Builder(getSherlockActivity())
                .setView(mView)
                .setTitle(context.getString(R.string.add_dialog_fragment_title_source))
                .setPositiveButton(context.getString(R.string.add_dialog_fragment_ok),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                getDialog().dismiss();
                            }
                        })
                .setNegativeButton(context.getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        getDialog().dismiss();
                    }
                })
                .create();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

    }
}
