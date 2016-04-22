package com.csus.csc258.csc258_group_project;

import android.app.Fragment;
import android.content.Context;
import android.content.ContextWrapper;
import android.os.Bundle;
import android.provider.Settings;
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
                    deleteFileFromInternalStorage(getActivity(),f.getFileName());
                    //Delete the file
                    ;
            }
        }

        if (v.getId() == R.id.rnmfilebtn) {
            TextDialogBox newFileWindow = new TextDialogBox();
            newFileWindow.setHint(getResources().getString(R.string.file_prompt));
            newFileWindow.setTitle(getResources().getString(R.string.add_file_title));
            newFileWindow.show(getFragmentManager(), "FileName");
            renameGroupFile("");
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
        writeFileOnInternalStorage(getActivity(),gf.getFileName(),"Sample Body");
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

    public void writeFileOnInternalStorage(Context mcoContext,String sFileName, String sBody){
        //File samplefiledir = new File(mcoContext.getFilesDir(),"sampledata");
        String device_id = getDeviceId(mcoContext);
        File samplefiledir = new File(mcoContext.getFilesDir().getAbsolutePath() + File.separator + device_id);
        Boolean write_successful = false;
        if(!samplefiledir.exists()){
            samplefiledir.mkdir();
        }
        try{
            File samplefile = new File(samplefiledir, sFileName);
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

    //TODO
    public void deleteFileFromInternalStorage(Context mcoContext,String sFilename){
        //File samplefiledir = new File(mcoContext.getFilesDir(),"sampledata");
        String device_id = getDeviceId(mcoContext);
        File samplefiledir = new File(mcoContext.getFilesDir().getAbsolutePath() + File.separator + device_id);
        Boolean isDeleted = null;
        File f = new File(mcoContext.getFilesDir(),sFilename);
        try {
        if(f.exists()) {
                f.delete();
                isDeleted = true;
                Toast.makeText(this.getActivity().getApplicationContext(), "Sample File Deleted Successfully from : " + samplefiledir + "\t with the name:\t" + sFilename, Toast.LENGTH_SHORT).show();
            }
            else
            {
                isDeleted = false;
                Toast.makeText(this.getActivity().getApplicationContext(), "Could not delete file", Toast.LENGTH_SHORT).show();
            }
        }
        catch (Exception e){
            Toast.makeText(this.getActivity().getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
            isDeleted = false;
            Log.e("ERROR:---", "Could not delete file" + e.getMessage());
        }
    }

    //TODO
    public void renameGroupFile(String sFilename){
        Context mcoContext = this.getActivity().getApplicationContext();
        //File samplefiledir = new File(mcoContext.getFilesDir(),sFilename);
        String device_id = getDeviceId(mcoContext);
        File samplefiledir = new File(mcoContext.getFilesDir().getAbsolutePath() + File.separator + device_id);
        samplefiledir.renameTo(samplefiledir);
    }

    public String getDeviceId (Context context)
    {
        String device_id = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        return device_id;
    }
}