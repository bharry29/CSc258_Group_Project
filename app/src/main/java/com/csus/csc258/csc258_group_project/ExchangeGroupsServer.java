package com.csus.csc258.csc258_group_project;

import android.os.AsyncTask;
import android.util.Log;
import org.json.JSONObject;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;

/**
 * This server thread accepts connections on port 8888 and transmits the list of owned
 * groups to any client that connects
 * @author Ben White
 */
public class ExchangeGroupsServer extends AsyncTask<Void, Void, Void> {
    private MainActivity mActivity;
    private int mHostPort;

    private static final String TAG = "ExchangeGroupsServer";

    public ExchangeGroupsServer(int hostPort, MainActivity activity) {
        mActivity = activity;
        mHostPort = hostPort;
    }

    @Override
    protected Void doInBackground(Void... params) {
        ServerSocket serverSocket;
        Socket client = null;
        DataOutputStream dataOutputStream = null;
        try {
            Log.i(TAG, "Creating server socket");

            serverSocket = new ServerSocket(mHostPort);

            while(!isCancelled()) {
                // TODO: How do we cancel this? One idea is to call serverSocket.close()
                // from another thread
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
            serverSocket.close();

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

        return null;
    }
}
