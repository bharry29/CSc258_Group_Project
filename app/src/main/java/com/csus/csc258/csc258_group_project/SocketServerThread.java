package com.csus.csc258.csc258_group_project;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ben on 3/6/2016.
 */
public class SocketServerThread extends Thread {
    private int mServerSocketPort = 8888;
    private List<String> mClientIPs = new ArrayList<>();

    private static final String TAG = "SocketServerThread";

    @Override
    public void run() {
        Socket client = null;
        DataInputStream dataInputStream = null;
        DataOutputStream dataOutputStream = null;

        try {
            Log.i(TAG, "Creating server socket");
            ServerSocket serverSocket = new ServerSocket(mServerSocketPort);

            while (true) {
                client = serverSocket.accept();
                dataInputStream = new DataInputStream(
                        client.getInputStream());
                dataOutputStream = new DataOutputStream(
                        client.getOutputStream());

                String messageFromClient, messageToClient, request;

                // Wait for client to send groups that they own

                // Send groups that the server owns

                //If no message sent from client, this code will block the program
                messageFromClient = dataInputStream.readUTF();

                final JSONObject jsondata;

                try {
                    jsondata = new JSONObject(messageFromClient);
                    request = jsondata.getString("request");

                    if (request.equals("")) {
                        String clientIPAddress = jsondata.getString("ipAddress");

                        // Add client IP to a list
                        mClientIPs.add(clientIPAddress);
                        messageToClient = "Connection Accepted";
                        dataOutputStream.writeUTF(messageToClient);
                    } else {
                        // There might be other queries, but as of now nothing.
                        dataOutputStream.flush();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e(TAG, "Unable to get request");
                    dataOutputStream.flush();
                }
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

            if (dataInputStream != null) {
                try {
                    dataInputStream.close();
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
