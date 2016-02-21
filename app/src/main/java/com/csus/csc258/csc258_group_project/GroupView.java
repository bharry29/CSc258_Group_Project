package com.csus.csc258.csc258_group_project;

import android.app.Fragment;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.widget.LinearSmoothScroller;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Group Editor Fragment
 * @author bwhite
 */
public class GroupView extends Fragment implements View.OnClickListener {
    View root_view;

    private ArrayList<Button> bDeleteButtons;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        root_view = inflater.inflate(R.layout.content_group,container, false);

        // Setup view objects and default the on click listener
        bDeleteButtons = new ArrayList<>();
        ImageButton b = (ImageButton) root_view.findViewById(R.id.btnCreate);
        b.setOnClickListener(this);
        b = (ImageButton) root_view.findViewById(R.id.btnRefresh);
        b.setOnClickListener(this);


        return root_view;
    }

    @Override
    public void onClick(View v) {

        // The add GroupView button was pressed
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
        lNewGroup.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));

        // Create a new GroupView name
        TextView txtGroupName = new TextView(root_view.getContext());
        txtGroupName.setText("Test New Group");
        txtGroupName.setTextAppearance(root_view.getContext(), android.R.style.TextAppearance_Medium);
        txtGroupName.setWidth(0);
        txtGroupName.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));
        lNewGroup.addView(txtGroupName);

        // Create a new delete button
        Button bDelete = new Button(root_view.getContext());
        bDelete.setText(getString(R.string.group_delete_button));
        bDelete.setOnClickListener(this);
        bDelete.setWidth((int)getResources().getDimension(R.dimen.group_button_width));
        bDeleteButtons.add(bDelete);
        lNewGroup.addView(bDelete);

        // Add the new GroupView to the list of groups
        lGroupList.addView(lNewGroup);
    }
}
