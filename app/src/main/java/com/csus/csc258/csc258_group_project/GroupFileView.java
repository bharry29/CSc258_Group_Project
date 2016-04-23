package com.csus.csc258.csc258_group_project;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.util.ArrayList;

/**
 * Created by Yulong on 2016/2/4.
 */
public class GroupFileView extends Fragment implements View.OnClickListener {
    View root_view;

    private ArrayList<Button> bDeleteButtons;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        root_view = inflater.inflate(R.layout.content_file, container, false);
        bDeleteButtons = new ArrayList<>();
        bDeleteButtons.add((Button)root_view.findViewById(R.id.delfilebtn));
        Button b = (Button) root_view.findViewById(R.id.addfilebtn);
        b.setOnClickListener(this);

        MainActivity activity = (MainActivity) getActivity();
        for (GroupFile f : activity.getGroupFiles()) {
            createGroupFile(f);
        }
        return root_view;
    }

    @Override
    public void onClick(View v) {
        final MainActivity activity = (MainActivity) getActivity();
        // The "Add File" button was pressed
        if (v.getId() == R.id.addfilebtn) {
            TextDialogBox newFileWindow = new TextDialogBox();
            newFileWindow.setHint(getResources().getString(R.string.file_prompt));
            newFileWindow.setTitle(getResources().getString(R.string.add_file_title));
            newFileWindow.show(getFragmentManager(), "FileName");
        }

        // A delete button was pressed
        for (GroupFile f : ((MainActivity) getActivity()).getGroupFiles()) {
            if (f.getFileId() == v.getId()) {
                Button b = (Button) v;
                // Delete button was pressed
                if (b.getText().equals(getString(R.string.delfilebtn)))
                    activity.deleteGroupFile(f);
            }
        }

        if (v.getId() == R.id.rnmfilebtn) {
            TextDialogBox newFileWindow = new TextDialogBox();
            newFileWindow.setHint(getResources().getString(R.string.file_prompt));
            newFileWindow.setTitle(getResources().getString(R.string.add_file_title));
            newFileWindow.show(getFragmentManager(), "FileName");
            // TODO: Rename the File on the File System
        }
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
        lNewFile.addView(txtFileName);


        // Create a new delete button
        Button bDelete = new Button(root_view.getContext());
        bDelete.setText(getString(R.string.delfilebtn));
        bDelete.setId(gf.getFileId());
        bDelete.getId();
        bDelete.setOnClickListener(this);
        bDeleteButtons.add(bDelete);
        lNewFile.addView(bDelete);

        // Add the new file to the list of files
        lFileList.addView(lNewFile);
    }

}