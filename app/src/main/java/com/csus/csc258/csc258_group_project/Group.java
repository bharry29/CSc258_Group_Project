package com.csus.csc258.csc258_group_project;

import android.net.wifi.p2p.WifiP2pDevice;
import android.util.Log;
import android.view.View;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Represents a group that is being shared. Groups can be owned by the current user
 * or another peer on the WiFi network.
 * @author Ben White
 */
public class Group {


    // Collection of files
    private List<GroupFile> mFiles;

    // The name of the group
    private String mGroupName;

    // The id of the group
    private int mId;

    // The Wifi Direct Device
    private WifiP2pDevice mDevice;

    // For debugging
    private static final String TAG = "Group";

    /**
     * Create a new group object
     * @param name The name of the group
     * @param device The device that owns the group
     * @see WifiP2pDevice
     */
    public Group(String name, WifiP2pDevice device) {
        mGroupName = name;
        mDevice = device;
        mId = View.generateViewId();
        mFiles = new ArrayList<>();
    }

    /**
     * Gets the name of the group
     * @return The name of the group
     */
    public String getName() { return mGroupName; }

    /**
     * Gets the device that owns the group
     * @return The device that owns the group
     */
    public WifiP2pDevice getDevice() { return mDevice; }

    /** Not using this method
    public JSONObject getJSONData() {
        JSONObject jsonData = new JSONObject();

        try {
            jsonData.put("groupName", mGroupName);
            jsonData.put("status", mStatus);
            jsonData.put("groupID", mId);
            jsonData.put("deviceAddress", mDeviceAddress);
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e(TAG, "Coultn't create JSON object");
            jsonData = null;
        }

        return jsonData;
    }
     */


    public List<GroupFile> getFiles() {
        return mFiles;
    }

    public void deleteFiles(List<GroupFile> files) {
        mFiles.removeAll(files);
    }

    public void deleteFile(GroupFile file) {
        mFiles.remove(file);
    }

    public void addFile(GroupFile file) {
        mFiles.add(file);
    }

    public void addFiles(List<GroupFile> files) {
        mFiles.addAll(files);
    }

    public int getId() { return mId; }

    public List<String> groupNamesList()
    {
        List<Group> groupsList = new ArrayList<>();
        List<String> groupNamesList = new ArrayList<>();
        for (Group g: groupsList)
            groupNamesList.add(g.getName());
        return groupNamesList;
    }
}
