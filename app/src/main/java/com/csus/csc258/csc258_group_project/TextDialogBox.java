package com.csus.csc258.csc258_group_project;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.InputType;
import android.widget.EditText;

import org.w3c.dom.Text;

/**
 * Used to get the name of the new group to create.
 * Could also be used for other user input needs
 * @author Ben White
 */
public class TextDialogBox extends DialogFragment {
    private String mTitle = "";
    private String mHint = "";

    // The activity that creates an instance of this dialog must
    // implement this interface in order to receive event callbacks
    public interface TextDialogListener {
        void onDialogPositiveClick(DialogFragment dialog, String input);
    }

    private TextDialogListener mListener;

    public void setTitle(String title) {
        mTitle = title;
    }

    public String getTitle() { return  mTitle; }

    public void setHint(String hint) {
        mHint = hint;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            mListener = (TextDialogListener) activity;
        }
        catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement TextDialogListener");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(mTitle);

        // Set up the input
        final EditText input = new EditText(getActivity());
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        input.setHint(mHint);
        builder.setView(input);

        // Set up the buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mListener.onDialogPositiveClick(TextDialogBox.this, input.getText().toString());
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        return builder.create();
    }
}
