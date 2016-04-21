package com.csus.csc258.csc258_group_project;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileWriter;
import java.util.*;

/**
 * Created by Yulong on 2016/2/4.
 */
public class GroupFileView extends Fragment implements View.OnClickListener {
    View root_view;

    private ArrayList<Button> bDeleteButtons;
    private Group grp;

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
                    //Delete the file
                    ;
            }
        }
    }

    private void createGroupFile(GroupFile gf) {
        // Get the layout for the list of files
        LinearLayout lFileList = (LinearLayout) root_view.findViewById(R.id.llFileList);

        // Create a new row
        LinearLayout lNewFile = new LinearLayout(root_view.getContext());
        lNewFile.setOrientation(LinearLayout.HORIZONTAL);

        // Create a new text file name
        TextView txtFileName = new TextView(root_view.getContext());
        txtFileName.setText(gf.getFileName());
        writeFileOnInternalStorage(getActivity(),gf.getFileName(),"Sample Body");
        lNewFile.addView(txtFileName);


        // Create a new delete button
        Button bDelete = new Button(root_view.getContext());
        bDelete.setText(getString(R.string.delfilebtn));
        bDelete.setId(gf.getFileId());
        bDelete.setOnClickListener(this);
        bDeleteButtons.add(bDelete);
        lNewFile.addView(bDelete);

        // Add the new file to the list of files
        lFileList.addView(lNewFile);
    }

    public void writeFileOnInternalStorage(Context mcoContext,String sFileName, String sBody){
        File samplefiledir = new File(mcoContext.getFilesDir(),"sampledata");
        Boolean write_successful = false;
        if(!samplefiledir.exists()){
            samplefiledir.mkdir();
        }
        try{
            File samplefile = new File(samplefiledir, sFileName+ ".txt");
            FileWriter writer = new FileWriter(samplefile);
            writer.append(sBody);
            writer.flush();
            writer.close();
            write_successful = true;
            Toast.makeText(this.getActivity().getApplicationContext(), "Sample File Created Successfully in : " + samplefiledir + "\t with the name:\t"+sFileName , Toast.LENGTH_SHORT).show();
        }
        catch (Exception e){
            Toast.makeText(this.getActivity().getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
            Log.e("ERROR:---", "Could not write file" + e.getMessage());
            write_successful = false;
        }
    }
}