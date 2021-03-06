package com.csus.csc258.csc258_group_project;

import android.app.Fragment;
import android.net.wifi.p2p.WifiP2pDevice;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Group Editor Fragment. Has the button click event handlers for the group view
 * @author Ben White
 */
public class GroupView extends Fragment implements View.OnClickListener {
    View root_view;

    // For debug
    private static final String TAG = "GroupView";

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        root_view = inflater.inflate(R.layout.content_group,container, false);

        // Setup view objects and default the on click listener
        ImageButton b;
        //b = (ImageButton) root_view.findViewById(R.id.btnCreate);
        //b.setOnClickListener(this);
        b = (ImageButton) root_view.findViewById(R.id.btnRefresh);
        b.setOnClickListener(this);

        // Display the group objects
        MainActivity activity = (MainActivity)getActivity();
        for(Group g : activity.getGroups()) {
            createGroup(g);
        }


        return root_view;
    }

    @Override
    public void onClick(View v) {
        final MainActivity activity = (MainActivity) getActivity();
        // The add group button was pressed
        /* Removing create group handler */
        /*if(v.getId() == R.id.btnCreate) {
            TextDialogBox newGroupWindow = new TextDialogBox();
            newGroupWindow.setHint(getResources().getString(R.string.group_create_prompt));
            newGroupWindow.setTitle(getResources().getString(R.string.group_create_title));
            newGroupWindow.show(getFragmentManager(), "groupName");
        }*/
        /*else*/ if(v.getId() == R.id.btnRefresh) {
            activity.refreshPeers();
            for (Group g : activity.getGroups())
                updateGroupButton(g);
        }
        else {
            // See if the view id is one of the groups
            for (Group g : activity.getGroups()) {
                if (g.getId() == v.getId()) {
                    Button b = (Button) v;
                    // Delete button was pressed
                    /*
                    if (b.getText().equals(getString(R.string.group_delete_button)))
                        activity.deleteGroup(g);
                        */
                    // Join button was pressed
                    if (b.getText().equals(getString(R.string.group_join_button)) || b.getText().equals(getString(R.string.group_download_button)))
                        ((GroupBroadcastReceiver)activity.getReceiver())
                                .connectToDevice(g.getDevice(), new GroupConnActionListener(g, this));
                }
            }
        }
    }

    private void createGroup(Group g) {

        String buttonName = "";

        switch (g.getDevice().status) {
            /*case OWNED:
                buttonName = getString(R.string.group_delete_button);
                break;*/
            case WifiP2pDevice.AVAILABLE:
                buttonName = getString(R.string.group_join_button);
                break;
            case WifiP2pDevice.CONNECTED:
            case WifiP2pDevice.INVITED:
                buttonName = getString(R.string.group_download_button);
                break;
        }

        // Get the layout for the list of views
        LinearLayout lGroupList = (LinearLayout) root_view.findViewById(R.id.llGroupList);

        // Create a new row
        LinearLayout lNewGroup = new LinearLayout(root_view.getContext());
        lNewGroup.setOrientation(LinearLayout.HORIZONTAL);
        lNewGroup.setLayoutParams(new LinearLayout
                .LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));

        // Create a new GroupView name
        TextView txtGroupName = new TextView(root_view.getContext());
        txtGroupName.setText(g.getName());
        // Using deprecated method to support older API's
        txtGroupName.setTextAppearance(root_view.getContext(),
                android.R.style.TextAppearance_Medium);
        txtGroupName.setWidth(0);
        txtGroupName.setLayoutParams(new LinearLayout
                .LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));
        lNewGroup.addView(txtGroupName);

        // Create a new action button
        Button bButton = new Button(root_view.getContext());
        bButton.setText(buttonName);
        bButton.setOnClickListener(this);
        bButton.setWidth((int) getResources().getDimension(R.dimen.group_button_width));
        bButton.setId(g.getId());
        lNewGroup.addView(bButton);

        /** I don't think we need this, it is covered in the file creation ~BW 4/24
        //create a directory for every group that is created
        String newGroupDirectoryPath = "/data/data/com.csus.csc258.csc258_group_project/files" + File.separator + txtGroupName;
        File GroupDirectory = new File(newGroupDirectoryPath);
        // have the object build the directory structure, if needed.
        GroupDirectory.mkdirs();
        */

        // Add the new GroupView to the list of groups
        lGroupList.addView(lNewGroup);


    }

    /**
     * Call this method when the connection status has changed with a group, this will update
     * the button label
     * @param group The group that had a status change
     */
    public void updateGroupButton(Group group) {
        LinearLayout lGroupList = (LinearLayout) root_view.findViewById(R.id.llGroupList);

        for(int i = 0; i < lGroupList.getChildCount(); i++) {
            LinearLayout lGroup = (LinearLayout)lGroupList.getChildAt(i);
            TextView groupName = (TextView)lGroup.getChildAt(0);
            Button groupButton = (Button)lGroup.getChildAt(1);
            // Are we on the group that we need to change?
            if(groupName.getText().equals(group.getDevice().deviceName)) {
                String buttonName = "";
                switch (group.getDevice().status) {
                    case WifiP2pDevice.AVAILABLE:
                        buttonName = getString(R.string.group_join_button);
                        break;
                    case WifiP2pDevice.CONNECTED:
                    case WifiP2pDevice.INVITED:
                        buttonName = getString(R.string.group_download_button);
                        break;
                }
                groupButton.setText(buttonName);
            }
        }
    }
}
