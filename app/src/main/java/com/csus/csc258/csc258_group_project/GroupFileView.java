package com.csus.csc258.csc258_group_project;

import android.app.Fragment;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * View of files that are owned by various groups
 */
public class GroupFileView extends Fragment implements View.OnClickListener {
    View root_view;

    private static final String TAG = "GroupFileView";

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        root_view = inflater.inflate(R.layout.content_file, container, false);
        ImageButton b = (ImageButton) root_view.findViewById(R.id.btnCreate);
        b.setOnClickListener(this);

        MainActivity activity = (MainActivity) getActivity();
        for (GroupFile f : activity.getGroupFiles()) {
            createGroupFile(f);
        }

        for (Group g : activity.getGroups()) {
            createHeader(g.getName());
            for (GroupFile f : g.getFiles()) {
                createGroupFile(f);
            }
        }
        return root_view;
    }

    @Override
    public void onClick(View v) {
        final MainActivity activity = (MainActivity) getActivity();
        // The "Add File" button was pressed
        if (v.getId() == R.id.btnCreate) {
            TextDialogBox newFileWindow = new TextDialogBox();
            newFileWindow.setHint(getResources().getString(R.string.file_prompt));
            newFileWindow.setTitle(getResources().getString(R.string.add_file_title));
            newFileWindow.show(getFragmentManager(), "FileName");
        }

        // See if one of the group buttons were pressed
        GroupFile fileToDelete = null; // Will store file to delete if a delete button was pressed
        for (GroupFile f : ((MainActivity) getActivity()).getGroupFiles()) {
            if (f.getFileId() == v.getId()) {
                Button b = (Button) v;
                // Delete button was pressed
                if (b.getText().equals(getString(R.string.file_delete_button)))
                    if(f.deleteFile())
                        Log.d(TAG, "Successfully deleted file before removing from group");
                    else
                        Log.w(TAG, "Was not able to delete file before removing form group");
                    fileToDelete = f;
            }
        }
        if (fileToDelete != null)
            activity.deleteGroupFile(fileToDelete);

        // See if a file name was clicked
        if (v instanceof TextView) {
            TextView txtView = (TextView)v;
            // Get group ID from delete button
            int id = ((Button)((LinearLayout)txtView.getParent()).getChildAt(1)).getId();
            GroupFile fileToOpen = null;

            // See if it is one of the owned files
            for(GroupFile f : ((MainActivity) getActivity()).getGroupFiles())
                if (f.getFileId() == id)
                    fileToOpen = f;
            // See if it is one of the remote files
            for(Group g : ((MainActivity) getActivity()).getGroups())
                for(GroupFile f : g.getFiles())
                    if (f.getFileId() == id)
                        fileToOpen = f;

            // Open the file
            if (fileToOpen != null && txtView.getText().equals(fileToOpen.getFileName())) {
                Uri uri = Uri.fromFile(fileToOpen.getFileObject());
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(uri, "text/plain");
                root_view.getContext().startActivity(intent);
            }
        }

        /* Removing: Not currently implemented
        if (v.getId() == R.id.rnmfilebtn) {
            TextDialogBox newFileWindow = new TextDialogBox();
            newFileWindow.setHint(getResources().getString(R.string.file_prompt));
            newFileWindow.setTitle(getResources().getString(R.string.add_file_title));
            newFileWindow.show(getFragmentManager(), "FileName");
        }
        */
    }

    private void createHeader(String headerName) {
        LinearLayout lFileList = (LinearLayout) root_view.findViewById(R.id.llFileList);

        // Create new row
        LinearLayout lNewHeader = new LinearLayout(root_view.getContext());
        lNewHeader.setOrientation(LinearLayout.HORIZONTAL);
        lNewHeader.setLayoutParams(new LinearLayout
                .LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));

        // Create a new text view with the header
        TextView txtHeader = new TextView(root_view.getContext());
        txtHeader.setText(headerName);
        txtHeader.setTypeface(Typeface.DEFAULT_BOLD);
        lNewHeader.addView(txtHeader);

        // Add header to row
        lFileList.addView(lNewHeader);
    }

    private void createGroupFile(GroupFile gf) {
        // Get the layout for the list of files
        LinearLayout lFileList = (LinearLayout) root_view.findViewById(R.id.llFileList);

        // Create a new row
        LinearLayout lNewFile = new LinearLayout(root_view.getContext());
        lNewFile.setOrientation(LinearLayout.HORIZONTAL);
        lNewFile.setLayoutParams(new LinearLayout
                .LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));

        // Create a new text file name
        TextView txtFileName = new TextView(root_view.getContext());
        txtFileName.setText(gf.getFileName());
        txtFileName.setOnClickListener(this);
        lNewFile.addView(txtFileName);

        // Create a new delete button
        Button bDelete = new Button(root_view.getContext());
        bDelete.setText(getString(R.string.file_delete_button));
        bDelete.setId(gf.getFileId());
        bDelete.setOnClickListener(this);
        lNewFile.addView(bDelete);

        // Add the new file to the list of files
        lFileList.addView(lNewFile);
    }

}