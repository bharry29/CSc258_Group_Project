package com.csus.csc258.csc258_group_project;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Yulong on 2016/2/4.
 */
public class group extends Fragment {
    View rootview;
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        rootview = inflater.inflate(R.layout.content_group,container, false);
        return rootview;
    }
}
