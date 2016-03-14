package com.csus.csc258.csc258_group_project;

import android.os.AsyncTask;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.List;

/**
 * Used to connect to a ExchangeGroupsServer and download groups available
 * @author Ben White
 */
public class ExchangeGroupsClient extends AsyncTask<List<Group>, Void, Void> {
    private String mHostAddress;
    private int mHostPort;
    private MainActivity mActivity;

    // For debugging
    private static final String TAG = "ExchangeGroupsClient";

    public ExchangeGroupsClient(String hostAddress, int hostPort, MainActivity activity) {
        mHostAddress = hostAddress;
        mHostPort = hostPort;
        mActivity = activity;
    }

    /**
     * Get groups from server and update the activity with the new group available
     * @param params The current list of groups available
     */
    @Override
    protected Void doInBackground(List<Group>... params) {
        Socket socket;
        DataInputStream dataInputStream;
        String groupInformation, groupName, groupOwner;
        int groupID;
        JSONObject jsondata;
        // True server sends a group that we already have
        boolean haveGroup = false;

        try {
            // Create a new Socket instance and connect to host
            socket = new Socket(mHostAddress, mHostPort);

            dataInputStream = new DataInputStream(socket.getInputStream());

            groupInformation = dataInputStream.readUTF();

            jsondata = new JSONObject(groupInformation);

            groupName = jsondata.getString("groupName");
            groupID = jsondata.getInt("groupID");
            groupOwner = jsondata.getString("deviceAddress");

            for (Group g : params[0]) {
                if (g.getId() == groupID && g.getDeviceOwnerID() == groupOwner) {
                    haveGroup = true;
                    break;
                }
            }

            if (!haveGroup) {
                Group g = new Group(GroupStatus.AVAILABLE, groupName, groupOwner);
                mActivity.addGroup(g);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        } finally {

        }
        return null;
    }
}
