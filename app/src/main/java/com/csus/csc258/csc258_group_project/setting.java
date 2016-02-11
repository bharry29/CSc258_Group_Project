package com.csus.csc258.csc258_group_project;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.NavigationView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.content.SharedPreferences;
import android.widget.EditText;
import android.content.Context;
import android.widget.TextView;

/**
 * Created by Yulong on 2016/2/4.
 */
public class setting extends Fragment {
    View root_view;
    EditText EditText1,EditText2,EditText3;
    Button button1,button2;
    public static final String MyPREFERENCES = "MyPrefs" ;
    SharedPreferences sharedpreferences;
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        root_view = inflater.inflate(R.layout.content_setting,container, false);
        button1 = (Button)root_view.findViewById(R.id.setting_button1);
        button2 = (Button)root_view.findViewById(R.id.setting_button2);
        EditText1 = (EditText)root_view.findViewById(R.id.edit_id);
        EditText2 = (EditText)root_view.findViewById(R.id.setting_editText);
        EditText3 = (EditText)root_view.findViewById(R.id.setting_editText2);
        sharedpreferences = getActivity().getSharedPreferences(MyPREFERENCES,0);
        button1.setOnClickListener(new OnClickListener(){
            public void onClick(View v)
            {
               String temp_user = EditText1.getText().toString();
                SharedPreferences.Editor editor = sharedpreferences.edit();
                editor.putString("username", temp_user);
                editor.commit();
            }
        });
        button2.setOnClickListener(new OnClickListener(){
            public void onClick(View v)
            {
                String temp_username = EditText2.getText().toString();
                String temp_password = EditText3.getText().toString();
                SharedPreferences.Editor editor = sharedpreferences.edit();
                editor.putString("google_username", temp_username);
                editor.putString("google_password", temp_password);
                editor.commit();
            }
        });
        return root_view;
    }
}
