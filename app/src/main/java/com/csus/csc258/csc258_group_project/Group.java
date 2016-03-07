package com.csus.csc258.csc258_group_project;

import android.provider.Settings;
import android.util.Log;
import android.view.View;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * OWNED = This device is the group owner
 * JOINED = This device is not the owner but is a group member
 * AVAILABLE = This device is not a member of the groupt but it is available to join
 */
enum GroupStatus {OWNED, JOINED, AVAILABLE}

/**
 * Represents a group that is being shared. Groups can be owned by the current user
 * or another peer on the WiFi network.
 * @author Ben White
 */
public class Group {

    // The name of the group
    private String mGroupName;

    // The status of the group (owned, joined or available to join)
    private GroupStatus mStatus;

    // The id of the group
    private int mId;

    // The device owner id
    private String mDeviceOwnerID;

    // For debugging
    private static final String TAG = "Group";

    /**
     * Create a new group object
     * @param status The status of the group (owned, joined or available)
     * @param name The name of the group
     * @see GroupStatus
     */
    public Group(GroupStatus status, String name, String deviceOwnerID) {
        mStatus = status;
        mGroupName = name;
        mDeviceOwnerID = deviceOwnerID;
        mId = View.generateViewId();
    }

    /**
     * Gets the name of the group
     * @return The name of the group
     */
    public String getName() { return mGroupName; }

    /**
     * Gets the id of the device that owns the group
     * @return The ID of the device that owns the group
     */
    public String getDeviceOwnerID() { return mDeviceOwnerID; }

    /**
     * Gets the status of the group
     * @return The status of the group
     * @see GroupStatus
     */
    public GroupStatus getStatus() {
        return mStatus;
    }

    public JSONObject getJSONData() {
        JSONObject jsonData = new JSONObject();

        try {
            jsonData.put("groupName", mGroupName);
            jsonData.put("status", mStatus);
            jsonData.put("groupID", mId);
            jsonData.put("deviceOwnerID", mDeviceOwnerID);
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e(TAG, "Coultn't create JSON object");
            jsonData = null;
        }

        return jsonData;
    }

    public int getId() { return mId; }
}
