package com.csus.csc258.csc258_group_project;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

/**
 * Group Editor Fragment
 * @author bwhite
 */
public class group extends Fragment implements View.OnClickListener {
    View root_view;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        root_view = inflater.inflate(R.layout.content_group,container, false);

        Button b = (Button) root_view.findViewById(R.id.btnCreate);
        b.setOnClickListener(this);

        return root_view;
    }

    @Override
    public void onClick(View v) {

    }
}
