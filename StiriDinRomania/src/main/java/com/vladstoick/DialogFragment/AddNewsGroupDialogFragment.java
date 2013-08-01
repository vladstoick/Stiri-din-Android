package com.vladstoick.DialogFragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockDialogFragment;
import com.vladstoick.OttoBus.BusProvider;
import com.vladstoick.OttoBus.DataModifiedEvent;
import com.vladstoick.stiridinromania.R;

/**
 * Created by Vlad on 7/23/13.
 */
public class AddNewsGroupDialogFragment extends SherlockDialogFragment implements EditText.OnEditorActionListener {
    private EditText mEditText;
    private Button mPositive;
    private Button mNegative;
    public static String TAG = "ADDDIALOGGROUP";

    public AddNewsGroupDialogFragment() {
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater inflater = LayoutInflater.from(getSherlockActivity());

        BusProvider.getInstance().register(this);
        View mView = inflater.inflate(R.layout.dialog_addnewgroup, null);
        mEditText = (EditText) mView.findViewById(R.id.newGroupEditText);
        return new AlertDialog.Builder(getSherlockActivity())
                .setView(mView)
                .setPositiveButton(getSherlockActivity().getString(R.string.add_dialog_fragment_ok),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                BusProvider.getInstance().post(new DataModifiedEvent(DataModifiedEvent.TAG_GROUPADD,
                                        mEditText.getText().toString()));
                                getDialog().dismiss();
                            }
                        })
                .setNegativeButton(getSherlockActivity().getString(R.string.cancel),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                getDialog().dismiss();
                            }
                        })
                 .create();
    }


    @Override
    public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
        if (EditorInfo.IME_ACTION_DONE == actionId) {
            BusProvider.getInstance().post(new DataModifiedEvent(DataModifiedEvent.TAG_GROUPADD,
                    mEditText.getText().toString()));
            this.dismiss();
            return true;
        }
        return false;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        BusProvider.getInstance().unregister(this);
    }
}