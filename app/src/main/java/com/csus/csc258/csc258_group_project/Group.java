package com.csus.csc258.csc258_group_project;

import android.view.View;

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

    /**
     * Create a new group object
     * @param status The status of the group (owned, joined or available)
     * @param name The name of the group
     * @see GroupStatus
     */
    public Group(GroupStatus status, String name) {
        mStatus = status;
        mGroupName = name;
        mId = View.generateViewId();
    }

    /**
     * Gets the name of the group
     * @return The name of the group
     */
    public String getName() {
        return mGroupName;
    }

    /**
     * Gets the status of the group
     * @return The status of the group
     * @see GroupStatus
     */
    public GroupStatus getStatus() {
        return mStatus;
    }

    public int getId() { return mId; }
}
