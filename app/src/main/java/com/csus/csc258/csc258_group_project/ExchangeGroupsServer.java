package com.csus.csc258.csc258_group_project;

import android.os.AsyncTask;
import android.util.Log;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * This server thread accepts connections on port 8888 and transmits the list of owned
 * groups to any client that connects
 * @author Ben White
 */
public class ExchangeGroupsServer extends AsyncTask<Void, Void, Void> {
    private MainActivity mActivity;
    private int mHostPort;
    private String mDeviceName;

    private static final String TAG = "ExchangeGroupsServer";

    public ExchangeGroupsServer(int hostPort, MainActivity activity, String deviceName) {
        mActivity = activity;
        mHostPort = hostPort;
        mDeviceName = deviceName;
    }

    @Override
    protected Void doInBackground(Void... params) {
        ServerSocket serverSocket;
        Socket client = null;
        DataOutputStream dataOutputStream = null;
        DataInputStream dataInputStream = null;
        try {
            Log.i(TAG, "Creating server socket");

            serverSocket = new ServerSocket(mHostPort);

            while(!isCancelled()) {
                client = serverSocket.accept();
                Log.i(TAG, "Accepted a connection from " + client.toString());

                dataOutputStream = new DataOutputStream(client.getOutputStream());
                dataInputStream = new DataInputStream(client.getInputStream());

                // Send the device name
                dataOutputStream.writeUTF(mDeviceName);
                dataOutputStream.flush();

                // Wait for confirmation
                if(dataInputStream.readUTF().equals(mActivity.getString(R.string.socket_client_received_deviceName))) {

                    // Send the client the files that are on the server
                    for (GroupFile gf : mActivity.getGroupFiles()) {
                        // Send the file name
                        dataOutputStream.writeUTF(gf.getFileName());
                        dataOutputStream.flush();

                        // Wait for confirmation
                        if (dataInputStream.readUTF().equals(mActivity.getString(R.string.socket_client_received_fileName))) {
                            // Send the file
                            InputStream inputStream = new FileInputStream(gf.getFileObject());
                            byte buf[] = new byte[1024];
                            int len;
                            while ((len = inputStream.read(buf)) != -1)
                                dataOutputStream.write(buf, 0, len);
                        } else
                            Log.e(TAG, "doInBackground: Didn't successfully send filename " + gf.getFileName());
                    }
                }

                dataOutputStream.close();
                dataInputStream.close();
                client.close();
            }
            serverSocket.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
        finally {
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
