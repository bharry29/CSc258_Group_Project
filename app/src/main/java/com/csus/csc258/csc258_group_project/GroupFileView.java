package com.csus.csc258.csc258_group_project;

import android.app.Fragment;
import android.os.Bundle;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import java.util.*;

/**
 * Created by Yulong on 2016/2/4.
 */
public class GroupFileView extends Fragment implements View.OnClickListener {
    View root_view;

//    private String root;
//    private String currentPath;
//    private File targetFile;
//
    private ArrayList<Button> bDeleteButtons;
    private Group grp;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        root_view = inflater.inflate(R.layout.content_file, container, false);
        bDeleteButtons = new ArrayList<>();
        bDeleteButtons.add((Button)root_view.findViewById(R.id.delfilebtn));
        Button b = (Button) root_view.findViewById(R.id.addfilebtn);
        b.setOnClickListener(this);

        MainActivity activity = (MainActivity) getActivity();
        for (Group g : activity.getGroups()) {
            for (GroupFile f : g.getFiles()) {
                addFile(f.getFileName(), f.getFileId());
            }
        }
        return root_view;
    }

    @Override
    public void onClick(View v) {

        // The "Add File" button was pressed
        if (v.getId() == R.id.addfilebtn) {
            TextDialogBox newFileWindow = new TextDialogBox();
            newFileWindow.setHint(getResources().getString(R.string.file_prompt));
            newFileWindow.setTitle(getResources().getString(R.string.add_file_title));

//            Spinner groupsSpinner  = (Spinner) root_view.findViewById(R.id.nav_group);
//            ArrayAdapter<String> groupDataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, groupsList);

            newFileWindow.show(getFragmentManager(), "FileName");
        }

        // A delete button was pressed
        for (Group g : ((MainActivity) getActivity()).getGroups()) {
            for (GroupFile f : g.getFiles()) {
                if (f.getFileId() == v.getId()) {
                    Button b = (Button) v;
                    // Delete button was pressed
                    if (b.getText().equals(getString(R.string.delfilebtn)))
                        g.deleteFile(f);

                }
            }
        }

    }

    private void addFile(String filename, int id) {
        // Get the layout for the list of files
        LinearLayout lFileList = (LinearLayout) root_view.findViewById(R.id.llFileList);

        // Create a new row
        LinearLayout lNewFile = new LinearLayout(root_view.getContext());
        lNewFile.setOrientation(LinearLayout.HORIZONTAL);

        // Create a new group name
        TextView txtFileName = new TextView(new ContextThemeWrapper());
        txtFileName.setText("New File");
        lNewFile.addView(txtFileName);

        // Create a new delete button
        Button bDelete = new Button(new ContextThemeWrapper());
        bDelete.setText(getString(R.string.delfilebtn));
        bDelete.setOnClickListener(this);
        bDeleteButtons.add(bDelete);
        lNewFile.addView(bDelete);

        // Add the new file to the list of files
        lFileList.addView(lNewFile);
    }
}