package com.csus.csc258.csc258_group_project;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.List;

/**
 * Used to connect to a ExchangeGroupsServer and download groups available
 * @author Ben White
 */
public class ExchangeGroupsClient extends AsyncTask<Void, Void, Void> {
    private InetAddress mHostAddress;
    private int mHostPort;
    private MainActivity mActivity;

    // For debugging
    private static final String TAG = "ExchangeGroupsClient";

    public ExchangeGroupsClient(InetAddress hostAddress, int hostPort, MainActivity activity) {
        mHostAddress = hostAddress;
        mHostPort = hostPort;
        mActivity = activity;
    }

    /**
     * Get groups from server and update the activity with the new group available
     * @param params The current list of groups available
     */
    @Override
    protected Void doInBackground(Void... params) {
        Socket socket = null;
        DataInputStream dataInputStream = null;
        DataOutputStream dataOutputStream = null;
        Group activeGroup = null;
        String deviceName, fileName;
        // True server sends a group that we already have
        boolean haveGroup = false;

        try {
            // Create a new Socket instance and connect to host
            socket = new Socket(mHostAddress, mHostPort);

            Log.i(TAG, "Connected to " + socket.toString());

            dataInputStream = new DataInputStream(socket.getInputStream());
            dataOutputStream = new DataOutputStream(socket.getOutputStream());

            // Get device name
            deviceName = dataInputStream.readUTF();

            // Send confirmation
            dataOutputStream.writeUTF(mActivity.getString(R.string.socket_client_received_deviceName));

            // Find active group
            for(Group g : mActivity.getGroups()) {
                if(g.getName() == deviceName) {
                    activeGroup = g;
                    break;
                }
            }

            // If couldn't find a group the close connection
            if(activeGroup == null) {
                dataInputStream.close();
                dataOutputStream.close();
                socket.close();
            }
            else {
                while ((fileName = dataInputStream.readUTF()) != null) {
                    // Send confirmation
                    dataOutputStream.writeUTF(mActivity.getString(R.string.socket_client_received_fileName));

                    // Check to see if the file exsits
                    GroupFile receivedFile = null;
                    for (GroupFile gf : activeGroup.getFiles()) {
                        if(gf.getFileName() == fileName) {
                            receivedFile = gf;
                            if (receivedFile.deleteFile())
                                Log.d(TAG, "doInBackground: Deleted existing local copy of " + gf.getFileName());
                            else
                                Log.w(TAG, "doInBackground: Was not able to delete " + gf.getFileName());
                        }
                    }

                    // If file doesn't exist, then create it
                    if (receivedFile == null)
                        receivedFile = new GroupFile(fileName, mActivity.getApplicationContext(), deviceName, "");

                    // Create file content from input stream
                    receivedFile.createFromInputStream(dataInputStream);
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if (dataOutputStream != null) {
                try {
                    dataOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if (dataInputStream != null) {
                try {
                    dataInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }
}
