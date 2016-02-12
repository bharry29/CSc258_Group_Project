package com.csus.csc258.csc258_group_project;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.LinearSmoothScroller;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Group Editor Fragment
 * @author bwhite
 */
public class group extends Fragment implements View.OnClickListener {
    View root_view;

    private ArrayList<Button> bDeleteButtons;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        root_view = inflater.inflate(R.layout.content_group,container, false);

        bDeleteButtons = new ArrayList<>();

        bDeleteButtons.add((Button)root_view.findViewById(R.id.btnDelete));

        Button b = (Button) root_view.findViewById(R.id.btnCreate);
        b.setOnClickListener(this);

        return root_view;
    }

    @Override
    public void onClick(View v) {

        // The "Create new Group" button was pressed
        if(v.getId() == R.id.btnCreate)
            createGroup();

        // A delete button was pressed
        if(bDeleteButtons.contains(v)) {
            bDeleteButtons.remove(v); // Remove from collection
            // Remove from layout
            LinearLayout lGroup = (LinearLayout) v.getParent();
            LinearLayout lContainer = (LinearLayout) lGroup.getParent();
            lContainer.removeView(lGroup);
        }

    }

    private void createGroup() {
        // Get the layout for the list of views
        LinearLayout lGroupList = (LinearLayout) root_view.findViewById(R.id.llGroupList);

        // Create a new row
        LinearLayout lNewGroup = new LinearLayout(root_view.getContext());
        lNewGroup.setOrientation(LinearLayout.HORIZONTAL);

        // Create a new group name
        TextView txtGroupName = new TextView(root_view.getContext(), null, R.style.GroupName);
        txtGroupName.setText("Test New Group");
        lNewGroup.addView(txtGroupName);

        // Create a new delete button
        Button bDelete = new Button(root_view.getContext(), null, R.style.MediumButton);
        bDelete.setText(getString(R.string.group_delete_button));
        bDelete.setOnClickListener(this);
        bDeleteButtons.add(bDelete);
        lNewGroup.addView(bDelete);

        // Add the new group to the list of groups
        lGroupList.addView(lNewGroup);
    }
}
