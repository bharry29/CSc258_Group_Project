package com.csus.csc258.csc258_group_project;

import android.util.Log;
import org.json.JSONObject;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * This server thread accepts connections on port 8888 and transmits the list of owned
 * groups to any client that connects
 * @author Ben White
 */
public class ExchangeGroupsServer extends Thread {
    private MainActivity mActivity;

    private static final String TAG = "ExchangeGroupsServer";

    public ExchangeGroupsServer(MainActivity activity) {
        mActivity = activity;
    }

    @Override
    public void run() {
        ServerSocket serverSocket;
        Socket client = null;
        DataOutputStream dataOutputStream = null;
        try {
            Log.i(TAG, "Creating server socket");

            serverSocket = new ServerSocket(8888);

            while (true) {
                client = serverSocket.accept();
                Log.i(TAG, "Accepted a connection from " + client.toString());

                dataOutputStream = new DataOutputStream(client.getOutputStream());

                // Send owned groups to the client
                for (Group g : mActivity.getGroups()) {
                    if (g.getStatus() == GroupStatus.OWNED) {
                        JSONObject jsondata = g.getJSONData();
                        dataOutputStream.writeUTF(jsondata.toString());
                    }
                }

                dataOutputStream.close();
                client.close();
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (client != null) {
                try {
                    client.close();
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
        }
    }
}
