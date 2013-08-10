package com.vladstoick.DialogFragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockDialogFragment;
import com.vladstoick.OttoBus.BusProvider;
import com.vladstoick.OttoBus.DataModifiedEvent;
import com.vladstoick.stiridinromania.R;

/**
 * Created by Vlad on 8/10/13.
 */
public class RenameDialogFragment extends SherlockDialogFragment
        implements EditText.OnEditorActionListener {
    public class ElementRenamedEvent {
        public final String type;
        public final int id;
        public final String newName;

        ElementRenamedEvent(String type, int id, String newName) {
            this.type = type;
            this.id = id;
            this.newName = newName;
        }
    }

    public static final String TAG = "RENAMEDIALOGFRAGMENT";
    public static final String GROUP_TAG = "GROUP";
    public static final String SOURCE_TAG = "SOURCE";
    private String type;
    private int id;
    EditText mEditText;

    public RenameDialogFragment(String type, int id) {
        this.type = type;
        this.id = id;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        BusProvider.getInstance().register(this);
        mEditText = new EditText(getSherlockActivity());
        AlertDialog builder = new AlertDialog.Builder(getSherlockActivity())
                .setView(mEditText)
                .setPositiveButton(getSherlockActivity().getString(R.string.ok),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                BusProvider.getInstance().post(new ElementRenamedEvent(type, id,
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
        InputMethodManager imm = (InputMethodManager) getActivity().
                getSystemService(Context.INPUT_METHOD_SERVICE);
        mEditText.requestFocus();
        builder.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        if (type == GROUP_TAG) {
            builder.setTitle(getString(R.string.rename) + " " + getString(R.string.group));
        } else {
            builder.setTitle(getString(R.string.rename) + " " + getString(R.string.feed));
        }
        return builder;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        BusProvider.getInstance().unregister(this);
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        return false;
    }
}
