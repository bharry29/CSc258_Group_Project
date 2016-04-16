package com.csus.csc258.csc258_group_project;

import android.net.wifi.p2p.WifiP2pManager;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by Ben on 4/16/2016.
 */
public class GroupConnActionListener implements WifiP2pManager.ActionListener {
    // For debug
    private static final String TAG = "GroupConnActionListener";

    private Group mGroup;
    private GroupView mGroupView;

    public GroupConnActionListener(Group group, GroupView view) {
        mGroup = group;
        mGroupView = view;
    }

    @Override
    public void onSuccess() {
        Log.d(TAG, "Successfully connected to peer " + mGroup.getDevice().deviceName);
        mGroup.setStats(GroupStatus.JOINED);
        mGroupView.updateGroupButton(mGroup);
    }

    @Override
    public void onFailure(int reason) {
        Log.w(TAG, "Failed to connect to peer " + mGroup.getDevice().deviceName);
        switch (reason) {
            case WifiP2pManager.BUSY:
                Log.w(TAG, "Reason: Busy");
                break;
            case WifiP2pManager.P2P_UNSUPPORTED:
                Log.w(TAG, "Reason: P2P Unsupported");
                break;
            case WifiP2pManager.ERROR:
                Log.w(TAG, "Reason: Error");
                break;
            default:
                Log.w(TAG, "Reason: Unknown Issue");
        }

        Toast.makeText(mGroupView.getActivity(), "Connect failed. Retry.",
                Toast.LENGTH_SHORT).show();
    }
}
