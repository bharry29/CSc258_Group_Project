package com.csus.csc258.csc258_group_project;

import android.app.DialogFragment;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;

/**
 * Group Editor Fragment. Has the button click event handlers for the group view
 * @author Ben White
 */
public class GroupView extends Fragment implements View.OnClickListener {
    View root_view;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        root_view = inflater.inflate(R.layout.content_group,container, false);

        // Setup view objects and default the on click listener
        ImageButton b = (ImageButton) root_view.findViewById(R.id.btnCreate);
        b.setOnClickListener(this);
        b = (ImageButton) root_view.findViewById(R.id.btnRefresh);
        b.setOnClickListener(this);

        // Display the group objects
        MainActivity activity = (MainActivity)getActivity();
        for(Group g : activity.getGroups()) {
            createGroup(g.getName(), g.getId());
        }


        return root_view;
    }

    @Override
    public void onClick(View v) {
        // The add group button was pressed
        if(v.getId() == R.id.btnCreate) {
            TextDialogBox newGroupWindow = new TextDialogBox();
            newGroupWindow.setHint(getResources().getString(R.string.group_create_prompt));
            newGroupWindow.setTitle(getResources().getString(R.string.group_create_title));
            newGroupWindow.show(getFragmentManager(), "groupName");
        }
        else if(v.getId() == R.id.btnRefresh) {
            ((MainActivity) getActivity()).refreshPeers();
        }
        else {
            // See if the view id is one of the groups
            for (Group g : ((MainActivity) getActivity()).getGroups()) {
                if (g.getId() == v.getId()) {
                    Button b = (Button) v;
                    // Delete button was pressed
                    if (b.getText().equals(getString(R.string.group_delete_button)))
                        ((MainActivity) getActivity()).deleteGroup(g);
                }
            }
        }

    }

    private void createGroup(String input, int id) {

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
        txtGroupName.setText(input);
        // Using deprecated method to support older API's
        txtGroupName.setTextAppearance(root_view.getContext(),
                android.R.style.TextAppearance_Medium);
        txtGroupName.setWidth(0);
        txtGroupName.setLayoutParams(new LinearLayout
                .LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));
        lNewGroup.addView(txtGroupName);

        // Create a new delete button
        Button bDelete = new Button(root_view.getContext());
        bDelete.setText(getString(R.string.group_delete_button));
        bDelete.setOnClickListener(this);
        bDelete.setWidth((int) getResources().getDimension(R.dimen.group_button_width));
        bDelete.setId(id);
        lNewGroup.addView(bDelete);

        // Add the new GroupView to the list of groups
        lGroupList.addView(lNewGroup);
    }
}
